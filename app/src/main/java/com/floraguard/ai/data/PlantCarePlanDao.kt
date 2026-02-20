package com.floraguard.ai.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlantCarePlanDao {
    @Query("SELECT * FROM plant_care_plan WHERE LOWER(diseaseName) = LOWER(:diseaseName) LIMIT 1")
    suspend fun getByDiseaseName(diseaseName: String): PlantCarePlan?

    @Query("SELECT * FROM plant_care_plan WHERE LOWER(diseaseName) LIKE '%' || LOWER(:query) || '%' LIMIT 1")
    suspend fun searchByDiseaseName(query: String): PlantCarePlan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<PlantCarePlan>)

    @Query("SELECT COUNT(*) FROM plant_care_plan")
    suspend fun countPlans(): Int
}
