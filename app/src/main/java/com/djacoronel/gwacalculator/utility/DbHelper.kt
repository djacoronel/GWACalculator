package com.djacoronel.gwacalculator.utility

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbHelper @Inject constructor(mContext: Context) : ManagedSQLiteOpenHelper(mContext, "gwaDB", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("Course", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "courseCode" to TEXT,
                "units" to REAL,
                "grade" to REAL,
                "semester" to INTEGER)
        db.createTable("Semester", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "semester" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable("Course", true)
        db.dropTable("Semester", true)

        db.createTable("Course", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "courseCode" to TEXT,
                "units" to REAL,
                "grade" to REAL,
                "semester" to INTEGER)
        db.createTable("Semester", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "semester" to TEXT)
    }
}