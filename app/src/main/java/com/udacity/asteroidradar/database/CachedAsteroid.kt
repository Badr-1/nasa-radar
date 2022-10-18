package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid


@Entity(tableName = "asteroids_table")
data class CachedAsteroid(
    @PrimaryKey
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

fun List<CachedAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

@Dao
interface AsteroidDao {
    // get asteroids from start date to end date
    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate ASC")
    fun getAsteroidsWithRange(startDate: String, endDate: String): LiveData<List<CachedAsteroid>>

    // get asteroids from today
    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate = :today ORDER BY closeApproachDate ASC")
    fun getTodayAsteroids(today: String): LiveData<List<CachedAsteroid>>

    @Query("SELECT * FROM asteroids_table ORDER BY closeApproachDate ASC")
    fun getAllAsteroids(): LiveData<List<CachedAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: CachedAsteroid)
}