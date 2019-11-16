package team.gotohel.howwasyourday.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_login.*
import team.gotohel.howwasyourday.*


class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        showLoginLayout()

        if (MyPreference.stayLogin && MyPreference.savedUserId != null && MyPreference.savedUserName != null) {
            connectToSendBird(MyPreference.savedUserId!!, MyPreference.savedUserName!!)
        } else {
            Handler().postDelayed({
                view_splash.visibility = View.GONE
            }, 1000)
        }
    }

    fun showSignUpLayout(view: View? = null) {
        edit_email.setText("")
        edit_password.setText("")
        edit_nickname.setText("")

        edit_nickname.visibility = View.VISIBLE

        btn_login.visibility = View.GONE
        btn_sign_up.visibility = View.VISIBLE

        btn_show_sign_up.visibility = View.GONE
        btn_show_login.visibility = View.VISIBLE
    }

    fun showLoginLayout(view: View? = null) {
        edit_email.setText("")
        edit_password.setText("")
        edit_nickname.setText("")

        edit_nickname.visibility = View.GONE

        btn_login.visibility = View.VISIBLE
        btn_sign_up.visibility = View.GONE

        btn_show_sign_up.visibility = View.VISIBLE
        btn_show_login.visibility = View.GONE
    }

    fun doLogin(view: View) {

        startChat()
    }

    fun doSignUp(view: View) {

    }

    fun startChat() {
        val inputId = edit_email.text.toString().trim()
        val inputName = edit_password.text.toString().trim()

        when {
            inputId.isEmpty() -> toast("id is empty")
            inputName.isEmpty() -> toast("mame is empty")
            else -> {
                MyPreference.savedUserId = inputId
                MyPreference.savedUserName = inputName
                connectToSendBird(inputId, inputName)
            }
        }
    }

    private fun connectToSendBird(userId: String, userNickname: String) {
        // Show the loading indicator
        val dialog = showProgressDialog("login")

        SendBird.connect(userId) { user, e ->
            // Callback received; hide the progress bar.
            dialog.dismiss()

            if (e != null) {
                // Error!
                toast("Login to SendBird failed")
                toastDebug("${e.code}: ${e.message}")

                MyPreference.stayLogin = false
            } else {
                MyPreference.stayLogin = true

                // Update the user's nickname
                SendBird.updateCurrentUserInfo(userNickname, null) { e ->
                        if (e != null) {
                            // Error!
                            toast("Update user nickname failed")
                            toastDebug("${e.code}: ${e.message}")
                        }
                }

                // Proceed to MainActivity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}