package com.semraabdurahmanli.chatapprealtime.ui.view_model

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.semraabdurahmanli.chatapprealtime.data.response.model.Message
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


class SingleViewModel : ViewModel() {
    private var messages = MutableLiveData<MutableList<Message>>()
    private lateinit var listOfMessage: MutableList<Message>

    fun getMessages(secondUserUid: String) {
        listOfMessage = mutableListOf()
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseNetwork.databaseReference.child("Single").child("Messages")
                .child("${FirebaseNetwork.auth.currentUser?.uid.toString()}-${secondUserUid}")
                .addChildEventListener(object :
                    ChildEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        if (snapshot.exists()) {
                            listOfMessage.add(snapshot.getValue<Message>() as Message)
                            messages.postValue(listOfMessage)
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

    fun sendMessage(secondUserUid: String, message: String, userToken : String) {
        FirebaseNetwork.databaseReference.child("Single").child("Messages")
            .child("${FirebaseNetwork.auth.currentUser?.uid.toString()}-${secondUserUid}")
            .push()
            .setValue(
                Message(
                    FirebaseNetwork.auth.currentUser?.uid.toString(),
                    secondUserUid,
                    message
                )
            )

        FirebaseNetwork.databaseReference.child("Single").child("Messages")
            .child("${secondUserUid}-${FirebaseNetwork.auth.currentUser?.uid.toString()}")
            .push()
            .setValue(
                Message(
                    FirebaseNetwork.auth.currentUser?.uid.toString(),
                    secondUserUid,
                    message
                )
            )

        sendNotification(message,userToken)
    }

    private fun sendNotification(message: String, userToken: String) {

        try {
            val jsonObject = JSONObject()


            val notificationObj = JSONObject()
            notificationObj.put("title", FirebaseNetwork.auth.currentUser?.displayName.toString())
            notificationObj.put("body", message)


            val dataObj = JSONObject()
            dataObj.put("userId", FirebaseNetwork.auth.currentUser?.uid.toString())
            dataObj.put("isGroup",false )


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
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }

        })

    }

    fun observeMessages(): LiveData<MutableList<Message>> {
        return messages
    }

}