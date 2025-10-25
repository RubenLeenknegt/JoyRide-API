package leafcar.backend.services

import leafcar.backend.domain.Car
import leafcar.backend.dto.request.CarCreateOrUpdateRequest
import leafcar.backend.dto.request.CarTcoDataRequest
import leafcar.backend.dto.response.CarCpkDataResponse
import leafcar.backend.dto.response.CarTcoDataResponse
import leafcar.backend.mappers.CarMapper
import leafcar.backend.mappers.CarMapper.toDomain
import leafcar.backend.repository.CarRepository

/**
 * Service layer for managing car operations.
 *
 * Handles business logic for creating, updating, deleting cars,
 * as well as calculating Total Cost of Ownership (TCO) and
 * Cost Per Kilometer (CPK) metrics.
 *
 * Interacts with [CarRepository] for persistence and uses [CarMapper]
 * for conversions between entities, domain models, and DTOs.
 */

class CarService(
    private val carRepository: CarRepository
) {

    // APP-UC-03: Auto beheren
    /** Creates a new car for the given owner. Returns the created domain object or null on failure. */
    fun createCar(request: CarCreateOrUpdateRequest, ownerId: String): Car? {
        val car = CarMapper.fromCarCreateRequest(request, ownerId)
        val createdCar = carRepository.create(car)

        if (createdCar == null)
            return null
        else
            return createdCar.toDomain()
    }
    /** Updates an existing car by ID. Returns the updated domain object or null on failure. */
    // APP-UC-03: Auto beheren
    fun updateCar(request: CarCreateOrUpdateRequest, id: String): Car? {
        val car = CarMapper.fromCarUpdateRequest(request, id)
        val updatedCar = carRepository.update(car)

        if (updatedCar == null)
            return null
        else
            return updatedCar.toDomain()
    }

    /** Deletes a car by ID. Returns true if deletion succeeds, false otherwise. */
    // APP-UC-03: Auto beheren
    fun deleteCar(id: String): Boolean {
        return try {
            carRepository.delete(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Calculates the Total Cost of Ownership (TCO) for a car and returns a response DTO. */
    fun getTco(id: String): CarTcoDataResponse? {
        val tcoData = carRepository.getTcoData(id) ?: return null

        return try {
            val depreciation = tcoData.purchasePrice.toBigDecimal() - tcoData.residualValue.toBigDecimal()
            val runningCosts = tcoData.annualKm.toBigDecimal() * tcoData.usageYears.toBigDecimal() *
                    (tcoData.energyCostPerKm.toBigDecimal() + tcoData.maintenanceCostPerKm.toBigDecimal())
            val result = depreciation + runningCosts
            return CarMapper.toCarTcoDataResponse(tcoData, result.toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Calculates the Cost Per Kilometer (CPK) for a car and returns a response DTO. */
    fun getCpk(id: String): CarCpkDataResponse? {
        val cpkData = carRepository.getCpkData(id) ?: return null

        return try {
            val fuelPrice = when (cpkData.fuelType) {
                "ICE" -> 2.0
                "BEV" -> 1.5
                "FCEV" -> 1.25
                else -> return null
            }
            val result = (cpkData.averageConsumption / 100) * fuelPrice
            return CarMapper.toCarCpkDataResponse(cpkData, fuelPrice, result)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


