package com.djacoronel.gwacalculator.utility

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DbHelper(mContext: Context) : ManagedSQLiteOpenHelper(mContext, "gwaDB") {
    companion object {
        private var instance: DbHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DbHelper {
            if (instance == null) {
                instance = DbHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

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

    // Access property for Context
    val Context.database: DbHelper
        get() = getInstance(applicationContext)
}