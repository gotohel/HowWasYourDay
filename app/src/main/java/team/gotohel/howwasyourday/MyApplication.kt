package team.gotohel.howwasyourday

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.sendbird.android.SendBird

class MyApplication: Application() {
    companion object {
        private var instance: MyApplication? = null

        val context: Context
            get() = instance!!

        val SEND_BIRD_APP_ID
            get() = instance?.resources?.getString(R.string.send_bird_app_id) ?: ""

    }
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        SendBird.init(SEND_BIRD_APP_ID, applicationContext)
    }

}