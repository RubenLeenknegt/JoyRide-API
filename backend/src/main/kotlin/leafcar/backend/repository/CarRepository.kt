package leafcar.backend.repository

import leafcar.backend.dao.CarEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Car
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Repository voor het ophalen en (in de toekomst) opslaan van `Car`-domeinobjecten.
 *
 * Wat is een Repository?
 * - Een Repository is een laag (patroon) die toegang tot data afschermt van de rest van de applicatie.
 *   Code in de service- of use‑case‑laag spreekt tegen een Repository in termen van domeinobjecten (zoals `Car`),
 *   zonder te weten hoe of waar de data werkelijk wordt opgeslagen (database, API, file, enz.).
 * - Voordelen: scheiding van verantwoordelijkheden, makkelijker testen (mocken/stubben), en flexibiliteit om de
 *   onderliggende opslag te vervangen zonder de rest van de code aan te passen.
 *
 * Implementatiedetails van deze `CarRepository`:
 * - Gebruikt JetBrains Exposed (DAO) onder de motorkap: `CarEntity` is de database-entity.
 * - Binnen een database-transactie (`transaction { ... }`) worden entities opgehaald en via `toDomain()` gemapt
 *   naar het domeinmodel `Car`. Zo lekt databasekennis niet door naar het domein.
 * - Deze klasse is bewust klein gehouden; extra methoden zoals `getById`, `create`, `update`, `delete` kunnen
 *   later worden toegevoegd.
 */
class CarRepository {

    /**
     * Haalt alle auto’s op uit de database en geeft ze terug als een lijst van `Car`-domeinobjecten.
     *
     * - Transactie: de query en mapping worden binnen een Exposed `transaction` uitgevoerd.
     * - Mapping: elke `CarEntity` wordt met `toDomain()` vertaald naar een `Car` zodat de rest van de applicatie
     *   geen directe afhankelijkheid heeft van database-entities.
     */
    fun getAll(): List<Car> = transaction {
        CarEntity.all().map { it.toDomain() }
    }
}

//#### Tips en vervolgstappen
//- Overweeg om een interface te introduceren (bijv. `interface CarRepository`) en deze implementatie `ExposedCarRepository` te noemen. Dat maakt testen (mocks) en wisselen van opslagtechnologie eenvoudiger.
//- Veelgebruikte extra methoden:
//- `fun getById(id: String): Car?`
//- `fun create(car: Car): Car`
//- `fun update(id: String, update: Car): Boolean`
//- `fun delete(id: String): Boolean`
//- Als je soft deletes of paginering nodig hebt, documenteer die contracten ook in de KDoc zodat het gedrag duidelijk is voor consumers van de repository.
//- Als je buiten de repository transactiebeheer wilt doen (bijv. in de service‑laag), kun je Exposed’s `transaction` daar plaatsen en de repository puur de query/mapping laten doen; documenteer dan in de KDoc waar de transactiegrenzen liggen.