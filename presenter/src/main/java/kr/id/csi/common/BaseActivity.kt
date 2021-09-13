package kr.id.csi.common

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kr.id.data.Constants
import kr.id.data.db.DBHelper

open class BaseActivity : AppCompatActivity() {
    val compositeDisposable = CompositeDisposable()



    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }
}