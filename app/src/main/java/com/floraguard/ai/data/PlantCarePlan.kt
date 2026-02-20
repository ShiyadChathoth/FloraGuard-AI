package com.floraguard.ai.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_care_plan")
data class PlantCarePlan(
    @PrimaryKey val diseaseName: String,
    val treatmentSteps: String,
    val soilComposition: String,
    val wateringLogic: String,
    val lightExposure: String,
    val nutrientGuide: String
)
