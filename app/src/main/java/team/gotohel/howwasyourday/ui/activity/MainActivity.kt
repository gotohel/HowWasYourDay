package team.gotohel.howwasyourday.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import team.gotohel.howwasyourday.MyPreference
import team.gotohel.howwasyourday.R
import team.gotohel.howwasyourday.api.MyApiClient
import team.gotohel.howwasyourday.model.DailyLogSimple
import team.gotohel.howwasyourday.model.PostDailyLog
import team.gotohel.howwasyourday.showProgressDialog
import team.gotohel.howwasyourday.toast

class MainActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        // callback for do something
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {

            }

            override fun onSlide(view: View, v: Float) {
                bg_bottom_sheet.alpha = v
                if (v > 0) {
                    bg_bottom_sheet.visibility = View.VISIBLE
                } else {
                    bg_bottom_sheet.visibility = View.GONE
                }
            }
        })

        edit_daily_log.addTextChangedListener {
            if (it.isNullOrBlank()) {
                btn_save.visibility = View.INVISIBLE
            } else {
                btn_save.visibility = View.VISIBLE
            }
        }
    }

    fun showBottomSheet(view: View) {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun hideBottomSheet(view: View? = null) {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun showSubMenu(view: View) {
        val titleChatList = "Chat List"
        val titleHelp = SpannableString("Help to doctor")
        titleHelp.setSpan(ForegroundColorSpan(Color.parseColor("#E55555")), 0, titleHelp.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        titleHelp.setSpan(TypefaceSpan(resources.getFont(R.font.koho_bold)), 0, titleHelp.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val titleLogout = "Logout"

        PopupMenu(this, view).apply {
            menu.add(titleChatList)
            menu.add(titleHelp)
            menu.add(titleLogout)
            setOnMenuItemClickListener {
                when (it.title) {
                    titleChatList -> {
                        startActivity(Intent(this@MainActivity, ChatListActivity::class.java))
                    }
                    titleHelp -> {
                        startActivity(Intent(this@MainActivity, DoctorDetailActivity::class.java))
                    }
                    titleLogout -> {
                        logout()
                    }
                }

                return@setOnMenuItemClickListener true
            }
        }.show()
    }

    fun justSave(view: View) {
        postDailyLog(false)
    }

    fun saveAndPublish(view: View) {
        postDailyLog(true)
    }

    var postDialog: AlertDialog? = null
    private fun postDailyLog(isSharable: Boolean) {

        val dailyLog = edit_daily_log.text.toString()
        if (dailyLog.isEmpty()) {
            toast("nothing to post!")
        } else {
            hideBottomSheet()
            postDialog = showProgressDialog("Posting...")

            val apiCall = MyApiClient.getInstance().call
            apiCall.uploadDailyLog(PostDailyLog(
                user_id = MyPreference.savedUserId,
                text_log = edit_daily_log.text.toString(),
                is_sharable = isSharable
            ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response, e ->
                    postDialog?.dismiss()

                    if (response != null) {
                        edit_daily_log.setText("")
                        toast("success to post!!!")
                        apiCall.shareDailyLog(DailyLogSimple(response.daily_log.id))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()

                        apiCall.analyzeDailyLog(DailyLogSimple(response.daily_log.id))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                    } else {
                        toast("save failed... ")
                        e?.printStackTrace()
                    }
                }
        }
    }

    private fun logout() {
        MyPreference.stayLogin = false

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(LoginActivity.KEY_SKIP_SPLASH, true)
        startActivity(intent)
    }
}
