package com.djacoronel.gwacalculator.utility

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbHelper @Inject constructor(mContext: Context) : ManagedSQLiteOpenHelper(mContext, "gwaDB", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("Course", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "courseCode" to TEXT,
                "units" to REAL,
                "grade" to REAL,
                "semester" to TEXT)
        db.createTable("Semester", true,
                "semester" to TEXT + PRIMARY_KEY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable("Course", true)
        db.dropTable("Semester", true)

        db.createTable("Course", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "courseCode" to TEXT,
                "units" to REAL,
                "grade" to REAL,
                "semester" to TEXT)
        db.createTable("Semester", true,
                "semester" to TEXT + PRIMARY_KEY)
    }
}