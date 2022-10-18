package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    init {
        loadPictureOfDay()
        loadAsteroids()
    }

    private fun loadAsteroids() {
        viewModelScope.launch {
            try {
                val start_date = Date(System.currentTimeMillis())
                val end_date =
                    Date(System.currentTimeMillis() + Constants.DEFAULT_END_DATE_DAYS * 24 * 60 * 60 * 1000)
                val start_date_formatted =
                    SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT).format(start_date)
                val end_date_formatted =
                    SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT).format(end_date)
                _asteroids.value = parseAsteroidsJsonResult(
                    JSONObject(
                        AsteroidApi.retrofitService.getAsteroids(
                            start_date_formatted,
                            end_date_formatted,
                            Constants.API_KEY

                        ).string()
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = AsteroidApi.retrofitService.getPictureOfDay(Constants.API_KEY)
            } catch (e: Exception) {
                _pictureOfDay.value = PictureOfDay("", "", "")
            }
        }
    }

    fun displayPropertyDetails(asteroid: Asteroid) {
        TODO("Not yet implemented")
    }
}