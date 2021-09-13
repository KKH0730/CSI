package kr.id.data.repository

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import kr.id.data.Constants
import java.lang.Exception

class DeleteImpl {


    companion object{
        private var deleteInstance : DeleteImpl? = null

        fun getInstance() : DeleteImpl {
            if(deleteInstance == null) {
                deleteInstance = DeleteImpl()
            }
            return deleteInstance as DeleteImpl
        }
    }

    fun deleteTable(db : SQLiteDatabase, tableKeyName : String, type : String){
        var tableName = if(type == Constants.BRANCH_PASSENGER) {
            LocalPreference.getInstance().getTableName("${tableKeyName}A")
        } else {
            LocalPreference.getInstance().getTableName("${tableKeyName}B")
        }

        val sql = "DROP TABLE $tableName"
        db.execSQL(sql)
    }

    fun deleteBranchList(db : SQLiteDatabase, branchName : String){
        val sql = "DELETE FROM ${Constants.CSI_LIST_TABLE} WHERE branch='$branchName'"
        db.execSQL(sql)
    }

    fun deleteBranchListTotal(db : SQLiteDatabase){
        try{
            val sql = "DROP TABLE csi_list"
            Log.d("test","delete")
            db.execSQL(sql)
        }catch (e : Exception) {
            Log.d("test","deleteBranchListTotal error : ${e.message}")
        }
    }

}