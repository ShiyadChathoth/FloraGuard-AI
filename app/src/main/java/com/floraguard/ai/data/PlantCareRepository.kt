package com.floraguard.ai.data

class PlantCareRepository(
    private val plantCarePlanDao: PlantCarePlanDao
) {
    suspend fun ensureSeedData() {
        if (plantCarePlanDao.countPlans() == 0) {
            plantCarePlanDao.insertAll(defaultCarePlans())
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
}
