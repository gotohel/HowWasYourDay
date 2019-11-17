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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelListQuery
import kotlinx.android.synthetic.main.activity_chat_list.*
import team.gotohel.howwasyourday.R
import team.gotohel.howwasyourday.isFromDoctor
import team.gotohel.howwasyourday.ui.adapter.ChatListAdapter

class ChatListActivity: AppCompatActivity() {

    private var mChannelListQuery: GroupChannelListQuery? = null
    private val chatListAdapter = ChatListAdapter(this).apply {
        mItemClickListener = object : ChatListAdapter.OnItemClickListener {
            override fun onItemClick(channel: GroupChannel) {
                if (channel.isFromDoctor()) {
                    startActivity(
                        Intent(this@ChatListActivity, DoctorChatActivity::class.java).apply {
                            putExtra(DoctorChatActivity.KEY_CHAT_URL, channel.url)
                        }
                    )
                } else {
                    startActivity(
                        Intent(this@ChatListActivity, NormalChatActivity::class.java).apply {
                            putExtra(NormalChatActivity.KEY_CHAT_URL, channel.url)
                        }
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        list_group_chat.layoutManager = LinearLayoutManager(this)
        list_group_chat.adapter = chatListAdapter

        swipe_refresh_group_chat.setOnRefreshListener {
            refreshChannelList()
        }
    }

    override fun onResume() {
        super.onResume()

        refreshChannelList()
    }

    private fun refreshChannelList() {
        mChannelListQuery = GroupChannel.createMyGroupChannelListQuery().also {
            it.next { list, e ->
                if (e != null) {
                    // Error!
                    e.printStackTrace()
                } else {
                    Log.d("테스트", "채팅 리스트 불러옴 ${list.size} 개")
                    chatListAdapter.setChatList(list)
                }

                swipe_refresh_group_chat.isRefreshing = false
            }
        }
    }

    fun showSubMenu(view: View) {
        val titleWrite = SpannableString("Write")
        titleWrite.setSpan(TypefaceSpan(resources.getFont(R.font.koho_bold)), 0, titleWrite.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val titleHelp = SpannableString("NEED HELP")
        titleHelp.setSpan(ForegroundColorSpan(Color.parseColor("#E55555")), 0, titleHelp.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        titleHelp.setSpan(TypefaceSpan(resources.getFont(R.font.koho_bold)), 0, titleHelp.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        PopupMenu(this, view).apply {
            menu.add(titleWrite)
            menu.add(titleHelp)
            setOnMenuItemClickListener {
                when (it.title) {
                    titleWrite -> {
                        finish()
                    }
                    titleHelp -> {
                        startActivity(Intent(this@ChatListActivity, DoctorDetailActivity::class.java))
                    }
                }

                return@setOnMenuItemClickListener true
            }
        }.show()
    }
}