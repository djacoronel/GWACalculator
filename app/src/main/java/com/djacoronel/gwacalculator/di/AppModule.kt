package com.djacoronel.gwacalculator.di

import com.djacoronel.gwacalculator.App
import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.model.CourseRepository
import com.djacoronel.gwacalculator.presenter.GWACalcPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by djacoronel on 11/15/17.
 */
@Module
class AppModule(val app: App) {
    @Provides
    @Singleton
    fun provideApp() = app

    @Provides
    @Singleton
    fun provideRepo(): Contract.Repository = CourseRepository(provideApp())

    @Provides
    @Singleton
    fun provideView(): Contract.Actions = GWACalcPresenter(provideRepo())
}