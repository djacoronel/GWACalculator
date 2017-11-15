package com.djacoronel.gwacalculator

import android.app.Application
import com.djacoronel.gwacalculator.di.AppComponent
import com.djacoronel.gwacalculator.di.AppModule
import com.djacoronel.gwacalculator.di.DaggerAppComponent

/**
 * Created by djacoronel on 11/15/17.
 */
class App : Application() {

    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
    }
}