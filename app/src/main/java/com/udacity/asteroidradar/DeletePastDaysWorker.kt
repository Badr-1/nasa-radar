package com.udacity.asteroidradar

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.getTodayDate
import com.udacity.asteroidradar.database.getDatabase

class DeletePastDaysWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    companion object{
        const val WORK_NAME = "DeletePastDaysWorker"
    }

    override fun doWork(): Result {
        val database = getDatabase(applicationContext)
        return try {
            database.asteroidDao.deleteAsteroidsBeforeToday(getTodayDate())
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
