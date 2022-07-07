package ru.sevenci.dandata
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL( // DDL
            "create table " + TABLE_DATA + "(" +
                    KEY_ID + " integer primary key," +
                    KEY_STAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    KEY_VALUE + " INTEGER NOT NULL, " +
                    KEY_REM  + " TEXT (200) " +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists " + TABLE_DATA)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "contactDb"
        const val TABLE_DATA = "expData"
        const val KEY_ID = "id"
        const val KEY_STAMP = "datestamp"
        const val KEY_VALUE = "value"
        const val KEY_REM = "rem"
    }
}