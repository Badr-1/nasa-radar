package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.PictureOfDay

@Entity(tableName = "picture_of_day_table")
data class CachedPictureOfDay(
    @PrimaryKey
    val url: String,
    val mediaType: String,
    val title: String,
    val date: String
)

fun CachedPictureOfDay.asDomainModel(): PictureOfDay {
    return PictureOfDay(
        mediaType = this.mediaType,
        title = this.title,
        url = this.url,
        date = this.date
    )
}

@Dao
interface PictureOfDayDao {

    // get last picture of the day
    @Query("SELECT * FROM picture_of_day_table ORDER BY date DESC LIMIT 1")
    fun getLastPicture(): LiveData<CachedPictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pictureOfDay: CachedPictureOfDay)
}