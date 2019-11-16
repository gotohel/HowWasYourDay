package team.gotohel.howwasyourday.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sendbird.android.*
import team.gotohel.howwasyourday.R
import team.gotohel.howwasyourday.util.DateTimeHelper
import java.security.InvalidParameterException

class ChatListAdapter(val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private object ViewType {
        const val LOADING = 0
        const val ITEM = 1
    }

    var mItemClickListener: OnItemClickListener? = null
    var mItemLongClickListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(channel: GroupChannel)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(channel: GroupChannel)
    }

    private var chatList: MutableList<GroupChannel>? = null

    fun setChatList(chatList: MutableList<GroupChannel>) {
        this.chatList = chatList
        notifyDataSetChanged()
    }

    fun updateOrInsert(channel: BaseChannel) {
        if (channel !is GroupChannel || chatList == null) {
            return
        }

        for (i in chatList!!.indices) {
            if (chatList!![i].url == channel.url) {
                chatList!!.remove(chatList!![i])
                chatList!!.add(0, channel)
                notifyDataSetChanged()
                return
            }
        }

        chatList!!.add(0, channel)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return chatList?.size ?: 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList != null) {
            ViewType.ITEM
        } else {
            ViewType.LOADING
        }
    }

    class ChatViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {



        val topicText = itemView.findViewById(R.id.text_group_channel_list_topic) as TextView
        val lastMessageText = itemView.findViewById(R.id.text_group_channel_list_message) as TextView
        val unreadCountText = itemView.findViewById(R.id.text_group_channel_list_unread_count) as TextView
        val dateText = itemView.findViewById(R.id.text_group_channel_list_date) as TextView
        val memberCountText = itemView.findViewById(R.id.text_group_channel_list_member_count) as TextView
        val coverImage = itemView.findViewById(R.id.image_group_channel_list_cover) as ImageView
        val typingIndicatorContainer = itemView.findViewById(R.id.container_group_channel_list_typing_indicator) as LinearLayout


        internal fun bind(
            channel: GroupChannel,
            clickListener: OnItemClickListener?,
            longClickListener: OnItemLongClickListener?
        ) {
            topicText.text = channel.members.joinToString(",") { it.nickname }
            memberCountText.text = channel.memberCount.toString()

//            setChannelImage(context, position, channel, coverImage)

            val unreadCount = channel.unreadMessageCount
            // If there are no unread messages, hide the unread count badge.
            if (unreadCount == 0) {
                unreadCountText.visibility = View.INVISIBLE
            } else {
                unreadCountText.visibility = View.VISIBLE
                unreadCountText.text = channel.unreadMessageCount.toString()
            }

            val lastMessage = channel.lastMessage
            if (lastMessage != null) {
                dateText.visibility = View.VISIBLE
                lastMessageText.visibility = View.VISIBLE

                // Display information about the most recently sent message in the channel.
                dateText.text = DateTimeHelper.getEditingDay(lastMessage.createdAt)

                // Bind last message text according to the type of message. Specifically, if
                // the last message is a File Message, there must be special formatting.
                when (lastMessage) {
                    is UserMessage -> lastMessageText.text = lastMessage.message
                    is AdminMessage -> lastMessageText.text = lastMessage.message
                    else -> {
                        lastMessageText.text = lastMessage::class.java.simpleName
                    }
                }

                lastMessageText.text = lastMessage::class.java.simpleName // fixme  지우기..
            } else {
                dateText.visibility = View.INVISIBLE
                lastMessageText.visibility = View.INVISIBLE
            }

            // typing indicator animation
//            val indicatorImages = ArrayList<ImageView>()
//            indicatorImages.add(typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_1) as ImageView)
//            indicatorImages.add(typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_2) as ImageView)
//            indicatorImages.add(typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_3) as ImageView)
//            val indicator = TypingIndicator(indicatorImages, 600)
//            indicator.animate()

            // If someone in the channel is typing, display the typing indicator.
            if (channel.isTyping) {
                typingIndicatorContainer.visibility = View.VISIBLE
                lastMessageText.text = "Someone is typing"
            } else {
                // Display typing indicator only when someone is typing
                typingIndicatorContainer.visibility = View.GONE
            }

            // Set an OnClickListener to this item.
            if (clickListener != null) {
                itemView.setOnClickListener { clickListener.onItemClick(channel) }
            }

            // Set an OnLongClickListener to this item.
            if (longClickListener != null) {
                itemView.setOnLongClickListener {
                    longClickListener!!.onItemLongClick(channel)

                    // return true if the callback consumed the long click
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.LOADING -> LoadingViewHolder.createNew(parent)
            ViewType.ITEM -> ChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_group_chat, parent, false))
            else -> throw InvalidParameterException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingViewHolder -> {
                if (chatList == null) {
                    holder.showLoading()
                } else {
                    holder.showMessage("no chat")
                }
            }
            is ChatViewHolder -> {
                holder.bind(chatList!![position], mItemClickListener, mItemLongClickListener)
            }
        }
    }
}
