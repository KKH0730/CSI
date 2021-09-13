package kr.id.data.repository

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import io.reactivex.rxjava3.core.Single
import kr.id.data.Constants
import kr.id.data.model.BranchData
import kr.id.data.model.CustomerCsiData
import kr.id.data.model.PassengerCsiData

class SelectImpl() {

    companion object {
        private var selectInstance: SelectImpl? = null

        fun getInstance(): SelectImpl {
            if (selectInstance == null) {
                selectInstance = SelectImpl()
            }
            return selectInstance as SelectImpl
        }
    }

    fun selectBranchTable(database: SQLiteDatabase, tableKeyName: String, type: String): Any? {
        var customerCsiDataArray: ArrayList<CustomerCsiData> = ArrayList()
        var passengerCsiDataArray: ArrayList<PassengerCsiData> = ArrayList()

        var tableName = if(type == Constants.BRANCH_PASSENGER) {
            LocalPreference.getInstance().getTableName("${tableKeyName}A")
        } else {
            LocalPreference.getInstance().getTableName("${tableKeyName}B")
        }

        try {
            val sql =
                "select period, m10, m20, m30, m40, m50, w10, w20, w30, w40, w50 from $tableName"
            val cursor = database.rawQuery(sql, null) //파라미터는 없으니깐 null 값 넣어주면된다.


            for (i in 0 until cursor.count) {
                cursor.moveToNext() //이걸 해줘야 다음 레코드로 넘어가게된다.
                val period = cursor.getString(0) //첫번쨰 칼럼을 뽑아줌
                val m10 = cursor.getInt(1) //첫번쨰 칼럼을 뽑아줌
                val m20 = cursor.getInt(2)
                val m30 = cursor.getInt(3)
                val m40 = cursor.getInt(4)
                val m50 = cursor.getInt(5)
                val w10 = cursor.getInt(6)
                val w20 = cursor.getInt(7)
                val w30 = cursor.getInt(8)
                val w40 = cursor.getInt(9)
                val w50 = cursor.getInt(10)

                Log.d(
                    "test",
                    "type : $type, $m10, $m20, $m30, $m40, $m50, $w10, $w20, $w30, $w40, $w50"
                )
                customerCsiDataArray.add(
                    CustomerCsiData(
                        period,  m10, m20, m30, m40, m50, w10, w20, w30, w40, w50
                    )
                )


                passengerCsiDataArray.add(
                    PassengerCsiData(
                        period, m10, m20, m30, m40, m50, w10, w20, w30, w40, w50
                    )
                )

            }
            cursor.close()
        } catch (e: Exception) {
            Log.d("test", "selectInitBranchTable Error : ${e.message}")
        }

        return if(type == Constants.BRANCH_CUSTOMER) {
            customerCsiDataArray
        } else {
            passengerCsiDataArray
        }
    }

    fun selectColumn(
        database: SQLiteDatabase,
        tableKeyName: String,
        type: String,
        columnName: String,
        time: String
    ) {
        try {

            var tableName = if(type == Constants.BRANCH_PASSENGER) {
                LocalPreference.getInstance().getTableName("${tableKeyName}A")
            } else {
                LocalPreference.getInstance().getTableName("${tableKeyName}B")
            }

            val sql = "select $columnName from $tableName WHERE period=$time"
            val cursor = database.rawQuery(sql, null) //파라미터는 없으니깐 null 값 넣어주면된다.

            cursor.moveToNext()
            val value = cursor.getInt(0)

            if (value == 0) {
                return
            } else {
                UpdateImpl.getInstance()
                    .updatePassengerTable(database, tableKeyName, type, columnName, time, false)
            }

            Log.d("test", "value : ${value}")
            cursor.close()
        } catch (e: Exception) {
            Log.d("test", "selectInitBranchTable Error : ${e.message}")
        }
    }


    fun selectBranchList(database: SQLiteDatabase): Single<ArrayList<BranchData>> {
        return try {
            val sql = "select * from ${Constants.CSI_LIST_TABLE}"
            val cursor = database.rawQuery(sql, null) //파라미터는 없으니깐 null 값 넣어주면된다.
            val branchList = ArrayList<BranchData>()
            for (i in 0 until cursor.count) {
                cursor.moveToNext() //이걸 해줘야 다음 레코드로 넘어가게된다.
                var _id = cursor.getInt(0)
                val branchName = cursor.getString(1)
                val modify_name = cursor.getString(2)
                var created_at = cursor.getString(3)
                branchList.add(BranchData(branchName, modify_name, created_at))
                Log.d("test", "$i ->id : $_id, branchName : $branchName, modify_name : $modify_name, created_at : $created_at")
            }
            cursor.close()
            Single.just(branchList)
        } catch (e: Exception) {
            Log.d("test", "selectBranchList error : ${e.message}")
            val branchList = ArrayList<BranchData>()
            Single.just(branchList)
        }
    }
}