package leafcar.backend.service

import leafcar.backend.domain.Car
import leafcar.backend.dto.request.CarCreateOrUpdateRequest
import leafcar.backend.mappers.CarMapper
import leafcar.backend.mappers.CarMapper.toDomain
import leafcar.backend.repository.CarRepository

class CarService(
    private val carRepository: CarRepository
) {

    // APP-UC-03: Auto beheren
    fun createCar(request: CarCreateOrUpdateRequest, ownerId: String): Car? {
        val car = CarMapper.fromCarCreateRequest(request, ownerId)
        val createdCar = carRepository.create(car)

        if (createdCar == null)
            return null
        else
            return createdCar.toDomain()
    }

    // APP-UC-03: Auto beheren
    fun updateCar(request: CarCreateOrUpdateRequest, id: String): Car? {
        val car = CarMapper.fromCarUpdateRequest(request, id)
        val updatedCar = carRepository.update(car)

        if (updatedCar == null)
            return null
        else
            return updatedCar.toDomain()
    }

    // APP-UC-03: Auto beheren
    fun deleteCar(id: String): Boolean {
        return try {
            carRepository.delete(id)
            true
        } catch (e: Exception) {
            false
        }
    }

}
