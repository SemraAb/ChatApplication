package com.semraabdurahmanli.chatapprealtime.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.semraabdurahmanli.chatapprealtime.data.response.model.GroupModel
import com.semraabdurahmanli.chatapprealtime.data.response.model.User
import com.semraabdurahmanli.chatapprealtime.network.FirebaseNetwork
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private var user = MutableLiveData<User>()
    private var group = MutableLiveData<GroupModel>()
    fun getUser(userId : String){
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseNetwork.databaseReference.child("User").orderByChild("uid").equalTo(userId).addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.exists()){
                        user.postValue(snapshot.getValue<User>())
                    }
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

    fun getGroup(groupKey : String){
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseNetwork.databaseReference.child("Group").child(groupKey).child("GroupInfo").addChildEventListener(object  : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    group.postValue(snapshot.getValue<GroupModel>())
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

    fun observeUser() : LiveData<User>{
        return user
    }
    fun observeGroup() : LiveData<GroupModel>{
        return group
    }
}