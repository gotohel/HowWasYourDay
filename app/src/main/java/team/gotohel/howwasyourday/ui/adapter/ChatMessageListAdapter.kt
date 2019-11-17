package team.gotohel.howwasyourday.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sendbird.android.*
import team.gotohel.howwasyourday.R
import team.gotohel.howwasyourday.util.DateTimeHelper

class ChatMessageListAdapter(private val mContext: Context, private val isDoctorChat: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_USER_MESSAGE_ME = 10
        const val VIEW_TYPE_USER_MESSAGE_OTHER = 11
        const val VIEW_TYPE_ADMIN_MESSAGE = 30
    }

    private var mChannel: GroupChannel? = null
    private var mIsMessageListLoading: Boolean = false
    private var mMessageList: MutableList<BaseMessage> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                val myUserMsgView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_message_my, parent, false)
                return MyUserMessageHolder(myUserMsgView)
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                val otherUserMsgView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_message_other, parent, false)
                return OtherUserMessageHolder(otherUserMsgView)
            }
            VIEW_TYPE_ADMIN_MESSAGE -> {
                val adminMsgView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_message_admin, parent, false)
                return AdminMessageHolder(adminMsgView)
            }

            else -> throw Exception("Unknown View Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = mMessageList[position]
        var isNewDay = false
        var isNewMinute = false

        if (position < mMessageList.size - 1) {
            val prevMessage = mMessageList[position + 1]

            if (!DateTimeHelper.hasSameDate(message.createdAt, prevMessage.createdAt)) {
                isNewDay = true
            }
        } else if (position == mMessageList.size - 1) {
            isNewDay = true
        }

        if (position > 0) {
            val nextMessage = mMessageList[position - 1]

            if (!DateTimeHelper.hasSameMinute(message.createdAt, nextMessage.createdAt)) {
                isNewMinute = true
            }
        } else if (position == 0) {
            isNewMinute = true
        }



        when (holder) {
            is MyUserMessageHolder -> holder.bind(
                message as UserMessage,
                isNewDay,
                isNewMinute
            )
            is OtherUserMessageHolder -> holder.bind(
                message as UserMessage,
                isNewDay,
                isNewMinute
            )
            is AdminMessageHolder -> holder.bind(
                message as AdminMessage,
                isNewDay
            )
            else -> {
            }
        }
    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = mMessageList[position]

        if (message is UserMessage) {
            return if (message.sender.userId == SendBird.getCurrentUser().userId) {
                VIEW_TYPE_USER_MESSAGE_ME
            } else {
                VIEW_TYPE_USER_MESSAGE_OTHER
            }
        } else if (message is AdminMessage) {
            return VIEW_TYPE_ADMIN_MESSAGE
        }

        return -1
    }

    fun addFirst(message: BaseMessage) {
        mMessageList.add(0, message)
        notifyDataSetChanged()
    }

    fun markAllMessagesAsRead() {
        mChannel?.markAsRead()
        notifyDataSetChanged()
    }


    fun delete(msgId: Long) {
        for (msg in mMessageList) {
            if (msg.getMessageId() == msgId) {
                mMessageList.remove(msg)
                notifyDataSetChanged()
                break
            }
        }
    }

    fun update(message: BaseMessage) {
        var baseMessage: BaseMessage
        for (index in mMessageList.indices) {
            baseMessage = mMessageList.get(index)
            if (message.messageId == baseMessage.messageId) {
                mMessageList.removeAt(index)
                mMessageList.add(index, message)
                notifyDataSetChanged()
                break
            }
        }
    }


    fun setChannel(channel: GroupChannel) {
        mChannel = channel
    }

    @Synchronized
    private fun isMessageListLoading(): Boolean {
        return mIsMessageListLoading
    }


    @Synchronized
    private fun setMessageListLoading(tf: Boolean) {
        mIsMessageListLoading = tf
    }

    /**
     * Replaces current message list with new list.
     * Should be used only on initial load or refresh.
     */
    fun loadLatestMessages(limit: Int, handler: BaseChannel.GetMessagesHandler?) {
        if (mChannel == null) {
            return
        }

        if (isMessageListLoading()) {
            return
        }

        setMessageListLoading(true)
        mChannel!!.getPreviousMessagesByTimestamp(java.lang.Long.MAX_VALUE,
            true,
            limit,
            true,
            BaseChannel.MessageTypeFilter.ALL,
            null,
            BaseChannel.GetMessagesHandler { list, e ->
                handler?.onResult(list, e)

                setMessageListLoading(false)
                if (e != null) {
                    e.printStackTrace()
                    return@GetMessagesHandler
                }

                if (list.size <= 0) {
                    return@GetMessagesHandler
                }

                mMessageList.clear()

                for (message in list) {
                    mMessageList.add(message)
                }

                notifyDataSetChanged()
            })
    }

    fun loadPreviousMessages(limit: Int, handler: BaseChannel.GetMessagesHandler?) {
        if (mChannel == null) {
            return
        }

        if (isMessageListLoading()) {
            return
        }

        var oldestMessageCreatedAt = java.lang.Long.MAX_VALUE
        if (mMessageList.size > 0) {
            oldestMessageCreatedAt = mMessageList.get(mMessageList.size - 1).createdAt
        }

        setMessageListLoading(true)
        mChannel!!.getPreviousMessagesByTimestamp(oldestMessageCreatedAt,
            false,
            limit,
            true,
            BaseChannel.MessageTypeFilter.ALL,
            null,
            BaseChannel.GetMessagesHandler { list, e ->
                handler?.onResult(list, e)

                setMessageListLoading(false)
                if (e != null) {
                    e.printStackTrace()
                    return@GetMessagesHandler
                }

                for (message in list) {
                    mMessageList.add(message)
                }

                notifyDataSetChanged()
            })
    }

    private inner class AdminMessageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.text_group_chat_message) as TextView
        private val dateText: TextView = itemView.findViewById(R.id.text_group_chat_date) as TextView

        internal fun bind(message: AdminMessage, isNewDay: Boolean) {
            messageText.text = message.message

            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.text = DateTimeHelper.formatDate(message.createdAt)
            } else {
                dateText.visibility = View.GONE
            }
        }
    }

    private inner class MyUserMessageHolder internal constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal var messageText: TextView = itemView.findViewById(R.id.text_group_chat_message) as TextView
        internal var timeText: TextView = itemView.findViewById(R.id.text_group_chat_time) as TextView
        internal var dateText: TextView = itemView.findViewById(R.id.text_group_chat_date) as TextView

        init {
            if (isDoctorChat) {
                messageText.setBackgroundResource(R.drawable.bg_chat_message_my_orange)
            } else {
                messageText.setBackgroundResource(R.drawable.bg_chat_message_my_purple)
            }
        }

        internal fun bind(message: UserMessage, isNewDay: Boolean, isNewMinute: Boolean) {
            messageText.text = message.message

            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.text = DateTimeHelper.formatDate(message.createdAt)
            } else {
                dateText.visibility = View.GONE
            }

            if (isNewMinute) {
                timeText.visibility = View.VISIBLE
                timeText.text = DateTimeHelper.formatTime(message.createdAt)
            } else {
                timeText.visibility = View.GONE
            }
        }
    }

    private inner class OtherUserMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var messageText: TextView = itemView.findViewById(R.id.text_group_chat_message) as TextView
        internal var timeText: TextView = itemView.findViewById(R.id.text_group_chat_time) as TextView
        internal var dateText: TextView = itemView.findViewById(R.id.text_group_chat_date) as TextView

        internal fun bind(message: UserMessage, isNewDay: Boolean, isNewMinute: Boolean) {
            messageText.text = message.message

            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.text = DateTimeHelper.formatDate(message.createdAt)
            } else {
                dateText.visibility = View.GONE
            }

            if (isNewMinute) {
                timeText.visibility = View.VISIBLE
                timeText.text = DateTimeHelper.formatTime(message.createdAt)
            } else {
                timeText.visibility = View.GONE
            }
        }
    }
}
