package team.gotohel.howwasyourday

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


    }

    fun startChat(view: View) {
        val inputId = edit_send_bird_id.text.toString().trim()
        val inputName = edit_send_bird_name.text.toString().trim()

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

        btn_start_chat.isEnabled = false

        SendBird.connect(userId) { user, e ->
            // Callback received; hide the progress bar.
            dialog.dismiss()

            if (e != null) {
                // Error!
                toast("Login to SendBird failed")
                toastDebug("${e.code}: ${e.message}")

                btn_start_chat.isEnabled = true

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