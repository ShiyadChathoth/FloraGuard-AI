package com.floraguard.ai.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlantCareProfileDao {
    @Query("SELECT * FROM plant_care_profile WHERE plantName = :plantName LIMIT 1")
    suspend fun getByPlantName(plantName: String): PlantCareProfile?

    @Query("SELECT * FROM plant_care_profile WHERE searchKey = :searchKey LIMIT 1")
    suspend fun getBySearchKey(searchKey: String): PlantCareProfile?

    @Query("SELECT * FROM plant_care_profile WHERE plantName LIKE '%' || :query || '%' LIMIT 1")
    suspend fun searchByPlantName(query: String): PlantCareProfile?

    @Query("SELECT COUNT(*) FROM plant_care_profile")
    suspend fun countProfiles(): Int

    @Query("SELECT plantName FROM plant_care_profile ORDER BY plantName")
    suspend fun getAllPlantNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<PlantCareProfile>)
}
