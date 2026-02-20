#!/usr/bin/env python3
"""Train a plant-disease image classifier and export TensorFlow Lite assets.

Expected dataset layout:
dataset/
  Tomato_Early_Blight/
  Tomato_Late_Blight/
  Tomato_Leaf_Mold/
  Potato_Early_Blight/
  Pepper_Bacterial_Spot/
  Healthy/
"""

from __future__ import annotations

import argparse
from pathlib import Path
from typing import Sequence

try:
    import tensorflow as tf
    _TF_IMPORT_ERROR: Exception | None = None
except Exception as exc:  # pragma: no cover - import guard for local setup
    tf = None  # type: ignore[assignment]
    _TF_IMPORT_ERROR = exc


DEFAULT_LABELS = [
    "Tomato_Early_Blight",
    "Tomato_Late_Blight",
    "Tomato_Leaf_Mold",
    "Potato_Early_Blight",
    "Pepper_Bacterial_Spot",
    "Healthy",
]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Train FloraGuard model and export model.tflite + labels.txt"
    )
    parser.add_argument(
        "--data-dir",
        type=Path,
        default=Path("dataset"),
        help="Root folder containing one subfolder per class label.",
    )
    parser.add_argument(
        "--output-model",
        type=Path,
        default=Path("app/src/main/assets/model.tflite"),
        help="Output path for exported TFLite model.",
    )
    parser.add_argument(
        "--output-labels",
        type=Path,
        default=Path("app/src/main/assets/labels.txt"),
        help="Output path for labels file (one label per line).",
    )
    parser.add_argument(
        "--image-size",
        type=int,
        default=224,
        help="Square input image size (default: 224).",
    )
    parser.add_argument(
        "--batch-size",
        type=int,
        default=32,
        help="Batch size for training/validation.",
    )
    parser.add_argument(
        "--epochs",
        type=int,
        default=10,
        help="Base training epochs with frozen backbone.",
    )
    parser.add_argument(
        "--fine-tune-epochs",
        type=int,
        default=5,
        help="Fine-tuning epochs with partially unfrozen backbone.",
    )
    parser.add_argument(
        "--fine-tune-at",
        type=int,
        default=100,
        help="Layer index in MobileNetV2 to start unfreezing from.",
    )
    parser.add_argument(
        "--validation-split",
        type=float,
        default=0.2,
        help="Validation split ratio used by image_dataset_from_directory.",
    )
    parser.add_argument(
        "--seed",
        type=int,
        default=42,
        help="Random seed for split and shuffling.",
    )
    parser.add_argument(
        "--learning-rate",
        type=float,
        default=1e-3,
        help="Initial learning rate.",
    )
    parser.add_argument(
        "--fine-tune-learning-rate",
        type=float,
        default=1e-5,
        help="Learning rate used during fine-tuning.",
    )
    parser.add_argument(
        "--dynamic-range-quant",
        action="store_true",
        help="Enable dynamic range quantization during TFLite conversion.",
    )
    parser.add_argument(
        "--allow-custom-labels",
        action="store_true",
        help="Skip strict class-name check against FloraGuard labels.",
    )
    return parser.parse_args()


def assert_dataset_exists(path: Path) -> None:
    if not path.exists() or not path.is_dir():
        raise FileNotFoundError(f"Dataset directory not found: {path}")


def enforce_label_names(class_names: Sequence[str], allow_custom_labels: bool) -> None:
    if allow_custom_labels:
        return

    missing = sorted(set(DEFAULT_LABELS) - set(class_names))
    extra = sorted(set(class_names) - set(DEFAULT_LABELS))
    if missing or extra:
        details = []
        if missing:
            details.append(f"Missing classes: {missing}")
        if extra:
            details.append(f"Unexpected classes: {extra}")
        details.append(
            "Use --allow-custom-labels to bypass this check if you intentionally changed classes."
        )
        raise ValueError("\n".join(details))


def build_model(input_size: int, class_count: int, fine_tune_at: int) -> tuple[tf.keras.Model, tf.keras.Model]:
    inputs = tf.keras.Input(shape=(input_size, input_size, 3))
    x = tf.keras.layers.RandomFlip("horizontal")(inputs)
    x = tf.keras.layers.RandomRotation(0.05)(x)
    x = tf.keras.layers.RandomZoom(0.1)(x)
    x = tf.keras.layers.Rescaling(scale=1.0 / 127.5, offset=-1.0)(x)

    base_model = tf.keras.applications.MobileNetV2(
        input_shape=(input_size, input_size, 3),
        include_top=False,
        weights="imagenet",
    )
    base_model.trainable = False

    x = base_model(x, training=False)
    x = tf.keras.layers.GlobalAveragePooling2D()(x)
    x = tf.keras.layers.Dropout(0.25)(x)
    outputs = tf.keras.layers.Dense(class_count, activation="softmax")(x)
    model = tf.keras.Model(inputs=inputs, outputs=outputs)

    # Prepare default fine-tune state for later stage.
    for layer in base_model.layers[:fine_tune_at]:
        layer.trainable = False

    return model, base_model


def write_labels(path: Path, labels: Sequence[str]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(labels) + "\n", encoding="utf-8")


def export_tflite(model: tf.keras.Model, output_path: Path, dynamic_range_quant: bool) -> None:
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    if dynamic_range_quant:
        converter.optimizations = [tf.lite.Optimize.DEFAULT]
    tflite_model = converter.convert()
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_bytes(tflite_model)


def main() -> None:
    args = parse_args()

    if tf is None:
        raise SystemExit(
            "TensorFlow is not installed. Install it first with: pip install tensorflow"
        ) from _TF_IMPORT_ERROR

    assert_dataset_exists(args.data_dir)

    train_ds = tf.keras.utils.image_dataset_from_directory(
        args.data_dir,
        validation_split=args.validation_split,
        subset="training",
        seed=args.seed,
        image_size=(args.image_size, args.image_size),
        batch_size=args.batch_size,
    )
    val_ds = tf.keras.utils.image_dataset_from_directory(
        args.data_dir,
        validation_split=args.validation_split,
        subset="validation",
        seed=args.seed,
        image_size=(args.image_size, args.image_size),
        batch_size=args.batch_size,
    )

    class_names = list(train_ds.class_names)
    enforce_label_names(class_names, args.allow_custom_labels)

    autotune = tf.data.AUTOTUNE
    train_ds = train_ds.shuffle(1000).prefetch(autotune)
    val_ds = val_ds.prefetch(autotune)

    model, base_model = build_model(args.image_size, len(class_names), args.fine_tune_at)
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=args.learning_rate),
        loss="sparse_categorical_crossentropy",
        metrics=["accuracy"],
    )

    print(f"Training base model for {args.epochs} epochs...")
    model.fit(train_ds, validation_data=val_ds, epochs=args.epochs)

    if args.fine_tune_epochs > 0:
        print(f"Fine-tuning model for {args.fine_tune_epochs} epochs...")
        base_model.trainable = True
        for layer in base_model.layers[: args.fine_tune_at]:
            layer.trainable = False

        model.compile(
            optimizer=tf.keras.optimizers.Adam(
                learning_rate=args.fine_tune_learning_rate
            ),
            loss="sparse_categorical_crossentropy",
            metrics=["accuracy"],
        )
        model.fit(train_ds, validation_data=val_ds, epochs=args.fine_tune_epochs)

    write_labels(args.output_labels, class_names)
    export_tflite(model, args.output_model, args.dynamic_range_quant)

    print(f"Saved model: {args.output_model}")
    print(f"Saved labels: {args.output_labels}")
    print("Done.")


if __name__ == "__main__":
    main()
