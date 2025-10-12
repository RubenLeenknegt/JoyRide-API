package leafcar.backend.service

import leafcar.backend.dao.CarEntity
import leafcar.backend.dto.request.CarCreateRequest
import leafcar.backend.mappers.CarMapper
import leafcar.backend.repository.CarRepository

class CarService(
    private val carRepository: CarRepository
) {

    fun createCar(request: CarCreateRequest, ownerId: String): CarEntity {
        val car = CarMapper.toCarCreateRequest(request, ownerId)
        // eventueel extra validaties of rules hier
        return carRepository.create(car)
    }
}
