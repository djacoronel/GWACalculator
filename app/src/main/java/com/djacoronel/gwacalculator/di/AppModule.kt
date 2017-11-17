package com.djacoronel.gwacalculator.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by djacoronel on 11/15/17.
 */
@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application
}