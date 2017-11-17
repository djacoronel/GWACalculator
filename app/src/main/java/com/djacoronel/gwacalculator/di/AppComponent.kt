package com.djacoronel.gwacalculator.di

import android.app.Application
import com.djacoronel.gwacalculator.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


/**
 * Created by djacoronel on 11/15/17.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, AndroidInjectionModule::class, ActivityBuilder::class))
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}