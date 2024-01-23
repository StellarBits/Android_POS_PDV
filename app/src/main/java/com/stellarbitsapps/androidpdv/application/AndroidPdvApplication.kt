package com.stellarbitsapps.androidpdv.application

import android.app.Application
import com.stellarbitsapps.androidpdv.database.AppDatabase

class AndroidPdvApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}