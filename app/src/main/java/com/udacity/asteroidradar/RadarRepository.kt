package com.udacity.asteroidradar

import android.annotation.SuppressLint
import android.net.Network
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.RadarDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar

class RadarRepository(private val database: RadarDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it.asDomainModel()
        }

    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.pictureOfDayDao.getLastPicture()) {
            it?.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO)
        {
            try {
                val asteroids = parseAsteroidsJsonResult(
                    JSONObject(
                        AsteroidApi.retrofitService.getAsteroids(
                            API_KEY,
                            getTodayDate(),
                            getWeekendDate()
                        ).string()
                    )
                )
                database.asteroidDao.insertAll(*(asteroids.asDatabaseModel()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                database.pictureOfDayDao.insert(
                    AsteroidApi.retrofitService.getPictureOfDay(API_KEY).asDatabaseModel()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("NewApi", "SimpleDateFormat", "WeekBasedYear")
    private fun getWeekendDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        return SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT).format(calendar.time)
    }

    @SuppressLint("NewApi", "SimpleDateFormat", "WeekBasedYear")
    private fun getTodayDate(): String {
        return SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT).format(Calendar.getInstance().time)
    }
}