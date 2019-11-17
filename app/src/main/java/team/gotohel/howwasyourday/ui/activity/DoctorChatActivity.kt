package team.gotohel.howwasyourday.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sendbird.android.BaseChannel
import com.sendbird.android.BaseMessage
import com.sendbird.android.GroupChannel
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_normal_chat.*
import team.gotohel.howwasyourday.*
import team.gotohel.howwasyourday.ui.adapter.ChatMessageListAdapter
import team.gotohel.howwasyourday.util.SendBirdUtils

class DoctorChatActivity: AppCompatActivity() {

    companion object {
        const val KEY_CHAT_URL = "KEY_CHAT_URL"

        const val CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT"
        const val CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT"
        const val CHANNEL_LIST_LIMIT = 30
    }

    private lateinit var mLayoutManager: LinearLayoutManager
    private val chatMessageListAdapter =  ChatMessageListAdapter(this, true)

    private var mChannel: GroupChannel? = null
    private lateinit var targetChatUrl: String

    private var doctorName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_chat)

        val iChatUrl = intent.getStringExtra(KEY_CHAT_URL)

        if (iChatUrl == null) {
            toast("Can't find chat")
            finish()
        } else {
            targetChatUrl = iChatUrl

            mLayoutManager = LinearLayoutManager(this).apply {
                reverseLayout = true
            }
            list_chat_message.layoutManager = mLayoutManager
            list_chat_message.adapter = chatMessageListAdapter

            list_chat_message.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (mLayoutManager.findLastVisibleItemPosition() == chatMessageListAdapter.itemCount - 1) {
                        chatMessageListAdapter.loadPreviousMessages(CHANNEL_LIST_LIMIT, null)
                    }
                }
            })
        }
    }

    private fun refresh() {
        if (mChannel == null) {
            GroupChannel.getChannel(targetChatUrl,
                GroupChannel.GroupChannelGetHandler { groupChannel, e ->
                    if (e != null) {
                        // Error!
                        e.printStackTrace()
                        return@GroupChannelGetHandler
                    }

                    mChannel = groupChannel
                    chatMessageListAdapter.setChannel(mChannel!!)
                    chatMessageListAdapter.loadLatestMessages(
                        CHANNEL_LIST_LIMIT,
                        BaseChannel.GetMessagesHandler { list, e ->
                            chatMessageListAdapter.markAllMessagesAsRead()
                        }
                    )

                    updateActionBarTitle()
                })
        } else {
            mChannel!!.refresh(GroupChannel.GroupChannelRefreshHandler { e ->
                if (e != null) {
                    // Error!
                    e.printStackTrace()
                    return@GroupChannelRefreshHandler
                }

                chatMessageListAdapter.loadLatestMessages(
                    CHANNEL_LIST_LIMIT,
                    BaseChannel.GetMessagesHandler { list, e ->
                        chatMessageListAdapter.markAllMessagesAsRead()
                    }
                )

                updateActionBarTitle()
            })
        }
    }

    private fun updateActionBarTitle() {
        doctorName = mChannel?.getOtherUserName()
        text_chat_title.text = ("$doctorName")
        text_chat_description.text = ("The doctor\nwill help you!")
    }

    override fun onResume() {
        super.onResume()

        SendBirdUtils.addConnectionManagementHandler(
            CONNECTION_HANDLER_ID,
            object : SendBirdUtils.ConnectionManagementHandler {
                override fun onConnected(reconnect: Boolean) {
                    refresh()
                }
            })

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, object : SendBird.ChannelHandler() {
            override fun onMessageReceived(baseChannel: BaseChannel, baseMessage: BaseMessage) {
                if (baseChannel.url == targetChatUrl) {
                    chatMessageListAdapter.markAllMessagesAsRead()
                    // Add new message to view
                    chatMessageListAdapter.addFirst(baseMessage)
                }
            }

            override fun onMessageDeleted(baseChannel: BaseChannel?, msgId: Long) {
                super.onMessageDeleted(baseChannel, msgId)
                if (baseChannel!!.url == targetChatUrl) {
                    chatMessageListAdapter.delete(msgId)
                }
            }

            override fun onMessageUpdated(channel: BaseChannel?, message: BaseMessage?) {
                super.onMessageUpdated(channel, message)
                if (channel!!.url == targetChatUrl) {
                    chatMessageListAdapter.update(message!!)
                }
            }

            override fun onReadReceiptUpdated(channel: GroupChannel?) {
                if (channel!!.url == targetChatUrl) {
                    chatMessageListAdapter.notifyDataSetChanged()
                }
            }

            override fun onTypingStatusUpdated(channel: GroupChannel?) {

            }
        })
    }

    override fun onPause() {
        SendBirdUtils.removeConnectionManagementHandler(CONNECTION_HANDLER_ID)
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID)
        super.onPause()
    }

    fun sendMessage(view: View) {
        val userInput = edit_message.text.toString()
        if (userInput.isNotEmpty()) {
            mChannel?.sendUserMessage(userInput) { userMessage, e ->
                if (e != null) {
                    // Error!
                    toast("Send failed with error " + e.code + ": " + e.message)
                } else {
                    edit_message.setText("")
                    chatMessageListAdapter.addFirst(userMessage)
                }
            }
        }
    }

    fun backToChatList(view: View) {
        finish()
    }

    fun showDoctorDetail(view: View) {
        startActivity(Intent(this@DoctorChatActivity, DoctorDetailActivity::class.java).apply {
            putExtra(DoctorDetailActivity.KEY_DOCTOR_NAME, doctorName)
        })
    }
}