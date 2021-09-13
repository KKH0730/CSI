package kr.id.csi.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {
    protected val compositeDisposable = CompositeDisposable()
    private val mIsLoading = MutableLiveData(false)
    val isLoading : LiveData<Boolean> get() = mIsLoading

    fun showProgress() {
        mIsLoading.value = true
    }

    fun hideProgress() {
        mIsLoading.value = false
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}