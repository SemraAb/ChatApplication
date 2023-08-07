package com.semraabdurahmanli.chatapprealtime.ui.view_model

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.semraabdurahmanli.chatapprealtime.data.response.model.GroupModel
import com.semraabdurahmanli.chatapprealtime.data.response.model.User
import com.semraabdurahmanli.chatapprealtime.network.FirebaseNetwork
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel : ViewModel() {

    private var operationMessage = MutableLiveData<String>()
    private var canCreateGroup = MutableLiveData<Boolean>()
    private var userList = MutableLiveData<MutableList<User>>()
    private var groupList = MutableLiveData<MutableList<GroupModel>>()
    private lateinit var listOfUser: MutableList<User>
    private lateinit var listOfGroup: MutableList<GroupModel>

    fun getGroups() {
        CoroutineScope(Dispatchers.IO).launch {
            listOfGroup = mutableListOf()
            FirebaseNetwork.databaseReference.child("Group")
                .addChildEventListener(object : ChildEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        if (snapshot.exists()) {
                            listOfGroup.add(
                                snapshot.child("GroupInfo").getValue<HashMap<String, GroupModel>>()?.toList()
                                    ?.get(0)?.second!!
                            )
                            groupList.postValue(listOfGroup)
                            Log.d("Emill", snapshot.child("GroupInfo").getValue().toString())

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

    fun getUsers() {
        listOfUser = mutableListOf()
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseNetwork.databaseReference.child("User")
                .addChildEventListener(object : ChildEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        if (snapshot.exists()) {
                               if (!FirebaseNetwork.auth.currentUser?.email.toString()
                                    .equals(snapshot.getValue<User>()?.email.toString())
                            ) {
                                listOfUser.add(snapshot.getValue<User>() as User)
                                userList.postValue(listOfUser)
                            } else {
                                canCreateGroup.postValue(snapshot.getValue<User>()?.canCreateGroup == true)
                            }
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        if (FirebaseNetwork.auth.currentUser?.email.toString()
                                .equals(snapshot.getValue<User>()?.email.toString())
                        ) {
                            canCreateGroup.postValue(snapshot.getValue<User>()?.canCreateGroup == true)
                        }
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

     fun updateFcmToken() {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseNetwork.databaseReference.child("User")
                        .child(FirebaseNetwork.auth.currentUser?.uid.toString()).child("fcmToken")
                        .setValue(
                            task.getResult()
                        )
                }
            }
        }
    }
    fun deleteFcmToken() {
        FirebaseMessaging.getInstance().deleteToken()
            .addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(p0: Task<Void>) {

                }
            })
    }

    fun signOut(){
        FirebaseNetwork.auth.signOut()
        operationMessage.postValue("Signed out")
    }

    fun addGroup(groupName: String) {
        val uuid = UUID.randomUUID()
        FirebaseNetwork.databaseReference.child("Group").child(uuid.toString()).child("GroupInfo").push().setValue(GroupModel(groupName, uuid.toString()))
    }

    fun observeUsers(): LiveData<MutableList<User>> {
        return userList
    }

    fun observeGroup(): LiveData<MutableList<GroupModel>> {
        return groupList
    }

    fun observeCanCreateGroup(): LiveData<Boolean> {
        return canCreateGroup
    }

    fun observeOperationMessage() : LiveData<String>{
        return operationMessage
    }
}