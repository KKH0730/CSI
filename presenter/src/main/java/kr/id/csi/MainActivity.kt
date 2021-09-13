package kr.id.csi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kr.id.csi.common.BaseActivity
import kr.id.csi.databinding.ActivityMainBinding
import kr.id.csi.ui.MakeCsiActivity
import kr.id.csi.utils.CommonUtils
import kr.id.csi.viewmodel.MainViewModel
import kr.id.data.Constants
import kr.id.data.db.DBHelper
import kr.id.data.model.BranchData
import kr.id.data.repository.*
import java.util.*


class MainActivity : BaseActivity(),OnItemClickListener {
    private var binding: ActivityMainBinding? = null
    private var mainViewModel: MainViewModel? = null
    private var db: SQLiteDatabase? = null
    private var alertDialog : AlertDialog? = null
    private var toCreateBranchName : String? = null
    private var branchListAdapter : BranchListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        checkPermission()
        init()

        setRecyclerView()
        observeInitBranchLiveData()
        observeBranchListLiveData()
    }

    private fun init() {
        binding!!.activity = this
        LocalPreference.init(this)

        MobileAds.initialize(this) { }
        val adRequest = AdRequest.Builder().build()
        binding!!.adView.loadAd(adRequest)

        db = DBHelper.getInstance(this, Constants.db_name, 1).writableDatabase
        mainViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(
                    CreateImpl.getInstance(),
                    InsertImpl.getInstance(),
                    UpdateImpl.getInstance(),
                    DeleteImpl.getInstance(),
                    SelectImpl.getInstance()
                ) as T
            }
        }).get(MainViewModel::class.java)
    }

    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
            100)
        }
    }

    private fun setRecyclerView(){
        branchListAdapter = BranchListAdapter(this)
        binding!!.recyclerView.adapter = branchListAdapter
    }


    fun clickMakeTable() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_brach_name, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        alertDialog = builder.create()
        alertDialog!!.show()

        alertDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val params = alertDialog!!.window?.attributes
        val deviceSize = CommonUtils.getDeviceSize(this)
        params?.width = (deviceSize.width * 0.8).toInt()
        alertDialog!!.window?.attributes = params

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener { alertDialog!!.dismiss() }
        dialogView.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            if(dialogView.findViewById<EditText>(R.id.editBranchName).text.trim().isEmpty()) {
                Toast.makeText(applicationContext, "지점명을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                toCreateBranchName = dialogView.findViewById<EditText>(R.id.editBranchName).text.toString()
                LocalPreference.getInstance().setTableName(toCreateBranchName!!)

                mainViewModel!!.requestInsertBranchList(
                    db!!,
                    dialogView.findViewById<EditText>(R.id.editBranchName).text.toString(),
                    Constants.BRANCH_PASSENGER)

            }
        }
    }


    private fun observeInitBranchLiveData(){
        mainViewModel!!.createQueryResultData.observe(this, {
            if(it is Exception) {
                Toast.makeText(this, "동일한 이름의 객수조사표가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
            } else {
                if(it == true){
                    LocalPreference.getInstance().setId()

                    val intent = Intent(this, MakeCsiActivity::class.java)
                    intent.putExtra("branchName", toCreateBranchName)
                    intent.putExtra("tableName", toCreateBranchName)
                    startActivity(intent)

                    alertDialog!!.dismiss()
                    alertDialog = null
                    toCreateBranchName = null
                } else {
                    toCreateBranchName = null
                    Toast.makeText(this, "동일한 이름의 객수조사표가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun observeBranchListLiveData(){
        mainViewModel!!.branchLiveData.observe(this, {
            if(it != null) {
                binding!!.layoutNo.visibility = View.GONE

                val arr = it
                Collections.sort(arr, object : Comparator<BranchData> {
                    override fun compare(o1: BranchData?, o2: BranchData?): Int {
                        return o2!!.created_at.toLong().compareTo(o1!!.created_at.toLong())
                    }

                })

                branchListAdapter!!.setBranchList(arr)
                branchListAdapter!!.notifyDataSetChanged()

                if(it.size == 0) {
                    binding!!.layoutNo.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        mainViewModel!!.requestBranchList(db!!)
    }

    override fun onItemClicked(branchName: String, modify_name : String, created_at: String) {
        val intent = Intent(this, MakeCsiActivity::class.java)
        intent.putExtra("isAlreadyExist", true)
        intent.putExtra("branchName",  modify_name)
        intent.putExtra("tableName",branchName)
        startActivity(intent)
    }
}