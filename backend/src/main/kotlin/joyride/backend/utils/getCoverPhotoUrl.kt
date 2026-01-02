package joyride.backend.utils

import joyride.backend.dao.PhotosEntity
import joyride.backend.dao.PhotosTable
import org.jetbrains.exposed.sql.transactions.transaction

fun getCoverPhotoUrl(carId: String): String? =
    transaction {
        val photos = PhotosEntity
            .find { PhotosTable.carId eq carId }
            .toList()

        return@transaction photos.firstOrNull { it.filePath.endsWith("1.webp") }
            ?.filePath
            ?: photos.randomOrNull()?.filePath
    }
