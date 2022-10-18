package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.RadarRepository
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.getDatabase
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val database = getDatabase(application)
    private val repository = RadarRepository(database)

    init{
        viewModelScope.launch {
            repository.refreshAsteroids()
            repository.refreshPictureOfDay()
        }
    }

    val asteroids: LiveData<List<Asteroid>> = repository.asteroids
    val pictureOfDay: LiveData<PictureOfDay> = repository.pictureOfDay

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    fun navigateToSelectedAsteroid(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }
    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}