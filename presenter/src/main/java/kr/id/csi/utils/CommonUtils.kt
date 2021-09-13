package kr.id.csi.utils

import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kr.id.csi.DeviceSize
import java.text.SimpleDateFormat
import java.util.*

object CommonUtils {
    fun showKeyboard(context: Context, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyboard(context: Context, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun calTime(): String {
        val curTime = Date(System.currentTimeMillis())
        val formatter = SimpleDateFormat("HH:mm")
        var timeArray = formatter.format(curTime).split(":")

        if (timeArray[0] == "00") {
            return "0"
        } else if (timeArray[0] == "01") {
            return "1"
        } else if (timeArray[0] == "02") {
            return "2"
        } else if (timeArray[0] == "03") {
            return "3"
        } else if (timeArray[0] == "04") {
            return "4"
        } else if (timeArray[0] == "05") {
            return "5"
        } else if (timeArray[0] == "06") {
            return "6"
        } else if (timeArray[0] == "07") {
            return "7"
        } else if (timeArray[0] == "08") {
            return "8"
        } else if (timeArray[0] == "09") {
            return "9"
        } else if (timeArray[0] == "10") {
            return "10"
        } else if (timeArray[0] == "11") {
            return "11"
        } else if (timeArray[0] == "12") {
            return "12"
        } else if (timeArray[0] == "13") {
            return "13"
        } else if (timeArray[0] == "14") {
            return "14"
        } else if (timeArray[0] == "15") {
            return "15"
        } else if (timeArray[0] == "16") {
            return "16"
        } else if (timeArray[0] == "17") {
            return "17"
        } else if (timeArray[0] == "18") {
            return "18"
        } else if (timeArray[0] == "19") {
            return "19"
        } else if (timeArray[0] == "20") {
            return "20"
        } else if (timeArray[0] == "21") {
            return "21"
        } else if (timeArray[0] == "22") {
            return "22"
        } else {
            return "23"
        }
    }

    fun setRealTime(number : String) : String {
        when(number) {
            "0" -> { return "00~01" }
            "1" -> { return "01~02" }
            "2" -> { return "02~03" }
            "3" -> { return "03~04" }
            "4" -> { return "04~05" }
            "5" -> { return "05~06" }
            "6" -> { return "06~07" }
            "7" -> { return "07~08" }
            "8" -> { return "08~09" }
            "9" -> { return "09~10" }
            "10" -> { return "10~11" }
            "11" -> { return "11~12" }
            "12" -> { return "12~13" }
            "13" -> { return "13~14" }
            "14" -> { return "14~15" }
            "15" -> { return "15~16" }
            "16" -> { return "16~17" }
            "17" -> { return "17~18" }
            "18" -> { return "18~19" }
            "19" -> { return "19~20" }
            "20" -> { return "20~21" }
            "21" -> { return "21~22" }
            "22" -> { return "22~23" }
            else -> { return "23~24" }

        }
    }

    fun getDeviceSize(context: Context) : DeviceSize {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        var display : Display? = null

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display = context.display
            display!!.getRealMetrics(outMetrics)

            return DeviceSize(outMetrics.widthPixels, outMetrics.heightPixels)
        } else {
            display = windowManager.defaultDisplay
            display.getMetrics(outMetrics)

            val size = Point()
            display.getSize(size)

            return DeviceSize(size.x, size.y)
        }
    }
}