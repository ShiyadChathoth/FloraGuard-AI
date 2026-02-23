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

fun defaultPlantProfiles(): List<PlantCareProfile> {
    return listOf(
        PlantCareProfile(
            plantName = "Aloe Vera",
            searchKey = plantSearchKey("Aloe Vera"),
            soilComposition = "Sandy soil or a premixed cactus potting medium.",
            wateringLogic = "Water thoroughly and allow soil to dry completely before watering again; reduce in winter.",
            lightExposure = "Bright, indirect sunlight; avoid harsh direct sun.",
            nutrientGuide = "No fertilizer is usually required."
        ),
        PlantCareProfile(
            plantName = "Jade Plant",
            searchKey = plantSearchKey("Jade Plant"),
            soilComposition = "Well-drained soil; cactus mix or potting soil amended with sand/perlite.",
            wateringLogic = "Water when soil is dry; less is more and avoid overwatering.",
            lightExposure = "At least six hours of indirect light daily; brighter light helps.",
            nutrientGuide = "Balanced fertilizer once a year in early spring."
        ),
        PlantCareProfile(
            plantName = "Pothos",
            searchKey = plantSearchKey("Pothos"),
            soilComposition = "Well-drained potting soil.",
            wateringLogic = "Water thoroughly, then let soil dry between waterings; avoid constant dampness.",
            lightExposure = "Bright, indirect light; tolerates low light and fluorescent light.",
            nutrientGuide = "Balanced fertilizer every two months; skip feeding in winter."
        ),
        PlantCareProfile(
            plantName = "Peace Lily",
            searchKey = plantSearchKey("Peace Lily"),
            soilComposition = "Well-drained potting soil.",
            wateringLogic = "Keep soil moist but not soggy; let it dry slightly between waterings.",
            lightExposure = "Bright, indirect light.",
            nutrientGuide = "Fertilize in spring and summer using an organic fertilizer."
        ),
        PlantCareProfile(
            plantName = "Spider Plant",
            searchKey = plantSearchKey("Spider Plant"),
            soilComposition = "General purpose potting soil.",
            wateringLogic = "Water about once a week and allow soil to dry between waterings.",
            lightExposure = "Indirect to moderate light.",
            nutrientGuide = "Fertilize every three to four months."
        ),
        PlantCareProfile(
            plantName = "Tomato",
            searchKey = plantSearchKey("Tomato"),
            soilComposition = "Loose, well-drained loamy soil rich in organic matter.",
            wateringLogic = "Water deeply and consistently; keep soil evenly moist, not soggy.",
            lightExposure = "Full sun (6-8+ hours daily).",
            nutrientGuide = "Balanced fertilizer early; switch to higher phosphorus/potassium during flowering and fruiting."
        ),
        PlantCareProfile(
            plantName = "Tomato Plant",
            searchKey = plantSearchKey("Tomato Plant"),
            soilComposition = "Loose, well-drained loamy soil rich in organic matter.",
            wateringLogic = "Water deeply and consistently; keep soil evenly moist, not soggy.",
            lightExposure = "Full sun (6-8+ hours daily).",
            nutrientGuide = "Balanced fertilizer early; switch to higher phosphorus/potassium during flowering and fruiting."
        ),
        PlantCareProfile(
            plantName = "Pepper",
            searchKey = plantSearchKey("Pepper"),
            soilComposition = "Well-drained, fertile soil with organic matter.",
            wateringLogic = "Water deeply when the top layer dries; avoid waterlogging.",
            lightExposure = "Full sun (6-8+ hours daily).",
            nutrientGuide = "Balanced fertilizer; avoid excess nitrogen once flowering starts."
        ),
        PlantCareProfile(
            plantName = "Bell Pepper",
            searchKey = plantSearchKey("Bell Pepper"),
            soilComposition = "Well-drained, fertile soil with organic matter.",
            wateringLogic = "Water deeply when the top layer dries; avoid waterlogging.",
            lightExposure = "Full sun (6-8+ hours daily).",
            nutrientGuide = "Balanced fertilizer; avoid excess nitrogen once flowering starts."
        ),
        PlantCareProfile(
            plantName = "Chili Pepper",
            searchKey = plantSearchKey("Chili Pepper"),
            soilComposition = "Well-drained, fertile soil with organic matter.",
            wateringLogic = "Water deeply when the top layer dries; avoid waterlogging.",
            lightExposure = "Full sun (6-8+ hours daily).",
            nutrientGuide = "Balanced fertilizer; avoid excess nitrogen once flowering starts."
        ),
        PlantCareProfile(
            plantName = "Chilli Pepper",
            searchKey = plantSearchKey("Chilli Pepper"),
            soilComposition = "Well-drained, fertile soil with organic matter.",
            wateringLogic = "Water deeply when the top layer dries; avoid waterlogging.",
            lightExposure = "Full sun (6-8+ hours daily).",
            nutrientGuide = "Balanced fertilizer; avoid excess nitrogen once flowering starts."
        ),
        PlantCareProfile(
            plantName = "Potato",
            searchKey = plantSearchKey("Potato"),
            soilComposition = "Loose, well-drained soil; avoid compacted clay.",
            wateringLogic = "Keep soil evenly moist; avoid letting it dry out during tuber formation.",
            lightExposure = "Full sun.",
            nutrientGuide = "Moderate nitrogen; ensure adequate phosphorus and potassium."
        ),
        PlantCareProfile(
            plantName = "Potato Plant",
            searchKey = plantSearchKey("Potato Plant"),
            soilComposition = "Loose, well-drained soil; avoid compacted clay.",
            wateringLogic = "Keep soil evenly moist; avoid letting it dry out during tuber formation.",
            lightExposure = "Full sun.",
            nutrientGuide = "Moderate nitrogen; ensure adequate phosphorus and potassium."
        ),
        PlantCareProfile(
            plantName = "Sunflower",
            searchKey = plantSearchKey("Sunflower"),
            soilComposition = "Loose, well-drained soil with organic matter.",
            wateringLogic = "Water regularly, especially during establishment; avoid waterlogging.",
            lightExposure = "Full sun (6-8+ hours daily).",
            nutrientGuide = "Balanced fertilizer at planting; avoid excess nitrogen."
        )
    )
}

private fun plantSearchKey(name: String): String {
    return name.lowercase().replace(Regex("[^a-z0-9]"), "")
}
