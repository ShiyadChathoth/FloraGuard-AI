package com.floraguard.ai.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlantCarePlan::class, PlantCareProfile::class], version = 2, exportSchema = false)
abstract class FloraGuardDatabase : RoomDatabase() {
    abstract fun plantCarePlanDao(): PlantCarePlanDao
    abstract fun plantCareProfileDao(): PlantCareProfileDao

    companion object {
        @Volatile
        private var INSTANCE: FloraGuardDatabase? = null

        fun getInstance(context: Context): FloraGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FloraGuardDatabase::class.java,
                    "flora_guard.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
