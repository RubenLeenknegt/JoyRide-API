package leafcar.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.util.toMap
import leafcar.backend.repository.CarRepository

/**
 * Ktor-routing extensie die HTTP-eindpunten rondom auto’s (Cars) registreert.
 *
 * Wat is een “Controller” in Ktor?
 * - In Ktor wordt routing vaak gebruikt als controller-laag: je definieert paden (routes)
 *   en HTTP-methoden (GET/POST/etc.) en koppelt die aan handlers.
 * - Deze functie voegt de `/cars`-routes toe aan een bestaande `Route`-boom. Zo blijft
 *   route-definitie modulair en testbaar.
 *
 * Afhankelijkheden en verantwoordelijkheid:
 * - `carRepository` wordt geïnjecteerd zodat de route handler(s) domeinobjecten kunnen
 *   ophalen zonder database-details te kennen (Repository-patroon).
 * - Serialisatie naar JSON gebeurt automatisch door Ktor i.c.m. Kotlinx Serialization
 *   wanneer je `call.respond(domeinObject)` gebruikt en de juiste ContentNegotiation
 *   plugin is geïnstalleerd.
 *
 * Eindpunten:
 * - GET `/cars` — geeft een lijst van `Car`-domeinobjecten terug (HTTP 200).
 *
 * Opmerkingen:
 * - Haal data op binnen de handler (niet erbuiten) zodat iedere request actuele data
 *   krijgt en eventuele request-specifieke context (logging, tracing) intact blijft.
 */

fun Route.carRouting(carRepository: CarRepository) {
    route("/cars") {

        // GET /cars?brand=BMW
        get {
            val params: Map<String, String> = call.request.queryParameters.toMap().mapValues { it.value.first() }
            val cars = carRepository.findWithFilters(params)
            call.respond(cars)
        }
//
//        // GET /cars/id/{id}
//        get("id/{id}") {
//            val id = call.parameters["id"]
//                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")
//
//            val car = carRepository.getById(id)
//            if (car.isEmpty()) call.respond(HttpStatusCode.NotFound, "No car with id $id")
//            else call.respond(HttpStatusCode.OK, car.first())
//        }
//
//        // GET /cars/brand/{brand}
//        get("brand/{brand}") {
//            val brand = call.parameters["brand"]
//                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing brand")
//
//            val cars = carRepository.getByBrand(brand)
//            if (cars.isEmpty()) call.respond(HttpStatusCode.NotFound, "No cars with brand $brand")
//            else call.respond(HttpStatusCode.OK, cars)
//        }
    }
}

//#### Tips en vervolgstappen
//- Validatie en foutafhandeling: bij toekomstige endpoints (bijv. `GET /cars/{id}` of `POST /cars`) documenteer je statuscodes (404/400/201) en foutformats in de KDoc van de handlerfunctie.
//- Paginering/filters: voor grote datasets kun je queryparameters ondersteunen (bijv. `?page=1&pageSize=20&brand=Toyota`). Documenteer de parameters en standaardwaarden in KDoc.
//- Versiebeheer: overweeg routes te namespacen (bijv. `/api/v1/cars`) en dit in de KDoc te vermelden.
//- Testbaarheid: omdat `carRouting` een pure extensie is met een geïnjecteerde repository, kun je in tests een fake/mock repository meegeven en via `testApplication {}` de responses asserten.
