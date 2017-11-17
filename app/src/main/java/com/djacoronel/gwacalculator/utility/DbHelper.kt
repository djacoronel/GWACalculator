package com.djacoronel.gwacalculator.utility

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbHelper @Inject constructor(mContext: Context) : ManagedSQLiteOpenHelper(mContext, "gwaDB") {

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("Course", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "courseCode" to TEXT,
                "units" to INTEGER,
                "grade" to REAL,
                "semester" to TEXT)
        db.createTable("Semester", true,
                "semester" to TEXT + PRIMARY_KEY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

}