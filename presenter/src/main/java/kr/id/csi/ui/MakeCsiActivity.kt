package kr.id.csi.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.style.DynamicDrawableSpan.ALIGN_CENTER
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kr.id.csi.R
import kr.id.csi.common.BaseActivity
import kr.id.csi.databinding.ActivityMakeCsiBinding
import kr.id.csi.utils.CommonUtils
import kr.id.csi.viewmodel.MainViewModel
import kr.id.data.Constants
import kr.id.data.db.DBHelper
import kr.id.data.model.CustomerCsiData
import kr.id.data.model.PassengerCsiData
import kr.id.data.repository.*
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.util.CellRangeAddress
import java.io.File
import java.io.FileOutputStream

class MakeCsiActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityMakeCsiBinding? = null
    private var branchName: String? = null
    private var tableName: String? = null
    private var mainViewModel: MainViewModel? = null
    private var db: SQLiteDatabase? = null
    private var isCanModify = false
    private var alertDialog : androidx.appcompat.app.AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_make_csi)

        init()
        observeProgressLiveData()
    }

    private fun init() {
        db = DBHelper.getInstance(this, Constants.db_name, 1).writableDatabase

        val adRequest = AdRequest.Builder().build()
        binding!!.adView.loadAd(adRequest)
        branchName = intent.getStringExtra("branchName")
        tableName = intent.getStringExtra("tableName")
        binding!!.textBranchName.setText(branchName)

        if (intent.getBooleanExtra("isAlreadyExist", false)) {
            setData()
        }

        binding!!.txtPassTeenMan.setOnClickListener(clickListener)
        binding!!.txtPassTeenMan.setOnLongClickListener(longClickListener)
        binding!!.txtPassTeenWoman.setOnClickListener(clickListener)
        binding!!.txtPassTeenWoman.setOnLongClickListener(longClickListener)
        binding!!.txtPassTwentyMan.setOnClickListener(clickListener)
        binding!!.txtPassTwentyMan.setOnLongClickListener(longClickListener)
        binding!!.txtPassTwentyWoman.setOnClickListener(clickListener)
        binding!!.txtPassTwentyWoman.setOnLongClickListener(longClickListener)
        binding!!.txtPassThirtyMan.setOnClickListener(clickListener)
        binding!!.txtPassThirtyMan.setOnLongClickListener(longClickListener)
        binding!!.txtPassThirtyWoman.setOnClickListener(clickListener)
        binding!!.txtPassThirtyWoman.setOnLongClickListener(longClickListener)
        binding!!.txtPassFortyMan.setOnClickListener(clickListener)
        binding!!.txtPassFortyMan.setOnLongClickListener(longClickListener)
        binding!!.txtPassFortyWoman.setOnClickListener(clickListener)
        binding!!.txtPassFortyWoman.setOnLongClickListener(longClickListener)
        binding!!.txtPassFiftyMan.setOnClickListener(clickListener)
        binding!!.txtPassFiftyMan.setOnLongClickListener(longClickListener)
        binding!!.txtPassFiftyWoman.setOnClickListener(clickListener)
        binding!!.txtPassFiftyWoman.setOnLongClickListener(longClickListener)

        binding!!.txtCustomerTeenMan.setOnClickListener(clickListener)
        binding!!.txtCustomerTeenMan.setOnLongClickListener(longClickListener)
        binding!!.txtCustomerTeenWoman.setOnClickListener(clickListener)
        binding!!.txtCustomerTeenWoman.setOnLongClickListener(longClickListener)
        binding!!.txtCustomerTwentyMan.setOnClickListener(clickListener)
        binding!!.txtCustomerTwentyMan.setOnLongClickListener(longClickListener)
        binding!!.txtCustomerTwentyWoman.setOnClickListener(clickListener)
        binding!!.txtCustomerTwentyWoman.setOnLongClickListener(longClickListener)
        binding!!.txtCustomerThirtyMan.setOnClickListener(clickListener)
        binding!!.txtCustomerThirtyMan.setOnLongClickListener(longClickListener)
        binding!!.txtCustomerThirtyWoman.setOnClickListener(clickListener)
        binding!!.txtCustomerThirtyWoman.setOnLongClickListener(longClickListener)
        binding!!.txtCustomerFortyMan.setOnClickListener(clickListener)
        binding!!.txtCustomerFortyMan.setOnLongClickListener(longClickListener)
        binding!!.txtCustomerFortyWoman.setOnClickListener(clickListener)
        binding!!.txtCustomerFortyWoman.setOnLongClickListener(longClickListener)
        binding!!.txtCustomerFiftyMan.setOnClickListener(clickListener)
        binding!!.txtCustomerFiftyMan.setOnLongClickListener(longClickListener)
        binding!!.txtCustomerFiftyWoman.setOnClickListener(clickListener)
        binding!!.txtCustomerFiftyWoman.setOnLongClickListener(longClickListener)

        binding!!.btnSaveExcel.setOnClickListener(this)
        binding!!.btnBack.setOnClickListener(this)
        binding!!.btnDelete.setOnClickListener(this)
        binding!!.btnModify.setOnClickListener(this)

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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                100)
        } else {
            clickMakeExcel()
//            saveExcel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 100) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                saveExcel()
                clickMakeExcel()
            } else {
                Toast.makeText(this, "객수조사표를 엑셀로 내보내기 위해서 저장공간 권한 사용에 대해 동의가 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSaveExcel -> {
                checkPermission()
            }
            R.id.btnBack -> {
                finish()
            }
            R.id.btnDelete -> {
                val dialogView = layoutInflater.inflate(R.layout.dialog_delete, null)
                val builder = AlertDialog.Builder(this)
                builder.setView(dialogView)

                val alertDialog = builder.create()
                alertDialog.show()

                alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val params = alertDialog.window?.attributes
                val deviceSize = CommonUtils.getDeviceSize(this)
                params?.width = (deviceSize.width * 0.6).toInt()
                alertDialog.window?.attributes = params

                dialogView.findViewById<AppCompatButton>(R.id.btnConfirm).setOnClickListener {
                    DeleteImpl.getInstance()
                        .deleteBranchList(db!!, tableName!!)
                    DeleteImpl.getInstance().deleteTable(
                        db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER
                    )
                    DeleteImpl.getInstance().deleteTable(
                        db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER
                    )

                    LocalPreference.getInstance().deleteSharedPreference("${tableName}A")
                    LocalPreference.getInstance().deleteSharedPreference("${tableName}B")
                    finish()
                }

                dialogView.findViewById<AppCompatButton>(R.id.btnCancel).setOnClickListener {
                    alertDialog.dismiss()
                }
            }

           R.id.btnModify -> {
                if (isCanModify) {
                    if (binding!!.textBranchName.text.toString().isEmpty()) {
                        Toast.makeText(this, "지점명을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        if (UpdateImpl.getInstance().updateBranchName(
                                db = db!!,
                                tableKeyName = tableName!!,
                                modifyBranchName = binding!!.textBranchName.text.toString()
                            )
                        ) {
                            binding!!.btnModify.text = "수정"
                            binding!!.textBranchName.isEnabled = false
                            isCanModify = false
                            Toast.makeText(this, "객수조사표가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("test", "2222")
                        }
                    }
                } else {
                    binding!!.btnModify.text = "완료"
                    binding!!.textBranchName.isEnabled = true
                    binding!!.textBranchName.isFocusable = true
                    isCanModify = true
                }
            }
        }
    }

    private fun observeProgressLiveData() {
        mainViewModel!!.isLoading.observe(this, {
            if (it) {
                binding!!.progressBar.visibility = View.VISIBLE
            } else {
                binding!!.progressBar.visibility = View.GONE
            }
        })
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun clickMakeExcel() {
        val dialogView = layoutInflater.inflate(R.layout.select_extension, null)
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setView(dialogView)

        alertDialog = builder.create()
        alertDialog!!.show()

        alertDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val params = alertDialog!!.window?.attributes
        val deviceSize = CommonUtils.getDeviceSize(this)
        params?.width = (deviceSize.width * 0.8).toInt()
        alertDialog!!.window?.attributes = params

        dialogView.findViewById<Button>(R.id.btnXls).setOnClickListener {
            saveExcel(".xls")
            alertDialog!!.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btnXlsx).setOnClickListener {
            saveExcel(".xlsx")
            alertDialog!!.dismiss()
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveExcel(extension : String) {
        val customerCsiData = SelectImpl.getInstance().selectBranchTable(
            db!!,
            tableName!!,
            Constants.BRANCH_CUSTOMER
        ) as ArrayList<CustomerCsiData>
        val passengerCsiData = SelectImpl.getInstance().selectBranchTable(
            db!!,
            tableName!!,
            Constants.BRANCH_PASSENGER
        ) as ArrayList<PassengerCsiData>

        for (element in passengerCsiData) {
            Log.d("test", "element : $element")
        }
        val workBook = HSSFWorkbook()
        var sheet = workBook.createSheet("new sheet")


        setBlank(sheet.createRow(0))
        val row1 = sheet.createRow(1)
        val row27 = sheet.createRow(27)

        setMergePassenger(sheet)

        row1.createCell(1).setCellValue("통행객")
        row1.createCell(2).setCellValue("10대")
        row1.createCell(4).setCellValue("20대")
        row1.createCell(6).setCellValue("30대")
        row1.createCell(8).setCellValue("40대")
        row1.createCell(10).setCellValue("50대 이상")
        row1.createCell(12).setCellValue("합계")
        row27.createCell(1).setCellValue("합계")

        val row2 = sheet.createRow(2)
        row2.createCell(2).setCellValue("남")
        row2.createCell(3).setCellValue("여")
        row2.createCell(4).setCellValue("남")
        row2.createCell(5).setCellValue("여")
        row2.createCell(6).setCellValue("남")
        row2.createCell(7).setCellValue("여")
        row2.createCell(8).setCellValue("남")
        row2.createCell(9).setCellValue("여")
        row2.createCell(10).setCellValue("남")
        row2.createCell(11).setCellValue("여")

        var passengerAgeSum = PassengerCsiData()
        var passengerRowSum = 0

        for (i in 0..23) {
            when (i) {
                0 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(3),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                1 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(4),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                2 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(5),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                3 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(6),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                4 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(7),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                5 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(8),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                6 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(9),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                7 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(10),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                8 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(11),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                9 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(12),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                10 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(13),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                11 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(14),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                12 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(15),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                13 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(16),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                14 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(17),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                15 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(18),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                16 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(19),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                17 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(20),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                18 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(21),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                19 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(22),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                20 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(23),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                21 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(24),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                22 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(25),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
                23 -> {
                    passengerRowSum += setPassengerToExcel(
                        sheet.createRow(26),
                        passengerCsiData[i],
                        passengerAgeSum
                    )
                }
            }
        }
        row27.createCell(2).setCellValue(passengerAgeSum.m10.toString())
        row27.createCell(3).setCellValue(passengerAgeSum.w10.toString())
        row27.createCell(4).setCellValue(passengerAgeSum.m20.toString())
        row27.createCell(5).setCellValue(passengerAgeSum.w20.toString())
        row27.createCell(6).setCellValue(passengerAgeSum.m30.toString())
        row27.createCell(7).setCellValue(passengerAgeSum.w30.toString())
        row27.createCell(8).setCellValue(passengerAgeSum.m40.toString())
        row27.createCell(9).setCellValue(passengerAgeSum.w40.toString())
        row27.createCell(10).setCellValue(passengerAgeSum.m50.toString())
        row27.createCell(11).setCellValue(passengerAgeSum.w50.toString())
        row27.createCell(12).setCellValue(
            (
                    passengerAgeSum.m10 +
                            passengerAgeSum.w10 +
                            passengerAgeSum.m20 +
                            passengerAgeSum.w20 +
                            passengerAgeSum.m30 +
                            passengerAgeSum.w30 +
                            passengerAgeSum.m40 +
                            passengerAgeSum.w40 +
                            passengerAgeSum.m50 +
                            passengerAgeSum.w50
                    ).toString()
        )


        val row30 = sheet.createRow(30)
        val row31 = sheet.createRow(31)
        val row32 = sheet.createRow(32)
        val row33 = sheet.createRow(33)
        val row34 = sheet.createRow(34)
        val row35 = sheet.createRow(35)
        val row36 = sheet.createRow(36)
        val row37 = sheet.createRow(37)
        val row38 = sheet.createRow(38)
        val row39 = sheet.createRow(39)

        val row40 = sheet.createRow(40)
        val row41 = sheet.createRow(41)
        val row42 = sheet.createRow(42)
        val row43 = sheet.createRow(43)
        val row44 = sheet.createRow(44)
        val row45 = sheet.createRow(45)
        val row46 = sheet.createRow(46)
        val row47 = sheet.createRow(47)
        val row48 = sheet.createRow(48)

        val row49 = sheet.createRow(49)
        val row50 = sheet.createRow(50)
        val row51 = sheet.createRow(51)
        val row52 = sheet.createRow(52)
        val row53 = sheet.createRow(53)
        val row54 = sheet.createRow(54)
        val row55 = sheet.createRow(55)
        val row56 = sheet.createRow(56)
        var row57 = sheet.createRow(57)


        row30.createCell(1).setCellValue("내점객")
        row30.createCell(2).setCellValue("10대")
        row30.createCell(4).setCellValue("20대")
        row30.createCell(6).setCellValue("30대")
        row30.createCell(8).setCellValue("40대")
        row30.createCell(10).setCellValue("50대 이상")
        row30.createCell(12).setCellValue("합계")
        row56.createCell(1).setCellValue("합계")


        row31.createCell(2).setCellValue("남")
        row31.createCell(3).setCellValue("여")
        row31.createCell(4).setCellValue("남")
        row31.createCell(5).setCellValue("여")
        row31.createCell(6).setCellValue("남")
        row31.createCell(7).setCellValue("여")
        row31.createCell(8).setCellValue("남")
        row31.createCell(9).setCellValue("여")
        row31.createCell(10).setCellValue("남")
        row31.createCell(11).setCellValue("여")

        setMergeCustomer(sheet)

        var customerAgeSum = CustomerCsiData()
        var customerRowSum = 0

        for (i in 0..23) {
            when (i) {
                0 -> {
                    customerRowSum += setCustomerToExcel(row32, customerCsiData[i], customerAgeSum)
                }
                1 -> {
                    customerRowSum += setCustomerToExcel(row33, customerCsiData[i], customerAgeSum)
                }
                2 -> {
                    customerRowSum += setCustomerToExcel(row34, customerCsiData[i], customerAgeSum)
                }
                3 -> {
                    customerRowSum += setCustomerToExcel(row35, customerCsiData[i], customerAgeSum)
                }
                4 -> {
                    customerRowSum += setCustomerToExcel(row36, customerCsiData[i], customerAgeSum)
                }
                5 -> {
                    customerRowSum += setCustomerToExcel(row37, customerCsiData[i], customerAgeSum)
                }
                6 -> {
                    customerRowSum += setCustomerToExcel(row38, customerCsiData[i], customerAgeSum)
                }
                7 -> {
                    customerRowSum += setCustomerToExcel(row39, customerCsiData[i], customerAgeSum)
                }
                8 -> {
                    customerRowSum += setCustomerToExcel(row40, customerCsiData[i], customerAgeSum)
                }
                9 -> {
                    customerRowSum += setCustomerToExcel(row41, customerCsiData[i], customerAgeSum)
                }
                10 -> {
                    customerRowSum += setCustomerToExcel(row42, customerCsiData[i], customerAgeSum)
                }
                11 -> {
                    customerRowSum += setCustomerToExcel(row43, customerCsiData[i], customerAgeSum)
                }
                12 -> {
                    customerRowSum += setCustomerToExcel(row44, customerCsiData[i], customerAgeSum)
                }
                13 -> {
                    customerRowSum += setCustomerToExcel(row45, customerCsiData[i], customerAgeSum)
                }
                14 -> {
                    customerRowSum += setCustomerToExcel(row46, customerCsiData[i], customerAgeSum)
                }
                15 -> {
                    customerRowSum += setCustomerToExcel(row47, customerCsiData[i], customerAgeSum)
                }
                16 -> {
                    customerRowSum += setCustomerToExcel(row48, customerCsiData[i], customerAgeSum)
                }
                17 -> {
                    customerRowSum += setCustomerToExcel(row49, customerCsiData[i], customerAgeSum)
                }
                18 -> {
                    customerRowSum += setCustomerToExcel(row50, customerCsiData[i], customerAgeSum)
                }
                19 -> {
                    customerRowSum += setCustomerToExcel(row51, customerCsiData[i], customerAgeSum)
                }
                20 -> {
                    customerRowSum += setCustomerToExcel(row52, customerCsiData[i], customerAgeSum)
                }
                21 -> {
                    customerRowSum += setCustomerToExcel(row53, customerCsiData[i], customerAgeSum)
                }
                22 -> {
                    customerRowSum += setCustomerToExcel(row54, customerCsiData[i], customerAgeSum)
                }
                23 -> {
                    customerRowSum += setCustomerToExcel(row55, customerCsiData[i], customerAgeSum)
                }
            }
        }

        row56.createCell(2).setCellValue(customerAgeSum.m10.toString())
        row56.createCell(3).setCellValue(customerAgeSum.w10.toString())
        row56.createCell(4).setCellValue(customerAgeSum.m20.toString())
        row56.createCell(5).setCellValue(customerAgeSum.w20.toString())
        row56.createCell(6).setCellValue(customerAgeSum.m30.toString())
        row56.createCell(7).setCellValue(customerAgeSum.w30.toString())
        row56.createCell(8).setCellValue(customerAgeSum.m40.toString())
        row56.createCell(9).setCellValue(customerAgeSum.w40.toString())
        row56.createCell(10).setCellValue(customerAgeSum.m50.toString())
        row56.createCell(11).setCellValue(customerAgeSum.w50.toString())
        row56.createCell(12).setCellValue(
            (customerAgeSum.m10
                    + customerAgeSum.w10
                    + customerAgeSum.m20
                    + customerAgeSum.w20
                    + customerAgeSum.m30
                    + customerAgeSum.w30
                    + customerAgeSum.m40
                    + customerAgeSum.w40
                    + customerAgeSum.m50
                    + customerAgeSum.w50
                    ).toString()
        )


//        var file: File? = null
//
//        if (Build.VERSION.SDK_INT >= 29) {
//            var path = getExternalFilesDir("${binding!!.textBranchName.text}.xlsx")
//            file = File(path, "${binding!!.textBranchName.text}.xlsx")
//        } else {
//            var path = filesDir
//            file = File(path, "${binding!!.textBranchName.text}.xlsx")
//        }

        var file: File? = null

        if (Build.VERSION.SDK_INT >= 29) {
            var path = getExternalFilesDir("${binding!!.textBranchName.text}$extension")
            file = File(path, "${binding!!.textBranchName.text}$extension")
        } else {
            var path = filesDir
            file = File(path, "${binding!!.textBranchName.text}$extension")
        }


        try {
            val os = FileOutputStream(file)
            workBook.write(os)
            os.flush()
            os.close()
            workBook.close()
        } catch (e: Exception) {
            Log.e("test", "excel error : ${e.message}")
        }


        val uri = FileProvider.getUriForFile(this, "kr.id.csi.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/excel"
        intent.putExtra(Intent.EXTRA_STREAM, uri)


        val resInfoList =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (element in resInfoList) {
            val packageName = element.activityInfo.packageName
            applicationContext.grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        startActivity(Intent.createChooser(intent, "엑셀 내보내기"))
    }




    private fun setPassengerToExcel(
        row: HSSFRow,
        data: PassengerCsiData,
        passengerAgeSum: PassengerCsiData
    ): Int {
        row.createCell(1).setCellValue(CommonUtils.setRealTime(data.period!!))
        row.createCell(2).setCellValue(data.m10.toString())
        row.createCell(3).setCellValue(data.w10.toString())
        row.createCell(4).setCellValue(data.m20.toString())
        row.createCell(5).setCellValue(data.w20.toString())
        row.createCell(6).setCellValue(data.m30.toString())
        row.createCell(7).setCellValue(data.w30.toString())
        row.createCell(8).setCellValue(data.m40.toString())
        row.createCell(9).setCellValue(data.w40.toString())
        row.createCell(10).setCellValue(data.m50.toString())
        row.createCell(11).setCellValue(data.w50.toString())
        row.createCell(12).setCellValue(
            (data.m10!! + data.w10!! +
                    data.m20!! + data.w20!! +
                    data.m30!! + data.w30!! +
                    data.m40!! + data.w40!! +
                    data.m50!! + data.w50!!).toString()
        )

        passengerAgeSum.m10 = passengerAgeSum.m10?.plus(data.m10!!)
        passengerAgeSum.m20 = passengerAgeSum.m20?.plus(data.m20!!)
        passengerAgeSum.m30 = passengerAgeSum.m30?.plus(data.m30!!)
        passengerAgeSum.m40 = passengerAgeSum.m40?.plus(data.m40!!)
        passengerAgeSum.m50 = passengerAgeSum.m50?.plus(data.m50!!)
        passengerAgeSum.w10 = passengerAgeSum.w10?.plus(data.w10!!)
        passengerAgeSum.w20 = passengerAgeSum.w20?.plus(data.w20!!)
        passengerAgeSum.w30 = passengerAgeSum.w30?.plus(data.w30!!)
        passengerAgeSum.w40 = passengerAgeSum.w40?.plus(data.w40!!)
        passengerAgeSum.w50 = passengerAgeSum.w50?.plus(data.w50!!)

        return data.m10!! + data.w10!! + data.m20!! + data.w20!! + data.m30!! + data.w30!! + data.m40!! + data.w40!! + data.m50!! + data.w50!!
    }


    private fun setCustomerToExcel(
        row: HSSFRow,
        data: CustomerCsiData,
        customerAgeSum: CustomerCsiData
    ): Int {
        row.createCell(1).setCellValue(CommonUtils.setRealTime(data.period!!))
        row.createCell(2).setCellValue(data.m10.toString())
        row.createCell(3).setCellValue(data.w10.toString())
        row.createCell(4).setCellValue(data.m20.toString())
        row.createCell(5).setCellValue(data.w20.toString())
        row.createCell(6).setCellValue(data.m30.toString())
        row.createCell(7).setCellValue(data.w30.toString())
        row.createCell(8).setCellValue(data.m40.toString())
        row.createCell(9).setCellValue(data.w40.toString())
        row.createCell(10).setCellValue(data.m50.toString())
        row.createCell(11).setCellValue(data.w50.toString())
        row.createCell(12).setCellValue(
            (data.m10!! + data.w10!! +
                    data.m20!! + data.w20!! +
                    data.m30!! + data.w30!! +
                    data.m40!! + data.w40!! +
                    data.m50!! + data.w50!!).toString()
        )
        customerAgeSum.m10 = customerAgeSum.m10?.plus(data.m10!!)
        customerAgeSum.m20 = customerAgeSum.m20?.plus(data.m20!!)
        customerAgeSum.m30 = customerAgeSum.m30?.plus(data.m30!!)
        customerAgeSum.m40 = customerAgeSum.m40?.plus(data.m40!!)
        customerAgeSum.m50 = customerAgeSum.m50?.plus(data.m50!!)
        customerAgeSum.w10 = customerAgeSum.w10?.plus(data.w10!!)
        customerAgeSum.w20 = customerAgeSum.w20?.plus(data.w20!!)
        customerAgeSum.w30 = customerAgeSum.w30?.plus(data.w30!!)
        customerAgeSum.w40 = customerAgeSum.w40?.plus(data.w40!!)
        customerAgeSum.w50 = customerAgeSum.w50?.plus(data.w50!!)

        return data.m10!! + data.w10!! + data.m20!! + data.w20!! + data.m30!! + data.w30!! + data.m40!! + data.w40!! + data.m50!! + data.w50!!
    }

    private fun setBlank(row0: HSSFRow) {
        row0.createCell(0).setCellValue(" ")
        row0.createCell(1).setCellValue(" ")
        row0.createCell(2).setCellValue(" ")
        row0.createCell(3).setCellValue(" ")
        row0.createCell(4).setCellValue(" ")
        row0.createCell(5).setCellValue(" ")
        row0.createCell(6).setCellValue(" ")
        row0.createCell(7).setCellValue(" ")
        row0.createCell(8).setCellValue(" ")
        row0.createCell(9).setCellValue(" ")
        row0.createCell(10).setCellValue(" ")
        row0.createCell(11).setCellValue(" ")
        row0.createCell(12).setCellValue(" ")
        row0.createCell(13).setCellValue(" ")
    }

    private fun setMergePassenger(sheet: HSSFSheet) {
        sheet.addMergedRegion(CellRangeAddress(1, 2, 1, 1)) //통행객
        sheet.addMergedRegion(CellRangeAddress(1, 1, 2, 3))
        sheet.addMergedRegion(CellRangeAddress(1, 1, 4, 5))
        sheet.addMergedRegion(CellRangeAddress(1, 1, 6, 7))
        sheet.addMergedRegion(CellRangeAddress(1, 1, 8, 9))
        sheet.addMergedRegion(CellRangeAddress(1, 1, 10, 11))
        sheet.addMergedRegion(CellRangeAddress(1, 2, 12, 12))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 1, 1))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 2, 2))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 3, 3))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 4, 4))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 5, 5))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 6, 6))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 7, 7))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 8, 8))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 9, 9))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 10, 10))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 11, 11))
        sheet.addMergedRegion(CellRangeAddress(27, 28, 12, 12))
    }


    private fun setMergeCustomer(sheet: HSSFSheet) {
        sheet.addMergedRegion(CellRangeAddress(30, 31, 1, 1))
        sheet.addMergedRegion(CellRangeAddress(30, 30, 2, 3))
        sheet.addMergedRegion(CellRangeAddress(30, 30, 4, 5))
        sheet.addMergedRegion(CellRangeAddress(30, 30, 6, 7))
        sheet.addMergedRegion(CellRangeAddress(30, 30, 8, 9))
        sheet.addMergedRegion(CellRangeAddress(30, 30, 10, 11))
        sheet.addMergedRegion(CellRangeAddress(30, 31, 12, 12))

        sheet.addMergedRegion(CellRangeAddress(56, 57, 1, 1))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 2, 2))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 3, 3))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 4, 4))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 5, 5))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 6, 6))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 7, 7))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 8, 8))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 9, 9))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 10, 10))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 11, 11))
        sheet.addMergedRegion(CellRangeAddress(56, 57, 12, 12))


    }


    private val clickListener = View.OnClickListener { v ->
        if (isCanModify) {
            (v as TextView).text = (v.text.toString().toInt() + 1).toString()

            when (v.id) {
                R.id.txtPassTeenMan -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER,
                        "m10",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtPassTwentyMan -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER,
                        "m20",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtPassThirtyMan -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER,
                        "m30",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtPassFortyMan -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER,
                        "m40",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtPassFiftyMan -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER,
                        "m50",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtPassTeenWoman -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER,
                        "w10",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtPassTwentyWoman -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER,
                        "w20",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtPassThirtyWoman -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER,
                        "w30",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtPassFortyWoman -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER,
                        "w40",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtPassFiftyWoman -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_PASSENGER,
                        "w50",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtCustomerTeenMan -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER,
                        "m10",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtCustomerTwentyMan -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER,
                        "m20",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtCustomerThirtyMan -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER,
                        "m30",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtCustomerFortyMan -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER,
                        "m40",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtCustomerFiftyMan -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER,
                        "m50",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtCustomerTeenWoman -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER,
                        "w10",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtCustomerTwentyWoman -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER,
                        "w20",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtCustomerThirtyWoman -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER,
                        "w30",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtCustomerFortyWoman -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER,
                        "w40",
                        CommonUtils.calTime(),
                        true
                    )
                }

                R.id.txtCustomerFiftyWoman -> {
                    UpdateImpl.getInstance().updatePassengerTable(
                        db = db!!,
                        tableName!!,
                        Constants.BRANCH_CUSTOMER,
                        "w50",
                        CommonUtils.calTime(),
                        true
                    )
                }
            }
        }
    }

    private val longClickListener = View.OnLongClickListener { v ->
        if (isCanModify) {
            val cnt = if ((v as TextView).text.toString().toInt() - 1 >= 0) v.text.toString()
                .toInt() - 1 else 0
            v.text = cnt.toString()

            when (v.id) {
                R.id.txtPassTeenMan -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "m10",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtPassTwentyMan -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "m20",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtPassThirtyMan -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "m30",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtPassFortyMan -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "m40",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtPassFiftyMan -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "m50",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtPassTeenWoman -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "w10",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtPassTwentyWoman -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "w20",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtPassThirtyWoman -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "w30",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtPassFortyWoman -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "w40",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtPassFiftyWoman -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "w50",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtCustomerTeenMan -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "m10",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtCustomerTwentyMan -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "m20",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtCustomerThirtyMan -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "m30",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtCustomerFortyMan -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "m40",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtCustomerFiftyMan -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "m50",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtCustomerTeenWoman -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "w10",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtCustomerTwentyWoman -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "w20",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtCustomerThirtyWoman -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "w30",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtCustomerFortyWoman -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "w40",
                        CommonUtils.calTime()
                    )
                }

                R.id.txtCustomerFiftyWoman -> {
                    SelectImpl.getInstance().selectColumn(
                        db!!,
                        branchName!!,
                        Constants.BRANCH_PASSENGER,
                        "w50",
                        CommonUtils.calTime()
                    )
                }
            }
        }
        true
    }

    private fun setData() {
        val customerCsiData = SelectImpl.getInstance().selectBranchTable(
            db!!,
            tableName!!,
            Constants.BRANCH_CUSTOMER
        ) as ArrayList<CustomerCsiData>
        val passengerCsiData = SelectImpl.getInstance().selectBranchTable(
            db!!,
            tableName!!,
            Constants.BRANCH_PASSENGER
        ) as ArrayList<PassengerCsiData>


        var passM10 = 0
        var passM20 = 0
        var passM30 = 0
        var passM40 = 0
        var passM50 = 0
        var passW10 = 0
        var passW20 = 0
        var passW30 = 0
        var passW40 = 0
        var passW50 = 0


        if (passengerCsiData != null) {
            for (element in passengerCsiData) {
                passM10 += element.m10!!
                passM20 += element.m20!!
                passM30 += element.m30!!
                passM40 += element.m40!!
                passM50 += element.m50!!
                passW10 += element.w10!!
                passW20 += element.w20!!
                passW30 += element.w30!!
                passW40 += element.w40!!
                passW50 += element.w50!!

            }
            binding!!.txtPassTeenMan.text = passM10.toString()
            binding!!.txtPassTwentyMan.text = passM20.toString()
            binding!!.txtPassThirtyMan.text = passM30.toString()
            binding!!.txtPassFortyMan.text = passM40.toString()
            binding!!.txtPassFiftyMan.text = passM50.toString()
            binding!!.txtPassTeenWoman.text = passW10.toString()
            binding!!.txtPassTwentyWoman.text = passW20.toString()
            binding!!.txtPassThirtyWoman.text = passW30.toString()
            binding!!.txtPassFortyWoman.text = passW40.toString()
            binding!!.txtPassFiftyWoman.text = passW50.toString()
        }

        var cusM10 = 0
        var cusM20 = 0
        var cusM30 = 0
        var cusM40 = 0
        var cusM50 = 0
        var cusW10 = 0
        var cusW20 = 0
        var cusW30 = 0
        var cusW40 = 0
        var cusW50 = 0

        if (customerCsiData != null) {
            for (element in customerCsiData) {
                cusM10 += element.m10!!
                cusM20 += element.m20!!
                cusM30 += element.m30!!
                cusM40 += element.m40!!
                cusM50 += element.m50!!
                cusW10 += element.w10!!
                cusW20 += element.w20!!
                cusW30 += element.w30!!
                cusW40 += element.w40!!
                cusW50 += element.w50!!

            }
            binding!!.txtCustomerTeenMan.text = cusM10.toString()
            binding!!.txtCustomerTwentyMan.text = cusM20.toString()
            binding!!.txtCustomerThirtyMan.text = cusM30.toString()
            binding!!.txtCustomerFortyMan.text = cusM40.toString()
            binding!!.txtCustomerFiftyMan.text = cusM50.toString()
            binding!!.txtCustomerTeenWoman.text = cusW10.toString()
            binding!!.txtCustomerTwentyWoman.text = cusW20.toString()
            binding!!.txtCustomerThirtyWoman.text = cusW30.toString()
            binding!!.txtCustomerFortyWoman.text = cusW40.toString()
            binding!!.txtCustomerFiftyWoman.text = cusW50.toString()
        }
    }
}