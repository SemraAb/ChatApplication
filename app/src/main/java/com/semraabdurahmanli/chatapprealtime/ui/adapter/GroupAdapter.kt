package com.semraabdurahmanli.chatapprealtime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.semraabdurahmanli.chatapprealtime.data.response.model.GroupModel
import com.semraabdurahmanli.chatapprealtime.databinding.ChatroomRecyclerRowBinding

class ChatroomAdapter(var list: List<GroupModel>, val listener: OnItemClickListener) :
    RecyclerView.Adapter<ChatroomAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ChatroomRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChatroomRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.roomName.setText(list.get(position).roomName)
        holder.binding.root.setOnClickListener {
            listener.onItemClick(list[position])
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(item: GroupModel)
}