package team.gotohel.howwasyourday.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sendbird.android.SendBird
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import team.gotohel.howwasyourday.*
import team.gotohel.howwasyourday.api.MyApiClient
import team.gotohel.howwasyourday.model.PostLogin
import team.gotohel.howwasyourday.model.PostUserRegister


class LoginActivity: AppCompatActivity() {

    companion object {
        const val KEY_SKIP_SPLASH = "KEY_SKIP_SPLASH"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        showLoginLayout()

        edit_email.setText(MyPreference.savedUserEmail ?: "")
        edit_password.setText(MyPreference.savedUserPassword ?: "")

        val skipSplash = intent?.getBooleanExtra(KEY_SKIP_SPLASH, false) ?: false

        when {
            skipSplash -> view_splash.visibility = View.GONE
            MyPreference.stayLogin -> startLogin()
            else -> Handler().postDelayed({
                view_splash.visibility = View.GONE
            }, 1000)
        }
    }

    fun showSignUpLayout(view: View? = null) {
        edit_nickname.visibility = View.VISIBLE

        btn_login.visibility = View.GONE
        btn_sign_up.visibility = View.VISIBLE

        btn_show_sign_up.visibility = View.GONE
        btn_show_login.visibility = View.VISIBLE
    }

    fun showLoginLayout(view: View? = null) {
        edit_nickname.visibility = View.GONE

        btn_login.visibility = View.VISIBLE
        btn_sign_up.visibility = View.GONE

        btn_show_sign_up.visibility = View.VISIBLE
        btn_show_login.visibility = View.GONE
    }

    private var loginDialog: AlertDialog? = null
    private fun showProgress(message: String) {
        if (view_splash.visibility != View.VISIBLE) {
            loginDialog = showProgressDialog(message)
        }
    }


    fun startLogin(view: View? = null) {
        val email = edit_email.text.toString().trim()
        val password = edit_password.text.toString().trim()

        when {
            email.isEmpty() -> {
                toast("email is empty")
                view_splash.visibility = View.GONE
            }
            password.isEmpty() -> {
                toast("password is empty")
                view_splash.visibility = View.GONE
            }
            else -> {
                showProgress("login...")
                MyApiClient.getInstance().call.login(PostLogin(
                    email = email,
                    password = password
                ))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { user, throwable ->
                        if (user != null) {
                            toast("success to login!")
                            MyPreference.savedUserEmail = email
                            MyPreference.savedUserPassword = password
                            connectSendBirdAndStart(user.id)
                        } else {
                            toast("Can not found user")
                            loginDialog?.dismiss()
                            view_splash.visibility = View.GONE
                            throwable?.printStackTrace()
                        }

                    }
            }
        }
    }

    fun startSignUp(view: View) {
        val email = edit_email.text.toString().trim()
        val password = edit_password.text.toString().trim()
        val nickname = edit_nickname.text.toString().trim()

        when {
            email.isEmpty() -> toast("email is empty")
            password.isEmpty() -> toast("password is empty")
            nickname.isEmpty() -> toast("nickname is empty")
            else -> {
                showProgress("sign up...")
                MyApiClient.getInstance().call.registerUser(PostUserRegister(
                    email = email,
                    password = password,
                    nickname = nickname
                ))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { user, throwable ->
                        if (user != null) {
                            toast("success to sign up")
                            MyPreference.savedUserEmail = email
                            MyPreference.savedUserPassword = password
                            connectSendBirdAndStart(user.id)
                        } else {
                            toast("sign up failed..")
                            loginDialog?.dismiss()
                            view_splash.visibility = View.GONE
                            throwable?.printStackTrace()
                        }
                    }
            }
        }
    }

    private fun connectSendBirdAndStart(userId: Int) {
        // Show the loading indicator

        SendBird.connect(userId.toString()) { user, e ->
            // Callback received; hide the progress bar.

            if (e != null) {
                loginDialog?.dismiss()
                toast("Login to SendBird failed")
                e.printStackTrace()

                MyPreference.stayLogin = false
                view_splash.visibility = View.GONE
            } else {
                MyPreference.stayLogin = true

                // Proceed to MainActivity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}