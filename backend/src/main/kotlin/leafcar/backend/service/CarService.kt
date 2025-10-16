package leafcar.backend.service

import leafcar.backend.domain.Car
import leafcar.backend.dto.request.CarCreateOrUpdateRequest
import leafcar.backend.mappers.CarMapper
import leafcar.backend.mappers.CarMapper.toDomain
import leafcar.backend.repository.CarRepository

class CarService(
    private val carRepository: CarRepository
) {

    fun createCar(request: CarCreateOrUpdateRequest, ownerId: String): Car {
        val carEntity = CarMapper.toCarCreateRequest(request, ownerId)
        val savedEntity = carRepository.create(carEntity)
        return savedEntity.toDomain()
    }

    fun updateCar(request: CarCreateOrUpdateRequest, id: String): Car {
        val carEntity = CarMapper.toCarUpdateRequest(request, id)
        val savedEntity = carRepository.update(carEntity)
        return savedEntity.toDomain()
    }

    fun deleteCar(id: String): Boolean {
        return try {
            carRepository.delete(id)
            true
        } catch (e: Exception) {
            false
        }
    }

}
