package com.floraguard.ai.data

fun defaultCarePlans(): List<PlantCarePlan> {
    return listOf(
        PlantCarePlan(
            diseaseName = "Tomato_Early_Blight",
            treatmentSteps = "1. Remove affected leaves.\n2. Apply copper-based fungicide every 7-10 days.\n3. Keep foliage dry and improve airflow.",
            soilComposition = "Well-drained loamy soil, pH 6.0-6.8, rich in organic matter.",
            wateringLogic = "Water deeply at root zone every 2-3 days; avoid overhead watering.",
            lightExposure = "Full sun (6-8 hours/day).",
            nutrientGuide = "Balanced NPK (10-10-10) every 2 weeks during active growth."
        ),
        PlantCarePlan(
            diseaseName = "Tomato_Late_Blight",
            treatmentSteps = "1. Remove infected plants immediately.\n2. Use protective fungicide on nearby plants.\n3. Sanitize tools after use.",
            soilComposition = "Sandy loam with good drainage, pH 6.2-6.8.",
            wateringLogic = "Morning watering only; keep leaves dry and reduce humidity.",
            lightExposure = "Full sun with strong air movement.",
            nutrientGuide = "Low nitrogen, higher potassium feed every 10-14 days."
        ),
        PlantCarePlan(
            diseaseName = "Tomato_Leaf_Mold",
            treatmentSteps = "1. Prune lower foliage.\n2. Increase spacing and ventilation.\n3. Apply sulfur or copper fungicide.",
            soilComposition = "Moist but well-drained soil, pH 6.0-6.5.",
            wateringLogic = "Irrigate soil directly when top 2 cm is dry.",
            lightExposure = "Full sun to partial sun with dry canopy conditions.",
            nutrientGuide = "Compost tea plus potassium-rich fertilizer every 2 weeks."
        ),
        PlantCarePlan(
            diseaseName = "Potato_Early_Blight",
            treatmentSteps = "1. Remove diseased leaves.\n2. Rotate crops annually.\n3. Use registered fungicide for solanaceous crops.",
            soilComposition = "Loose, fertile soil, pH 5.5-6.5.",
            wateringLogic = "Maintain evenly moist soil; avoid water stress swings.",
            lightExposure = "Full sun.",
            nutrientGuide = "Apply phosphorus and potassium at planting, then side-dress nitrogen lightly."
        ),
        PlantCarePlan(
            diseaseName = "Pepper_Bacterial_Spot",
            treatmentSteps = "1. Remove infected leaves/fruits.\n2. Use copper spray with bactericide support.\n3. Avoid handling plants when wet.",
            soilComposition = "Well-drained fertile soil, pH 6.0-6.8.",
            wateringLogic = "Drip irrigation preferred; water early morning only.",
            lightExposure = "Full sun, 6+ hours/day.",
            nutrientGuide = "Calcium-rich feed and balanced NPK every 2-3 weeks."
        ),
        PlantCarePlan(
            diseaseName = "Healthy",
            treatmentSteps = "No active disease detected. Continue preventive care.",
            soilComposition = "Match plant species needs; maintain pH in optimal range.",
            wateringLogic = "Water consistently based on species and growth stage.",
            lightExposure = "Provide recommended light duration for the plant variety.",
            nutrientGuide = "Use balanced fertilizer according to growth stage."
        )
    )
}
