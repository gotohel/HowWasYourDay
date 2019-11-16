package team.gotohel.howwasyourday.ui.adapter

import android.content.Context
import android.text.SpannableString
import android.text.style.TypefaceSpan
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
        const val ITEM_MY = 1
        const val ITEM_OTHER = 2
        const val ITEM_DOCTOR = 3
    }

    var mItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(channel: GroupChannel)
    }

    private var chatList: MutableList<GroupChannel>? = null

    fun setChatList(chatList: MutableList<GroupChannel>) {
        this.chatList = chatList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return chatList?.size ?: 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList != null) {
            (position % 3) + 1 // FIXME
        } else {
            ViewType.LOADING
        }
    }

    class ChatMyViewHolder internal constructor(val context: Context, itemView: View): RecyclerView.ViewHolder(itemView) {
        private val textUserName = itemView.findViewById(R.id.text_user_name) as TextView
        private val textLastMessageTime = itemView.findViewById(R.id.text_last_message_time) as TextView
        private val textLastMessage = itemView.findViewById(R.id.text_last_message) as TextView

        fun bind(channel: GroupChannel, clickListener: OnItemClickListener?) {
            textUserName.text = channel.members.joinToString(", ") { it.nickname }
            textLastMessageTime.text = DateTimeHelper.getEditingDay(channel.lastMessage.createdAt)
            textLastMessage.text = (channel.lastMessage as? UserMessage)?.message ?: ""

            if (channel.unreadMessageCount > 0) {
                textLastMessage.typeface = context.resources.getFont(R.font.koho_bold)
            } else {
                textLastMessage.typeface = context.resources.getFont(R.font.koho_regular)
            }

            if (clickListener != null) {
                itemView.setOnClickListener { clickListener.onItemClick(channel) }
            }
        }
    }

    class ChatOtherViewHolder internal constructor(val context: Context, itemView: View): RecyclerView.ViewHolder(itemView) {
        private val textUserName = itemView.findViewById(R.id.text_user_name) as TextView
        private val textLastMessageTime = itemView.findViewById(R.id.text_last_message_time) as TextView
        private val textLastMessage = itemView.findViewById(R.id.text_last_message) as TextView

        fun bind(channel: GroupChannel, clickListener: OnItemClickListener?) {
            textUserName.text = channel.members.joinToString(", ") { it.nickname }
            textLastMessageTime.text = DateTimeHelper.getEditingDay(channel.lastMessage.createdAt)
            textLastMessage.text = (channel.lastMessage as? UserMessage)?.message ?: ""

            if (channel.unreadMessageCount > 0) {
                textLastMessage.typeface = context.resources.getFont(R.font.koho_bold)
            } else {
                textLastMessage.typeface = context.resources.getFont(R.font.koho_regular)
            }

            if (clickListener != null) {
                itemView.setOnClickListener { clickListener.onItemClick(channel) }
            }
        }
    }

    class ChatDoctorViewHolder internal constructor(val context: Context, itemView: View): RecyclerView.ViewHolder(itemView) {
        private val textUserName = itemView.findViewById(R.id.text_user_name) as TextView
        private val textLastMessageTime = itemView.findViewById(R.id.text_last_message_time) as TextView
        private val textLastMessage = itemView.findViewById(R.id.text_last_message) as TextView

        fun bind(channel: GroupChannel, clickListener: OnItemClickListener?) {
            textUserName.text = channel.members.joinToString(", ") { it.nickname }
            textLastMessageTime.text = DateTimeHelper.getEditingDay(channel.lastMessage.createdAt)
            textLastMessage.text = (channel.lastMessage as? UserMessage)?.message ?: ""

            if (channel.unreadMessageCount > 0) {
                textLastMessage.typeface = context.resources.getFont(R.font.koho_bold)
            } else {
                textLastMessage.typeface = context.resources.getFont(R.font.koho_regular)
            }

            if (clickListener != null) {
                itemView.setOnClickListener { clickListener.onItemClick(channel) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.LOADING -> LoadingViewHolder.createNew(parent)
            ViewType.ITEM_MY -> ChatMyViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.item_group_chat_my, parent, false))
            ViewType.ITEM_OTHER -> ChatOtherViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.item_group_chat_other, parent, false))
            ViewType.ITEM_DOCTOR -> ChatDoctorViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.item_group_chat_doctor, parent, false))
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
            is ChatMyViewHolder -> holder.bind(chatList!![position], mItemClickListener)
            is ChatOtherViewHolder -> holder.bind(chatList!![position], mItemClickListener)
            is ChatDoctorViewHolder -> holder.bind(chatList!![position], mItemClickListener)
        }
    }
}
