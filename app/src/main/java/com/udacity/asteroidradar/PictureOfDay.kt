package com.udacity.asteroidradar

import com.squareup.moshi.Json
import com.udacity.asteroidradar.database.CachedPictureOfDay
import com.udacity.asteroidradar.database.PictureOfDayDao

data class PictureOfDay(@Json(name = "media_type") val mediaType: String, val title: String,
                        val url: String)


fun PictureOfDay.asDatabaseModel(): CachedPictureOfDay {
    return com.udacity.asteroidradar.database.CachedPictureOfDay(
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}