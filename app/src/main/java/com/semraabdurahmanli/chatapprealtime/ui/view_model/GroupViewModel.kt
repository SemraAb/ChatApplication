package com.semraabdurahmanli.chatapprealtime.ui.view_model

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.semraabdurahmanli.chatapprealtime.data.response.model.GroupModel
import com.semraabdurahmanli.chatapprealtime.data.response.model.RoomMessage
import com.semraabdurahmanli.chatapprealtime.data.response.model.User
import com.semraabdurahmanli.chatapprealtime.network.FirebaseNetwork
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class GroupViewModel : ViewModel() {
    private var messages = MutableLiveData<MutableList<RoomMessage>>()
    private lateinit var listOfMessage : MutableList<RoomMessage>

    fun getMessages(roomKey : String){
        listOfMessage = mutableListOf()
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseNetwork.databaseReference.child("Group").child(roomKey).child("Message")
                .addChildEventListener(object : ChildEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        listOfMessage.add(snapshot.getValue<RoomMessage>() as RoomMessage)
                        messages.postValue(listOfMessage)

                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
    }
    fun sendMessage(roomKey: String, message: String, group: GroupModel){
        FirebaseNetwork.databaseReference.child("Group").child(roomKey)
            .child("Message")
            .push()
            .setValue(
                RoomMessage(
                    FirebaseNetwork.auth.currentUser?.uid.toString(),
                    message,
                    FirebaseNetwork.auth.currentUser?.photoUrl.toString(),
                    FirebaseNetwork.auth.currentUser?.displayName.toString()
                )
            )
        getUsersForNotification(message, group)
    }

    fun getUsersForNotification(message: String, group: GroupModel) {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseNetwork.databaseReference.child("User")
                .addChildEventListener(object : ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        if(snapshot.exists()){
                            if (!FirebaseNetwork.auth.currentUser?.email.toString()
                                    .equals(snapshot.getValue<User>()?.email.toString())
                            ){
                                sendNotification(message,
                                    (snapshot.getValue<User>() as User) .fcmToken.toString(), group
                                )
                            }
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
    }

    private fun sendNotification(message: String, userToken: String, group: GroupModel) {

        try {
            val jsonObject = JSONObject()


            val notificationObj = JSONObject()
            notificationObj.put("title",group.roomName )
            notificationObj.put("body", "${FirebaseNetwork.auth.currentUser?.displayName.toString()}: $message")


            val dataObj = JSONObject()
            dataObj.put("groupKey", group.roomKey)
            dataObj.put("isGroup", true)


            jsonObject.put("notification", notificationObj)
            jsonObject.put("data",dataObj)
            jsonObject.put("to",userToken)

            callApi(jsonObject)
        }catch (e : Exception){
            e.printStackTrace()
        }


    }

    private fun callApi(jsonObject: JSONObject) {
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val body = RequestBody.create(JSON, jsonObject.toString())
        val request = Request.Builder().url(url).post(body).header(
            "Authorization",
            "Bearer AAAAAMiBg4c:APA91bEwYFUb_w_xcrhoF3IksjXs7E1NkZgvA6jAx9V4zsdWeYb_6kyxDfj_P8OPpehf0ZYYZ5aZVFR5QhU5EFvVY80b1QZpoTGQmMFg_28L5-SXfXQg0oY_Zd3DgcMpHrBBo9315Na7"
        )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }

        })

    }

    fun observeMessages():LiveData<MutableList<RoomMessage>>{
        return messages
    }
}