package com.floraguard.ai.data

class PlantCareRepository(
    private val plantCarePlanDao: PlantCarePlanDao,
    private val plantCareProfileDao: PlantCareProfileDao
) {
    suspend fun ensureSeedData() {
        if (plantCarePlanDao.countPlans() == 0) {
            plantCarePlanDao.insertAll(defaultCarePlans())
        }
        if (plantCareProfileDao.countProfiles() == 0) {
            plantCareProfileDao.insertAll(defaultPlantProfiles())
        }
    }

    suspend fun getPlanForDisease(diseaseName: String): PlantCarePlan? {
        val query = diseaseName.trim()
        if (query.isEmpty()) {
            return null
        }

        return plantCarePlanDao.getByDiseaseName(query)
            ?: plantCarePlanDao.getByDiseaseName(query.replace(" ", "_"))
            ?: plantCarePlanDao.searchByDiseaseName(query)
    }

    suspend fun getProfileForPlant(plantName: String): PlantCareProfile? {
        val query = plantName.trim()
        if (query.isEmpty()) {
            return null
        }
        val searchKey = normalizePlantName(query)
        return plantCareProfileDao.getBySearchKey(searchKey)
            ?: plantCareProfileDao.getByPlantName(query)
            ?: plantCareProfileDao.searchByPlantName(query)
    }

    private fun normalizePlantName(input: String): String {
        return input.lowercase()
            .replace(Regex("[^a-z0-9]"), "")
    }

    suspend fun getAllPlantNames(): List<String> {
        return plantCareProfileDao.getAllPlantNames()
    }
}
