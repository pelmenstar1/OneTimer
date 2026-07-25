package com.pelmenstar.onetimer

import android.app.Application
import com.pelmenstar.onetimer.persistance.AppDatabase
import com.pelmenstar.onetimer.persistance.createAppDatabase

class OneTimerApplication : Application() {
  val database: AppDatabase by lazy { createAppDatabase(this) }
}
