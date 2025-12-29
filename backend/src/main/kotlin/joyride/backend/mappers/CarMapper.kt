package joyride.backend.mappers

//import com.sun.tools.jdeprscan.Main.call
import joyride.backend.dao.CarEntity
import joyride.backend.dao.CarsTable
import joyride.backend.domain.Car
import joyride.backend.domain.Color
import joyride.backend.domain.FuelType
import joyride.backend.domain.TransmissionType
import joyride.backend.dto.request.CarCpkDataRequest
import joyride.backend.dto.request.CarCreateOrUpdateRequest
import joyride.backend.dto.request.CarLocationRequest
import joyride.backend.dto.request.CarTcoDataRequest
import joyride.backend.dto.response.CarCpkDataResponse
import joyride.backend.dto.response.CarListItemResponse
import joyride.backend.dto.response.CarTcoDataResponse
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID
import org.jetbrains.exposed.dao.id.EntityID
import kotlin.String

/**
 * Mapper object for converting between car-related entities, domain models, and DTOs.
 *
 * Provides functions to:
 * - Convert database entities ([CarEntity]) and DSL rows ([ResultRow]) to domain objects ([Car]).
 * - Convert domain objects to entities for persistence.
 * - Convert domain objects and entities to request/response DTOs for API communication.
 *
 * Ensures consistent transformation of car data across layers while handling enum/string conversions.
 */

object CarMapper {
    /** Converts a [CarEntity] to the domain [Car] object. */
    fun CarEntity.toDomain(): Car = Car(
        id = this.id.value,
        ownerId = this.ownerId.value,
        brand = this.brand,
        model = this.model,
        buildYear = this.buildYear,
        transmissionType = TransmissionType.valueOf(this.transmissionType), // string → enum
        color = Color.valueOf(this.color),
        fuelType = FuelType.valueOf(this.fuelType),
        length = this.length,
        width = this.width,
        seats = this.seats,
        isofixCompatible = this.isofixCompatible,
        phoneMount = this.phoneMount,
        luggageSpace = this.luggageSpace,
        parkingSensors = this.parkingSensors,
        locationX = this.locationX,
        locationY = this.locationY,
        licensePlate = this.licensePlate,
        pricePerDay = this.pricePerDay.toDouble(),
        purchasePrice = this.purchasePrice.toDouble(),
        residualValue = this.residualValue.toDouble(),
        usageYears = this.usageYears,
        annualKm = this.annualKm,
        energyCostPerKm = this.energyCostPerKm.toDouble(),
        maintenanceCostPerKm = this.maintenanceCostPerKm.toDouble(),
        averageConsumption = this.averageConsumption.toDouble()
    )

    /** Converts a database DSL [ResultRow] to a domain [Car] object. */
    fun toCar(row: ResultRow): Car = Car(
        id = row[CarsTable.id].value,
        ownerId = row[CarsTable.ownerId].value,
        brand = row[CarsTable.brand],
        model = row[CarsTable.model],
        buildYear = row[CarsTable.buildYear],
        color = Color.valueOf(row[CarsTable.color]),
        fuelType = FuelType.valueOf(row[CarsTable.fuelType]),
        length = row[CarsTable.length],
        width = row[CarsTable.width],
        seats = row[CarsTable.seats],
        isofixCompatible = row[CarsTable.isofixCompatible],
        phoneMount = row[CarsTable.phoneMount],
        luggageSpace = row[CarsTable.luggageSpace],
        parkingSensors = row[CarsTable.parkingSensors],
        transmissionType = TransmissionType.valueOf(row[CarsTable.transmissionType]),
        locationX = row[CarsTable.locationX],
        locationY = row[CarsTable.locationY],
        licensePlate = row[CarsTable.licensePlate],
        pricePerDay = row[CarsTable.pricePerDay].toDouble(),
        purchasePrice = row[CarsTable.purchasePrice].toDouble(),
        residualValue = row[CarsTable.residualValue].toDouble(),
        usageYears = row[CarsTable.usageYears],
        annualKm = row[CarsTable.annualKm],
        energyCostPerKm = row[CarsTable.energyCostPerKm].toDouble(),
        maintenanceCostPerKm = row[CarsTable.maintenanceCostPerKm].toDouble(),
        averageConsumption = row[CarsTable.averageConsumption].toDouble()
    )

    /** Converts a [CarEntity] to [CarLocationRequest] for location-only data. */
    fun CarEntity.toCarLocationRequest(): CarLocationRequest = CarLocationRequest(
        id = this.id.value,
        locationX = this.locationX,
        locationY = this.locationY
    )

    /** Converts a [CarCreateOrUpdateRequest] to a new [Car] domain object. */
    fun fromCarCreateRequest(request: CarCreateOrUpdateRequest, ownerId: String): Car = Car(
        id = UUID.randomUUID().toString(),
        ownerId = ownerId,
        brand = request.brand,
        model = request.model,
        buildYear = request.buildYear,
        transmissionType = TransmissionType.valueOf(request.transmissionType), // string → enum
        color = Color.valueOf(request.color),
        fuelType = FuelType.valueOf(request.fuelType),
        length = request.length,
        width = request.width,
        seats = request.seats,
        isofixCompatible = request.isofixCompatible,
        phoneMount = request.phoneMount,
        luggageSpace = request.luggageSpace,
        parkingSensors = request.parkingSensors,
        locationX = request.locationX,
        locationY = request.locationY,
        licensePlate = request.licensePlate,
        pricePerDay = request.pricePerDay.toDouble(),
        purchasePrice = request.purchasePrice.toDouble(),
        residualValue = request.residualValue.toDouble(),
        usageYears = request.usageYears,
        annualKm = request.annualKm,
        energyCostPerKm = request.energyCostPerKm.toDouble(),
        maintenanceCostPerKm = request.maintenanceCostPerKm.toDouble(),
        averageConsumption = request.averageConsumption.toDouble()
    )

    /** Converts a [CarCreateOrUpdateRequest] to an existing [Car] domain object by ID. */
    fun fromCarUpdateRequest(request: CarCreateOrUpdateRequest, id: String): Car = Car(
        id = id,
        ownerId = request.ownerId!!,
        brand = request.brand,
        model = request.model,
        buildYear = request.buildYear,
        transmissionType = TransmissionType.valueOf(request.transmissionType), // string → enum
        color = Color.valueOf(request.color),
        fuelType = FuelType.valueOf(request.fuelType),
        length = request.length,
        width = request.width,
        seats = request.seats,
        isofixCompatible = request.isofixCompatible,
        phoneMount = request.phoneMount,
        luggageSpace = request.luggageSpace,
        parkingSensors = request.parkingSensors,
        locationX = request.locationX,
        locationY = request.locationY,
        licensePlate = request.licensePlate,
        pricePerDay = request.pricePerDay.toDouble(),
        purchasePrice = request.purchasePrice.toDouble(),
        residualValue = request.residualValue.toDouble(),
        usageYears = request.usageYears,
        annualKm = request.annualKm,
        energyCostPerKm = request.energyCostPerKm.toDouble(),
        maintenanceCostPerKm = request.maintenanceCostPerKm.toDouble(),
        averageConsumption = request.averageConsumption.toDouble()
    )

    /** Updates a [CarEntity] with values from a [Car] domain object. */
    fun CarEntity.fromDomain(car: Car) {
        ownerId = EntityID(car.ownerId, CarsTable)
        brand = car.brand
        model = car.model
        buildYear = car.buildYear
        transmissionType = car.transmissionType.name
        color = car.color.name
        fuelType = car.fuelType.name
        length = car.length
        width = car.width
        seats = car.seats
        isofixCompatible = car.isofixCompatible
        phoneMount = car.phoneMount
        luggageSpace = car.luggageSpace
        parkingSensors = car.parkingSensors
        locationX = car.locationX
        locationY = car.locationY
        licensePlate = car.licensePlate
        pricePerDay = car.pricePerDay.toBigDecimal()
        purchasePrice = car.purchasePrice.toBigDecimal()
        residualValue = car.residualValue.toBigDecimal()
        usageYears = car.usageYears
        annualKm = car.annualKm
        energyCostPerKm = car.energyCostPerKm.toBigDecimal()
        maintenanceCostPerKm = car.maintenanceCostPerKm.toBigDecimal()
        averageConsumption = car.averageConsumption.toBigDecimal()
    }

    /** Converts a [CarEntity] to [CarTcoDataRequest] for TCO calculations. */
    fun CarEntity.toCarTcoDataRequest(): CarTcoDataRequest = CarTcoDataRequest(
        id = this.id.value,
        purchasePrice = this.purchasePrice.toDouble(),
        residualValue = this.residualValue.toDouble(),
        usageYears = this.usageYears,
        annualKm = this.annualKm,
        energyCostPerKm = this.energyCostPerKm.toDouble(),
        maintenanceCostPerKm = this.maintenanceCostPerKm.toDouble()
    )

    /** Converts a [CarTcoDataRequest] and calculation result to [CarTcoDataResponse]. */
    fun toCarTcoDataResponse(request: CarTcoDataRequest, result: Double): CarTcoDataResponse = CarTcoDataResponse(
        id = request.id,
        purchasePrice = request.purchasePrice,
        residualValue = request.residualValue,
        usageYears = request.usageYears,
        annualKm = request.annualKm,
        energyCostPerKm = request.energyCostPerKm,
        maintenanceCostPerKm = request.maintenanceCostPerKm,
        result = result
    )

    /** Converts a [CarEntity] to [CarCpkDataRequest] for CPK calculations. */
    fun CarEntity.toCarCpkDataRequest(): CarCpkDataRequest = CarCpkDataRequest(
        id = this.id.value,
        ownerId = this.ownerId.value,
        averageConsumption = this.averageConsumption.toDouble(),
        fuelType = this.fuelType
    )

    /** Converts a [CarCpkDataRequest], fuel price, and result to [CarCpkDataResponse]. */
    fun toCarCpkDataResponse(request: CarCpkDataRequest, fuelPrice: Double, result: Double): CarCpkDataResponse =
        CarCpkDataResponse(
            id = request.id,
            ownerId = request.ownerId,
            averageConsumption = request.averageConsumption,
            fuelType = request.fuelType,
            fuelPrice = fuelPrice,
            cpk = result
        )

    fun Car.toCarListItemResponse(photoUrl: String?) = CarListItemResponse(
        id = id,
        brand = brand,
        model = model,
        buildYear = buildYear,
        transmissionType = transmissionType.name,
        fuelType = fuelType.name,
        pricePerDay = pricePerDay,
        seats = seats,
        coverPhotoUrl = photoUrl
    )

}