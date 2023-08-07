package com.semraabdurahmanli.chatapprealtime.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.semraabdurahmanli.chatapprealtime.databinding.ActivityHomeBinding
import com.semraabdurahmanli.chatapprealtime.ui.adapter.ChatUserAdapter
import com.semraabdurahmanli.chatapprealtime.ui.adapter.ChatroomAdapter
import com.semraabdurahmanli.chatapprealtime.ui.adapter.OnItemChatClickListener
import com.semraabdurahmanli.chatapprealtime.ui.adapter.OnItemClickListener
import com.semraabdurahmanli.chatapprealtime.data.response.model.GroupModel
import com.semraabdurahmanli.chatapprealtime.data.response.model.User
import com.semraabdurahmanli.chatapprealtime.network.FirebaseNetwork
import com.semraabdurahmanli.chatapprealtime.ui.group.GroupActivity
import com.semraabdurahmanli.chatapprealtime.ui.signin.GoogleSignInActivity
import com.semraabdurahmanli.chatapprealtime.ui.single.ChatActivity
import com.semraabdurahmanli.chatapprealtime.ui.view_model.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.semraabdurahmanli.chatapprealtime.R
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class HomeActivity : AppCompatActivity(), OnItemClickListener, OnItemChatClickListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private var hasNotificationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        //Permission
        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }

        //UI
        binding.groupNameET.visibility = View.GONE
        binding.addGroupButton.visibility = View.GONE
        binding.textView.visibility = View.GONE
        Picasso.get()
            .load(FirebaseNetwork.auth.currentUser?.photoUrl)
            .transform(CropCircleTransformation()).resize(150, 150)
            .into(binding.imageView)
        binding.usernameId.text = FirebaseNetwork.auth.currentUser?.displayName

        //Recycler Views
        binding.chatroomRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        viewModel.getGroups()
        viewModel.observeGroup().observe(this, Observer {
            binding.chatroomRV.adapter = ChatroomAdapter(it, this)
        })
        binding.singleChatRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewModel.getUsers()
        viewModel.observeUsers().observe(this, Observer {
            binding.singleChatRV.adapter = ChatUserAdapter(it, this)
        })

        viewModel.observeCanCreateGroup().observe(this, Observer {
            if (it) {
                binding.groupNameET.visibility = View.VISIBLE
                binding.addGroupButton.visibility = View.VISIBLE
                binding.textView.visibility = View.VISIBLE
            } else {
                binding.groupNameET.visibility = View.GONE
                binding.addGroupButton.visibility = View.GONE
                binding.textView.visibility = View.GONE
            }
        })


        viewModel.observeOperationMessage().observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            startActivity(Intent(this, GoogleSignInActivity::class.java))
            finish()
        })

        viewModel.updateFcmToken()
        setUpClickListeners()

    }

    fun setUpClickListeners() {
        binding.addGroupButton.setOnClickListener {
            viewModel.addGroup(binding.groupNameET.text.toString())
            binding.groupNameET.setText("")
        }

        binding.signOutImageView.setOnClickListener {
            viewModel.signOut()
            viewModel.deleteFcmToken()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            GoogleSignIn.getClient(this, gso).signOut()
        }
    }

    override fun onItemClick(item: GroupModel) {
        val intent = Intent(this@HomeActivity, GroupActivity::class.java)
        intent.putExtra("Group", item)
        startActivity(intent)
    }

    override fun onItemClick(user: User) {
        val intent = Intent(this@HomeActivity, ChatActivity::class.java)
        intent.putExtra("User", user)
        startActivity(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }


    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                            showNotificationPermissionRationale()
                        } else {
                            showSettingDialog()
                        }
                    }
                }
            } else {

            }
        }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale() {

        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}