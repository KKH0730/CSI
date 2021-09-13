package kr.id.data.repository

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import io.reactivex.rxjava3.core.Single
import kr.id.data.Constants
import java.text.SimpleDateFormat
import java.util.*

class InsertImpl {

    companion object {
        private var insertInstance: InsertImpl? = null

        fun getInstance(): InsertImpl {
            if (insertInstance == null) {
                insertInstance = InsertImpl()
            }
            return insertInstance as InsertImpl
        }
    }

    fun insertInitBranchTable(database : SQLiteDatabase, tableName : String) : Single<Boolean> {
        return try{
            val sql = "insert into $tableName(period, m10, m20, m30, m40, m50, w10, w20, w30, w40, w50) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"


            for(i in 0..23) {
                var params = arrayOf(
                    "$i", "", "", "", "", "", "", "", "", "", "",
                )

                database.execSQL(sql, params)
            }
            Single.just(true)
        }catch (e : Exception) {
            Single.just(false)
        }
    }


    fun insertBranch(database: SQLiteDatabase, branch: String) : Single<Boolean> {
        return try{


            val sql = "insert into ${Constants.CSI_LIST_TABLE}(branch, modify_name, created_at) values(?, ?, ?)"
            var params = arrayOf<Any>(branch, branch, System.currentTimeMillis().toString())
            database.execSQL(sql, params)
            Single.just(true)
        }catch (e : Exception) {
            Log.d("test","insertBranch error : ${e.message}")
            Single.just(false)
        }
    }
}