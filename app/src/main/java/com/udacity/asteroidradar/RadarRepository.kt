package com.udacity.asteroidradar


import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.getTodayDate
import com.udacity.asteroidradar.api.getWeekendDate
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.RadarDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

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


}