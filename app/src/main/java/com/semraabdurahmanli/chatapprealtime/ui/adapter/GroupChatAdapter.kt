package com.semraabdurahmanli.chatapprealtime.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.semraabdurahmanli.chatapprealtime.databinding.ChatroomChatRowBinding
import com.semraabdurahmanli.chatapprealtime.data.response.model.RoomMessage
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class GroupChatAdapter(val list: List<RoomMessage>, val currentUserUid: String) :
    RecyclerView.Adapter<GroupChatAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ChatroomChatRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChatroomChatRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list[position].senderUid.equals(currentUserUid)) {
            holder.binding.recieverLayout.visibility = View.GONE
            holder.binding.senderLayout.visibility = View.VISIBLE
            holder.binding.sentMessageID.text = list[position].message
        } else {
            holder.binding.recieverLayout.visibility = View.VISIBLE
            holder.binding.senderLayout.visibility = View.GONE
            holder.binding.receivedMessageID.text = list[position].message
            holder.binding.memberName.text = list[position].senderName
            Picasso.get()
                .load(list[position].senderPhoto)
                .transform(CropCircleTransformation())
                .into(holder.binding.memberPhoto)
        }
    }
}