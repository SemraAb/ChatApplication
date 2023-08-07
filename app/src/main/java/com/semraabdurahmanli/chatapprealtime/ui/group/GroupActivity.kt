package com.semraabdurahmanli.chatapprealtime.ui.group

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.semraabdurahmanli.chatapprealtime.ui.adapter.GroupChatAdapter
import com.semraabdurahmanli.chatapprealtime.data.response.model.GroupModel
import com.semraabdurahmanli.chatapprealtime.databinding.ActivityGroupBinding
import com.semraabdurahmanli.chatapprealtime.network.FirebaseNetwork
import com.semraabdurahmanli.chatapprealtime.ui.view_model.GroupViewModel

class GroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupBinding
    private lateinit var room: GroupModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var viewModel: GroupViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[GroupViewModel::class.java]
        room = intent.getSerializableExtra("Group") as GroupModel

        binding.chatRoomaNameText.text = room.roomName

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        binding.chatRoomMessageRV.layoutManager = layoutManager

        viewModel.getMessages(room.roomKey.toString())
        viewModel.observeMessages().observe(this, Observer {
            binding.chatRoomMessageRV.adapter =
                GroupChatAdapter(it, FirebaseNetwork.auth.currentUser?.uid.toString())
            binding.chatRoomMessageRV.adapter?.notifyDataSetChanged()
            binding.chatRoomMessageRV.scrollToPosition(it.size - 1)
        })


        setUpClickListeners()
    }

    fun setUpClickListeners(){
        binding.roomSendButton.setOnClickListener {
            if (binding.chatroomChatEditText.text.toString().trim().length != 0) {
                viewModel.sendMessage(
                    room.roomKey.toString(),
                    binding.chatroomChatEditText.text.toString().trim()
                , room)
                binding.chatroomChatEditText.setText("")
            }
        }
    }


}