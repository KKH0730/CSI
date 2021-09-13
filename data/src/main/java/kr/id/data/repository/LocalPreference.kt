package kr.id.data.repository

import android.content.Context
import android.content.SharedPreferences

class LocalPreference private constructor() {
    companion object {
        private var instance: LocalPreference? = null
        private var localPref: SharedPreferences? = null
        private var localEditor: SharedPreferences.Editor? = null

        fun getInstance(): LocalPreference {
            return instance ?: synchronized(this) {
                instance ?: LocalPreference().also { instance = it }
            }
        }

        fun init(context: Context) {
            localPref = context.getSharedPreferences("local", Context.MODE_PRIVATE)
            localEditor = localPref!!.edit()
        }
    }

    fun setId() {
        localEditor!!.putInt("id", getId() + 1).apply()
    }

    fun getId(): Int = localPref!!.getInt("id", 0)

    fun setTableName(tableName : String) {
        // a - passenger
        // b - customer
        val keyA = "tableA${getId()}"
        val keyB = "tableB${getId()}"
        localEditor!!.putString("${tableName}A", keyA).apply()
        localEditor!!.putString("${tableName}B", keyB).apply()

    }

    fun getTableName(tableName : String) : String {
        return localPref!!.getString(tableName, "")!!
    }

    fun deleteSharedPreference(tableKeyName : String){
        localEditor!!.remove(tableKeyName).apply()
    }

}