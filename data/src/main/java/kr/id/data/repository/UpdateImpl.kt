package kr.id.data.repository

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import kr.id.data.Constants
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class UpdateImpl {

    companion object{
        private var updateInstance : UpdateImpl? = null

        fun getInstance() : UpdateImpl {
            if(updateInstance == null) {
                updateInstance = UpdateImpl()
            }
            return updateInstance as UpdateImpl
        }
    }

    fun updatePassengerTable(db : SQLiteDatabase, tableKeyName : String, type : String, columnName : String, time: String, isPlus : Boolean){
        var tableName = if(type == Constants.BRANCH_PASSENGER) {
            LocalPreference.getInstance().getTableName("${tableKeyName}A")
        } else {
            LocalPreference.getInstance().getTableName("${tableKeyName}B")
        }

        try{
            var sql = ""
            sql = if(isPlus) {
                "UPDATE $tableName SET $columnName=$columnName + 1 WHERE period=$time"
            } else {
                "UPDATE $tableName SET $columnName=$columnName - 1 WHERE period=$time"
            }


            updateBranchEditedTime(db, tableKeyName, System.currentTimeMillis().toString())

            db.execSQL(sql)
        }catch (e : Exception) {
            Log.d("test","update error : ${e.message}")
        }
    }

    fun updateBranchName(db : SQLiteDatabase, tableKeyName: String, modifyBranchName : String) : Boolean {
        try{
            var sql =  "UPDATE ${Constants.CSI_LIST_TABLE} SET modify_name='$modifyBranchName' WHERE branch='$tableKeyName'"
            db.execSQL(sql)
            return true
        }catch (e : Exception) {
            Log.d("test","update error : ${e.message}")

            return false
        }
    }

    fun updateBranchEditedTime(db : SQLiteDatabase, tableKeyName: String, time : String){
        try{
            var sql =  "UPDATE ${Constants.CSI_LIST_TABLE} SET created_at='$time' WHERE branch='$tableKeyName'"
            db.execSQL(sql)

        }catch (e : Exception) {
            Log.d("test","update error : ${e.message}")
        }
    }
}