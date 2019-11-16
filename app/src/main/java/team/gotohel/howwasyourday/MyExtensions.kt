package team.gotohel.howwasyourday

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_progress.*


const val MIN_INTERVAL_TIME = 1000

fun Context.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    Log.d("토스트", message)
}

fun Context.toastDebug(message: String?) {
    Log.d("디버깅", "$message")
    if (BuildConfig.DEBUG_MODE) Toast.makeText(this, "[DEBUG] $message", Toast.LENGTH_SHORT).show()
}

fun Context.toast(releaseMessage: String?, debugMessage: String?) {
    if (BuildConfig.DEBUG_MODE) {
        toast(debugMessage)
    } else {
        toastDebug(releaseMessage)
    }
}

fun View.isDoubleClicked(): Boolean {
    if ((SystemClock.elapsedRealtime()-((tag as? Long) ?: 0)) < MIN_INTERVAL_TIME) {
        return true
    } else {
        tag = SystemClock.elapsedRealtime()
        return false
    }
}

fun Activity.hideKeyboard() {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (currentFocus != null) {
        inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        inputManager.hideSoftInputFromInputMethod(currentFocus!!.windowToken, 0)
    }
}

fun Activity.showProgressDialog(message: String): AlertDialog {
    return AlertDialog.Builder(this)
        .setView(R.layout.dialog_progress)
        .show()
        .apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            text_loading_message?.text = message
        }
}