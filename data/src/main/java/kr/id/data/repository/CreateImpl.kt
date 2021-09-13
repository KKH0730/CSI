package kr.id.data.repository

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import io.reactivex.rxjava3.core.Single
import kr.id.data.Constants

class CreateImpl(
    private val insertImpl : InsertImpl
){
    companion object{
        private var createInstance : CreateImpl? = null

        fun getInstance() : CreateImpl {
            if(createInstance == null) {
                createInstance = CreateImpl(InsertImpl())
            }
            return createInstance as CreateImpl
        }
    }

    fun createInitBranchTable(database : SQLiteDatabase, tableKeyName : String, type : String) : Single<Boolean> {
        return try{
            var tableName = if(type == Constants.BRANCH_PASSENGER) {
                LocalPreference.getInstance().getTableName("${tableKeyName}A")
            } else {
                LocalPreference.getInstance().getTableName("${tableKeyName}B")
            }

            val sql =
                "create table $tableName(_id integer PRIMARY KEY autoincrement, period text, m10 Integer, m20 Integer, m30 Integer, m40 Integer, m50 Integer, w10 Integer, w20 Integer, w30 Integer, w40 Integer, w50 Integer)"
            database.execSQL(sql)
            Log.d("test","테이블 생성됨. tableName : $tableName$type")

            insertImpl.insertInitBranchTable(database, tableName)
            Single.just(true)
        }catch (e : Exception) {
            Log.d("test","table create error : ${e}")
            Single.just(false)
        }
    }


}