package team.gotohel.howwasyourday.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import team.gotohel.howwasyourday.MyPreference
import team.gotohel.howwasyourday.R

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
    }

    fun logout(view: View) {
        MyPreference.savedUserId = null
        MyPreference.savedUserName = null
        MyPreference.stayLogin = false

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun showChatList(view: View) {
        startActivity(Intent(this, ChatListActivity::class.java))

    }


    fun showBottomSheet(view: View) {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun hideBottomSheet(view: View) {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun showHideBottomSheet() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }

    fun justSave(view: View) {

    }

    fun saveAndPublish(view: View) {

    }
}
