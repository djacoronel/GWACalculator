package com.djacoronel.gwacalculator.di

import com.djacoronel.gwacalculator.view.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by djacoronel on 11/17/17.
 */
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    abstract fun bindMainActivity(): MainActivity
}