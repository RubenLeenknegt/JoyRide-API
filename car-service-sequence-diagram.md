# Car Service Sequence Diagrams

This document contains sequence diagrams for the Car service operations in the LeafCar backend.

## 1. GET /cars (with filters)

```plantuml
@startuml
participant Client
participant CarController
participant CarRepository
participant Database

Client -> CarController: GET /cars?brand=BMW
activate CarController

CarController -> CarController: Extract query parameters\n(toMap())
CarController -> CarRepository: findWithFilters(params)
activate CarRepository

CarRepository -> Database: SELECT * FROM cars\nWHERE brand = 'BMW'
activate Database
Database --> CarRepository: ResultRows
deactivate Database

CarRepository -> CarRepository: Map ResultRows to Car objects\nusing CarMapper.toCar()
CarRepository --> CarController: List<Car>
deactivate CarRepository

CarController --> Client: HTTP 200 + List<Car>
deactivate CarController
@enduml
```

## 2. GET /cars/id/{id}

```plantuml
@startuml
participant Client
participant CarController
participant CarRepository
participant CarEntity
participant Database

Client -> CarController: GET /cars/id/123
activate CarController

CarController -> CarController: Extract path parameter 'id'

alt id is missing
    CarController --> Client: HTTP 400 "Missing id"
else id provided
    CarController -> CarRepository: getById(id)
    activate CarRepository
    
    CarRepository -> CarEntity: findById(id)
    activate CarEntity
    CarEntity -> Database: SELECT * FROM cars\nWHERE id = '123'
    activate Database
    Database --> CarEntity: ResultRow or null
    deactivate Database
    CarEntity --> CarRepository: CarEntity or null
    deactivate CarEntity
    
    alt CarEntity found
        CarRepository -> CarRepository: Convert CarEntity.toDomain()
        CarRepository --> CarController: List<Car> (single item)
        deactivate CarRepository
        CarController --> Client: HTTP 200 + Car
    else CarEntity not found
        CarRepository --> CarController: Empty list
        deactivate CarRepository
        CarController --> Client: HTTP 404 "No car with id 123"
    end
end
deactivate CarController
@enduml
```

## 3. GET /cars/location

```plantuml
@startuml
participant Client
participant CarController
participant CarRepository
participant CarEntity
participant CarMapper
participant Database

Client -> CarController: GET /cars/location
activate CarController

CarController -> CarRepository: getLocations()
activate CarRepository

CarRepository -> CarEntity: all()
activate CarEntity
CarEntity -> Database: SELECT * FROM cars
activate Database
Database --> CarEntity: All CarEntity records
deactivate Database
CarEntity --> CarRepository: List<CarEntity>
deactivate CarEntity

CarRepository -> CarMapper: toCarLocationRequest()\nfor each CarEntity
activate CarMapper
CarMapper --> CarRepository: List<CarLocationRequest>
deactivate CarMapper

CarRepository --> CarController: List<CarLocationRequest>
deactivate CarRepository

CarController --> Client: HTTP 200 + List<CarLocationRequest>
deactivate CarController
@enduml
```

## 4. POST /cars (Create Car)

```plantuml
@startuml
participant Client
participant CarController
participant CarService
participant CarMapper
participant CarRepository
participant CarEntity
participant Database

Client -> CarController: POST /cars\n+ CarCreateRequest body
activate CarController

CarController -> CarController: Receive CarCreateRequest
CarController -> CarController: Set ownerId (hardcoded)
CarController -> CarService: new CarService(carRepository)
activate CarService

CarController -> CarService: createCar(request, ownerId)

CarService -> CarMapper: toCarCreateRequest(request, ownerId)
activate CarMapper
CarMapper -> CarMapper: Generate UUID for car ID
CarMapper -> CarMapper: Convert strings to enums\n(TransmissionType, Color, FuelType)
CarMapper --> CarService: Car domain object
deactivate CarMapper

CarService -> CarRepository: create(car)
activate CarRepository

CarRepository -> CarEntity: new(car.id) { ... }
activate CarEntity
CarEntity -> CarEntity: Set all properties from\nCar domain object
CarEntity -> Database: INSERT INTO cars\n(id, ownerId, brand, model, ...)
activate Database
Database --> CarEntity: Inserted record
deactivate Database
CarEntity --> CarRepository: Saved CarEntity
deactivate CarEntity

CarRepository --> CarService: CarEntity
deactivate CarRepository

CarService -> CarMapper: savedEntity.toDomain()
activate CarMapper
CarMapper -> CarMapper: Convert CarEntity to Car\n(BigDecimal to Double, String to Enum)
CarMapper --> CarService: Car domain object
deactivate CarMapper

CarService --> CarController: Car
deactivate CarService

CarController --> Client: HTTP 201 + Created Car
deactivate CarController
@enduml
```

## Architecture Overview

```plantuml
@startuml
package "HTTP Layer" {
    [CarController] as Controller
}

package "Service Layer" {
    [CarService] as Service
}

package "Repository Layer" {
    [CarRepository] as Repository
}

package "Data Access Layer" {
    [CarEntity] as Entity
    [CarsTable] as Table
}

package "Domain Layer" {
    [Car] as Domain
    [CarCreateRequest] as Request
    [CarLocationRequest] as LocationReq
}

package "Mapping Layer" {
    [CarMapper] as Mapper
}

package "Database" {
    database "PostgreSQL" as DB
}

Controller --> Service : uses
Service --> Repository : uses
Repository --> Entity : uses
Entity --> Table : maps to
Entity --> DB : queries
Controller --> Repository : direct access (GET operations)
Mapper --> Domain : converts to/from
Mapper --> Entity : converts to/from
Mapper --> Request : converts from
Service --> Mapper : uses
Repository --> Mapper : uses

note right of Controller
  - GET /cars (with filters)
  - GET /cars/id/{id}
  - GET /cars/location  
  - POST /cars
end note

note right of Service
  Only used for complex
  business logic operations
  (currently just createCar)
end note

note right of Repository
  Data access operations:
  - findWithFilters()
  - getById()
  - getLocations()
  - create()
end note
@enduml
```

## Key Components Interaction Summary

1. **CarController**: Entry point handling HTTP requests and responses
2. **CarService**: Business logic layer (used only for car creation)
3. **CarRepository**: Data access layer with CRUD operations
4. **CarEntity**: Database entity using Exposed ORM
5. **CarMapper**: Converts between different object types (Entity ↔ Domain ↔ DTO)
6. **Domain Objects**:
    - `Car`: Core domain model
    - `CarCreateRequest`: Input DTO for car creation
    - `CarLocationRequest`: Output DTO for location data

## Data Flow Patterns

- **Simple Queries**: Controller → Repository → Database (bypasses Service layer)
- **Complex Operations**: Controller → Service → Repository → Database
- **Mapping**: Always done through CarMapper for type conversions
- **Error Handling**: HTTP status codes returned based on operation results