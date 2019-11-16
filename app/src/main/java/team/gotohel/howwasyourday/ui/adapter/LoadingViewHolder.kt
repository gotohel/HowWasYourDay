package team.gotohel.howwasyourday.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_loading_view.view.*
import team.gotohel.howwasyourday.R


class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun showLoading() {
        itemView.viewLoading.visibility = View.VISIBLE
        itemView.textMessage.visibility = View.GONE
    }

    fun showMessage(message: String = "no data") {
        itemView.textMessage.text = message

        itemView.viewLoading.visibility = View.GONE
        itemView.textMessage.visibility = View.VISIBLE
    }

    companion object {
        fun createNew(parentView: ViewGroup): LoadingViewHolder {
            val itemView = LayoutInflater.from(parentView.context).inflate(R.layout.item_loading_view, parentView, false)
            return LoadingViewHolder(itemView)
        }
    }
}
