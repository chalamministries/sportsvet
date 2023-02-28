package com.fini.pro.sportsvet

import android.app.Application

class MyApp : Application() {

    companion object {
        lateinit var shared: MyApp
    }

    override fun onCreate() {
        super.onCreate()

        shared = applicationContext as MyApp

//        FirebaseApp.writeException("MyApp", "Testing...")
    }

    fun isTablet() : Boolean {
        return resources.getBoolean(R.bool.isTablet)
    }

}