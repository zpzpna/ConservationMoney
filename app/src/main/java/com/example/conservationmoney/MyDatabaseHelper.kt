package com.example.conservationmoney

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
//模仿书上代码创建存储记账列表信息数据库
class MyDatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    private val createBook = "create table if not exists List (" +
            " id integer primary key autoincrement," +
            "typename char," +
            "date char," +
            "data char)"
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createBook)
        Toast.makeText(context, "table list is ok", Toast.LENGTH_SHORT).show()
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
    fun getInitData(): Cursor? {
        val database = writableDatabase
        return database.query("list", null, null, null, null, null, "data ASC")
    }
    }
