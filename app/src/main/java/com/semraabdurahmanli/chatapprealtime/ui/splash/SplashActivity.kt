package com.semraabdurahmanli.chatapprealtime.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.semraabdurahmanli.chatapprealtime.databinding.ActivitySplashBinding
import com.semraabdurahmanli.chatapprealtime.network.FirebaseNetwork
import com.semraabdurahmanli.chatapprealtime.ui.group.GroupActivity
import com.semraabdurahmanli.chatapprealtime.ui.home.HomeActivity
import com.semraabdurahmanli.chatapprealtime.ui.signin.GoogleSignInActivity
import com.semraabdurahmanli.chatapprealtime.ui.single.ChatActivity
import com.semraabdurahmanli.chatapprealtime.ui.view_model.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    private lateinit var viewModel : SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SplashViewModel::class.java]

        if(FirebaseNetwork.auth.currentUser != null && intent.extras != null){
            val isGroup = intent.extras!!.getString("isGroup").toBoolean()
            val homeIntent = Intent(this, HomeActivity::class.java)
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(homeIntent)
            if(isGroup){
                val groupKey = intent.extras!!.getString("groupKey")
                viewModel.getGroup(groupKey.toString())
                viewModel.observeGroup().observe(this, Observer {
                    val intent = Intent(this, GroupActivity::class.java)
                    intent.putExtra("Group", it)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                })
            }else{
                val userId = intent.extras!!.getString("userId")
                userId?.let { viewModel.getUser(it) }
                viewModel.observeUser().observe(this, Observer {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("User", it)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                })
            }


        }else{
            //When Open App
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, GoogleSignInActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)
        }

    }
}