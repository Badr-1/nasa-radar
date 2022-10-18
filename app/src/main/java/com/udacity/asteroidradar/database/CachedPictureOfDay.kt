package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.PictureOfDay

@Entity(tableName = "picture_of_day_table")
data class CachedPictureOfDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val mediaType: String,
    val title: String,
    val url: String
)

fun CachedPictureOfDay.asDomainModel(): PictureOfDay {
    return PictureOfDay(
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}

@Dao
interface PictureOfDayDao {

    // get last picture of the day
    @Query("SELECT * FROM picture_of_day_table ORDER BY id DESC LIMIT 1")
    fun getLastPicture(): LiveData<CachedPictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pictureOfDay: CachedPictureOfDay)
}