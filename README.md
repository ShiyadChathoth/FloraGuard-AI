# FloraGuard AI (Offline Android MVP)

This repository now contains a complete Android/Kotlin starter implementation of **FloraGuard AI** based on the master document.

## Implemented Core Features

- Jetpack Compose app with Home, Camera, and Results screens.
- CameraX integration for live preview and leaf image capture.
- Gallery image upload support.
- Offline TensorFlow Lite inference helper (`TFLiteClassifier`) with:
  - Resize to `224x224`
  - RGB normalization (`/255.0f`)
  - Label mapping from `labels.txt`
- Room database (`PlantCarePlan`) with DAO + repository for offline care-plan lookup.
- Pre-seeded offline care plans for common diseases.
- Results screen showing diagnosis + treatment + soil/watering/light/nutrient guidance.

## Important Setup

1. Add your trained model file to:
   - `app/src/main/assets/model.tflite` (recommended)
   - Any `*.tflite` filename in `app/src/main/assets` also works (auto-detected)
2. Update labels if needed in:
   - `app/src/main/assets/labels.txt`

If `model.tflite` is missing, the app still runs and manual lookup works, but AI diagnosis is disabled.

## Train Your Model

1. Install Python dependency:

```bash
pip install tensorflow
```

2. Prepare dataset folders (class names should match app care-plan names):

```text
dataset/
  Tomato_Early_Blight/
  Tomato_Late_Blight/
  Tomato_Leaf_Mold/
  Potato_Early_Blight/
  Pepper_Bacterial_Spot/
  Healthy/
```

3. Run training and export assets directly into Android app:

```bash
python3 train_model.py --data-dir dataset
```

Optional:

```bash
python3 train_model.py --data-dir dataset --epochs 15 --fine-tune-epochs 8 --dynamic-range-quant
```

The script writes:
- `app/src/main/assets/model.tflite`
- `app/src/main/assets/labels.txt`

## Build

From terminal:

```bash
./gradlew :app:assembleDebug
```

Or open the folder in Android Studio and sync Gradle.

Minimum SDK: 26
Compile SDK: 35
