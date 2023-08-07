package com.semraabdurahmanli.chatapprealtime.data.response.model

import java.io.Serializable

data class User(val canCreateGroup: Boolean? = false, val name: String? = "", val photoUrl: String? = "", val uid: String? = "", val email: String? = "", val fcmToken : String? = "" ) : Serializable

