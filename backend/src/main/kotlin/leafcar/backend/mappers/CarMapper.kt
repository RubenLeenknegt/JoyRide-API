package leafcar.backend.mappers

import leafcar.backend.dao.CarEntity
import leafcar.backend.dao.CarsTable
import leafcar.backend.domain.Car
import leafcar.backend.domain.Color
import leafcar.backend.domain.FuelType
import leafcar.backend.domain.TransmissionType
import leafcar.backend.dto.request.CarCreateOrUpdateRequest
import leafcar.backend.dto.request.CarLocationRequest
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

object CarMapper {
    // Extension function to convert CarEntity to Car object
    fun CarEntity.toDomain(): Car = Car(
        id = this.id.value,
        ownerId = this.ownerId,
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
        maintenanceCostPerKm = this.maintenanceCostPerKm.toDouble()
    )

    // Function to convert DSL row to Car object
    fun toCar(row: ResultRow): Car = Car(
        id = row[CarsTable.id].value,
        ownerId = row[CarsTable.ownerId],
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
        maintenanceCostPerKm = row[CarsTable.maintenanceCostPerKm].toDouble()
    )

    fun CarEntity.toCarLocationRequest(): CarLocationRequest = CarLocationRequest(
        id = this.id.value,
        locationX = this.locationX,
        locationY = this.locationY
    )

    fun fromCarCreateOrUpdateRequest(request: CarCreateOrUpdateRequest, ownerId: String): Car = Car(
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
    )


    fun toCarUpdateRequest(request: CarCreateOrUpdateRequest, id: String): Car = Car(
        id = id,
        ownerId = request.ownerId!!, // waarom moeten deze uitroeptekens?
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
    )

    fun CarEntity.fromDomain(car: Car) {
        ownerId = car.ownerId
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
    }
}