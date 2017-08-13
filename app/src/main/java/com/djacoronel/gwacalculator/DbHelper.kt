package com.djacoronel.gwacalculator

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DbHelper(mContext: Context) : ManagedSQLiteOpenHelper(mContext, "mydb") {
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
                "courseCode" to TEXT + PRIMARY_KEY,
                "units" to REAL,
                "grade" to REAL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    // Access property for Context
    val Context.database: DbHelper
        get() = DbHelper.getInstance(applicationContext)
}