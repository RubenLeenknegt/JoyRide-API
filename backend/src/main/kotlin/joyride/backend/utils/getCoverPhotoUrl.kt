package joyride.backend.utils

import joyride.backend.dao.PhotosEntity
import joyride.backend.dao.PhotosTable

fun getCoverPhotoUrl(carId: String): String? {
    val photos = PhotosEntity
        .find { PhotosTable.carId eq carId }
        .toList()

    return photos
        .firstOrNull { it.filePath.endsWith("1.webp") }
        ?.filePath
        ?: photos.randomOrNull()?.filePath
}