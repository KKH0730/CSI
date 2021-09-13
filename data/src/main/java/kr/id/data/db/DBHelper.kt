package kr.id.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kr.id.data.Constants

class DBHelper(context : Context, dbName : String, version : Int) : SQLiteOpenHelper(context, dbName, null, version){

    companion object {
        private var instance :  DBHelper? = null

        fun getInstance(context: Context,  dbName : String, version : Int) : DBHelper{
            if (instance == null) {
                instance = DBHelper(context, dbName ,version )
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
//        val sql = ("CREATE TABLE if not exists ${Constants.CSI_LIST_TABLE} ("
//                + "_id integer primary key autoincrement,"
//                + "branch text);")

        val sql = ("CREATE TABLE if not exists ${Constants.CSI_LIST_TABLE} ("
                + "_id integer primary key autoincrement,"
                + "branch text,"
                + "modify_name text,"
                + "created_at text);")

        db!!.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}