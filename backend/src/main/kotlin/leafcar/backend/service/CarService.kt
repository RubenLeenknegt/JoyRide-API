package leafcar.backend.service

import leafcar.backend.dao.CarEntity
import leafcar.backend.domain.Car
import leafcar.backend.dto.request.CarCreateRequest
import leafcar.backend.mappers.CarMapper
import leafcar.backend.mappers.CarMapper.toDomain
import leafcar.backend.repository.CarRepository

class CarService(
    private val carRepository: CarRepository
) {

    fun createCar(request: CarCreateRequest, ownerId: String): Car {
        val carEntity = CarMapper.toCarCreateRequest(request, ownerId)
        val savedEntity = carRepository.create(carEntity)
        return savedEntity.toDomain()
    }
}
