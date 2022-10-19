package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.RadarRepository
import com.udacity.asteroidradar.api.getTodayDate
import com.udacity.asteroidradar.database.getDatabase
import kotlinx.coroutines.launch

enum class AsteroidFilter { SHOW_WEEK, SHOW_TODAY, SHOW_SAVED }
enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status
    private val database = getDatabase(application)
    private val repository = RadarRepository(database)
    private val _filter = MutableLiveData<AsteroidFilter>()
    val filter: LiveData<AsteroidFilter>
        get() = _filter

    init {
        _filter.value = AsteroidFilter.SHOW_SAVED
        _status.value = AsteroidApiStatus.LOADING
        viewModelScope.launch {
            try {
                repository.refreshAsteroids()
                _status.value = AsteroidApiStatus.DONE
            }
            catch (e: Exception) {
                _status.value = AsteroidApiStatus.ERROR
            }
            repository.refreshPictureOfDay()
        }
    }

    private val asteroids: LiveData<List<Asteroid>> = repository.asteroids
    var filteredAsteroids: LiveData<List<Asteroid>> = repository.asteroids
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

    fun updateFilter(asteroidFilter: AsteroidFilter) {
        filteredAsteroids = when (asteroidFilter) {
            AsteroidFilter.SHOW_WEEK -> asteroids.value?.filter { it.closeApproachDate >= getTodayDate() }
            AsteroidFilter.SHOW_TODAY -> asteroids.value?.filter { it.closeApproachDate == getTodayDate() }
            else -> asteroids.value
        }.let { MutableLiveData(it) }
        _filter.value = asteroidFilter
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}