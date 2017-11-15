package com.djacoronel.gwacalculator.di

import com.djacoronel.gwacalculator.App
import com.djacoronel.gwacalculator.view.BaseActivity
import com.djacoronel.gwacalculator.view.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created by djacoronel on 11/15/17.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(app: App)
    fun inject(baseActivity: BaseActivity)
    fun inject(mainActivity: MainActivity)
}