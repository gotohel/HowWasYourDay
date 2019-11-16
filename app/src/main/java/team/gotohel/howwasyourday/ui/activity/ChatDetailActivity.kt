package team.gotohel.howwasyourday.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sendbird.android.*
import kotlinx.android.synthetic.main.activity_chat_detail.*
import team.gotohel.howwasyourday.R
import team.gotohel.howwasyourday.toast
import team.gotohel.howwasyourday.ui.adapter.ChatMessageListAdapter
import team.gotohel.howwasyourday.util.SendBirdUtils

class ChatDetailActivity: AppCompatActivity() {

    companion object {
        const val KEY_CHAT_URL = "KEY_CHAT_URL"

        const val CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT"
        const val CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT"
        const val CHANNEL_LIST_LIMIT = 30
    }

    private lateinit var mLayoutManager: LinearLayoutManager
    private val chatMessageListAdapter =  ChatMessageListAdapter(this)

    private var mChannel: GroupChannel? = null
    private lateinit var targetChatUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)

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
        if (mChannel != null && mChannel!!.memberCount >= 2) {
            text_chat_title.text = mChannel!!.members.joinToString(" & ") { it.nickname }
            text_chat_description.text = ("${mChannel!!.members[0].nickname} need\nyour support")
        } else {
            text_chat_title.text = "Chat"
            text_chat_description.text = ""
        }
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
}