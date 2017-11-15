package com.djacoronel.gwacalculator.view

import android.support.v7.app.AppCompatActivity
import com.djacoronel.gwacalculator.App
import com.djacoronel.gwacalculator.di.AppComponent

/**
 * Created by djacoronel on 11/15/17.
 */
abstract class BaseActivity : AppCompatActivity() {
    val androidComponent: AppComponent
        get() = (application as App).component
}