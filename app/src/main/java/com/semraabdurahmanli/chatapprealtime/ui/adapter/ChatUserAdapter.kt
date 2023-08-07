package com.semraabdurahmanli.chatapprealtime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.semraabdurahmanli.chatapprealtime.data.response.model.User
import com.semraabdurahmanli.chatapprealtime.databinding.ChatRecyclerRowBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class ChatUserAdapter(val list: List<User>, val listener: OnItemChatClickListener) :
    RecyclerView.Adapter<ChatUserAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ChatRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChatRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.nameId2.setText(list.get(position).name)
        holder.binding.emailText.setText(list.get(position).email)
        Picasso.get()
            .load(list[position].photoUrl)
            .transform(CropCircleTransformation())
            .resize(130, 130)
            .into(holder.binding.profileImage)
        holder.itemView.setOnClickListener {
            listener.onItemClick(list[position])
        }
    }
}

interface OnItemChatClickListener {
    fun onItemClick(user: User)
}
