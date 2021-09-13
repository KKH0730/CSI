package kr.id.csi.viewmodel

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kr.id.csi.common.BaseViewModel
import kr.id.data.Constants
import kr.id.data.model.BranchData

import kr.id.data.repository.*


class MainViewModel(
    private val createImpl: CreateImpl,
    private val insertImpl: InsertImpl,
    private val updateImpl: UpdateImpl,
    private val deleteImpl: DeleteImpl,
    private val selectImpl: SelectImpl

) : BaseViewModel() {
    private val mBranchListLiveData = MutableLiveData<ArrayList<BranchData>?>()
    val branchLiveData: LiveData<ArrayList<BranchData>?> get() = mBranchListLiveData

    private val mCreateQueryResultLiveData = MutableLiveData<Any>()
    val createQueryResultData: LiveData<Any> get() = mCreateQueryResultLiveData



    fun requestBranchList(db : SQLiteDatabase){
        compositeDisposable.add(
            selectImpl.selectBranchList(db)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .subscribeBy(
                    onSuccess = {
                        mBranchListLiveData.value = it
                    }, onError = {
                        mBranchListLiveData.value = null
                    }
                )
        )
    }

    fun requestInsertBranchList(
        db: SQLiteDatabase,
        branchName: String,
        type : String
    ) {
        compositeDisposable.add(
            insertImpl.insertBranch(db, branchName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .subscribeBy(
                    onSuccess = {
                        if(it) requestCratePassengerInit(db, branchName, type)
                        else mCreateQueryResultLiveData.value = false
                    }, onError = {
                        Log.d("test","e: ${it.message}")
                        mCreateQueryResultLiveData.value = false
                    }
                )
        )
    }

    fun requestCratePassengerInit(
        db: SQLiteDatabase,
        tableKeyName: String,
        type : String
    ) {
        compositeDisposable.add(
            createImpl.createInitBranchTable(db, tableKeyName, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        if(it) {
                            requestCrateCustomerInit(db, tableKeyName, Constants.BRANCH_CUSTOMER)
                        } else {
                            mCreateQueryResultLiveData.value  = it
                        }
                    }, onError = {
                        Log.d("test","222")
                        mCreateQueryResultLiveData.value  = it
                    }
                )
        )
    }

    private fun requestCrateCustomerInit(
        db: SQLiteDatabase,
        branchName: String,
        type : String
    ) {
        compositeDisposable.add(
            createImpl.createInitBranchTable(db, branchName, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate { hideProgress() }
                .subscribeBy(
                    onSuccess = {
                        mCreateQueryResultLiveData.value  = it
                    }, onError = {
                        Log.d("test","222")
                        mCreateQueryResultLiveData.value  = it
                    }
                )
        )
    }


}