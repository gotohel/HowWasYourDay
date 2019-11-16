package team.gotohel.howwasyourday.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import team.gotohel.howwasyourday.MyPreference
import team.gotohel.howwasyourday.R
import team.gotohel.howwasyourday.ui.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
}
