package team.gotohel.howwasyourday.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_doctor_detail.*
import kotlinx.android.synthetic.main.activity_normal_chat.text_chat_description
import kotlinx.android.synthetic.main.activity_normal_chat.text_chat_title


class DoctorDetailActivity: AppCompatActivity() {

    companion object {
        const val KEY_DOCTOR_NAME = "KEY_DOCTOR_NAME"
    }

    val phoneNumber = "010-4522-0517"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(team.gotohel.howwasyourday.R.layout.activity_doctor_detail)

//        val doctorName = intent?.getStringExtra(KEY_DOCTOR_NAME) ?: "Doctor"
        val doctorName = "Doctor"

        text_chat_title.text = (doctorName)
        text_chat_description.text = ("The doctor\nwill help you!")
        text_doctor_instruction.text = ("Hi! I’m $doctorName, I will help you.\n\nAs far as I’m converned, you seem to have symptoms of depression.\n\nIf nothing behavior as it is, there could be a big problem.\nI’ll help you heal your mind.\n\nHere is my contact number. If you want care your mind from me, please contact me.\n\nI hope to see you soon.\nThank you,")
        text_call_title.text = ("Contact $doctorName")
        text_call_description.text = ("Call $doctorName phone number ($phoneNumber)")
    }

    fun callDoctor(view: View) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        startActivity(intent)
    }

    fun backToChatList(view: View) {
        finish()
    }
}