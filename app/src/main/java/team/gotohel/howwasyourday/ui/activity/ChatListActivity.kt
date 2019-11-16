package team.gotohel.howwasyourday.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelListQuery
import kotlinx.android.synthetic.main.activity_chat_list.*
import team.gotohel.howwasyourday.R
import team.gotohel.howwasyourday.toastDebug
import team.gotohel.howwasyourday.ui.adapter.ChatListAdapter

class ChatListActivity: AppCompatActivity() {

    private var mChannelListQuery: GroupChannelListQuery? = null
    private val chatListAdapter = ChatListAdapter(this).apply {
        mItemClickListener = object : ChatListAdapter.OnItemClickListener {
            override fun onItemClick(channel: GroupChannel) {
                val intent = Intent(this@ChatListActivity, ChatDetailActivity::class.java).apply {
                    putExtra(ChatDetailActivity.KEY_CHAT_URL, channel.url)
                }
                
                startActivity(intent)
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
}