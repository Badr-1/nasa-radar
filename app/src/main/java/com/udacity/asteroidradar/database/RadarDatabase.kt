package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [CachedAsteroid::class, CachedPictureOfDay::class],
    version = 1,
    exportSchema = false
)
abstract class RadarDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
    abstract val pictureOfDayDao: PictureOfDayDao
}


private lateinit var INSTANCE: RadarDatabase
fun getDatabase(ctx: Context): RadarDatabase {
    synchronized(RadarDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                ctx.applicationContext,
                RadarDatabase::class.java,
                "radar_database"
            ).fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}