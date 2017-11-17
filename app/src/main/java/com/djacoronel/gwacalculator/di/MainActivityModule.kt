package com.djacoronel.gwacalculator.di

import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.model.CourseRepository
import com.djacoronel.gwacalculator.presenter.GWACalcPresenter
import com.djacoronel.gwacalculator.view.MainActivity
import dagger.Module
import dagger.Provides


/**
 * Created by djacoronel on 11/17/17.
 */
@Module
class MainActivityModule {

    @Provides
    fun provideView(mainActivity: MainActivity): Contract.View {
        return mainActivity
    }

    @Provides
    fun provideMainPresenter(view: Contract.View, repo: CourseRepository): Contract.Actions {
        return GWACalcPresenter(view, repo)
    }
}