package com.example.zingmp3phake.data.repo.resource.local.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.zingmp3phake.utils.CREATE_TABLE
import com.example.zingmp3phake.utils.DB_NAME
import com.example.zingmp3phake.utils.DB_VERSION

class SongDatabase(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = CREATE_TABLE
        db?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        // TODO implement later
    }

    fun queryData(sql: String) {
        val db: SQLiteDatabase = writableDatabase
        db.execSQL(sql)
    }

    fun getData(sql: String): Cursor {
        val db: SQLiteDatabase = readableDatabase
        return db.rawQuery(sql, null)
    }

    companion object {
        private var instance: SongDatabase? = null
        fun getInstance(context: Context?) = synchronized(this) {
            instance ?: SongDatabase(context, DB_NAME, null, DB_VERSION).also { instance = it }
        }
    }
}
