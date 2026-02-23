package com.floraguard.ai.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_care_profile")
data class PlantCareProfile(
    @PrimaryKey val plantName: String,
    val searchKey: String,
    val soilComposition: String,
    val wateringLogic: String,
    val lightExposure: String,
    val nutrientGuide: String
)
