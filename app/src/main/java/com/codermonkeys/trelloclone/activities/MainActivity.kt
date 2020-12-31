package com.codermonkeys.trelloclone.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codermonkeys.trelloclone.R
import com.codermonkeys.trelloclone.adapters.BoardItemsAdapter
import com.codermonkeys.trelloclone.databinding.ActivityMainBinding
import com.codermonkeys.trelloclone.firebase.FirestoreClass
import com.codermonkeys.trelloclone.models.Board
import com.codermonkeys.trelloclone.models.User
import com.codermonkeys.trelloclone.utils.Constants.DOCUMENT_ID
import com.codermonkeys.trelloclone.utils.Constants.NAME
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navigationView: NavigationView
    private lateinit var headerView: View
    private lateinit var binding: ActivityMainBinding

    private lateinit var mUserName: String

    companion object {
        const val MY_PROFILE_REQUEST_CODE = 11
        const val CREATE_BOARD_REQUEST_CODE = 12
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationView = findViewById(R.id.nav_view)
        headerView = navigationView.inflateHeaderView(R.layout.nav_header_main)

        FirestoreClass().loadUserData(this, true)

        setUpActionBar()

        binding.navView.setNavigationItemSelectedListener(this)

        binding.appBarMain.fabCreateBoard.setOnClickListener {
            val createBoardIntent = Intent(this, CreateBoardActivity::class.java)
            createBoardIntent.putExtra(NAME, mUserName)
            startActivityForResult(createBoardIntent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(
                    Intent(this, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )
            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FirestoreClass().loadUserData(this)
        } else if(resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE) {
            FirestoreClass().getBoardList(this)
        } else {
            Log.e(TAG, "Cancelled")
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.appBarMain.toolbarMainActivity)
        binding.appBarMain.toolbarMainActivity.apply {
            setNavigationIcon(R.drawable.ic_action_navigation_menu)
            setNavigationOnClickListener {
                //Toggle Drawer
                toggleDrawer()
            }
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun updateNavigationUserDetails(user: User, readBoardList: Boolean) {

        mUserName = user.name

        //Setting user image into NavHeader
        Glide.with(this)
            .load(user.image)
            .fitCenter()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(
                headerView.findViewById(R.id.iv_profile_user_image)
            )

        headerView.findViewById<TextView>(R.id.tv_username).text = user.name

        if(readBoardList) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardList(this)
        }
    }

     fun populateBoardListToUi(boardList: ArrayList<Board>) {
        hideProgressDialog()
        if (boardList.size > 0) {
            binding.appBarMain.mainContent.apply {
                rvBoardsList.visibility = View.VISIBLE
                tvNoBoardsAvailable.visibility = View.GONE

                rvBoardsList.layoutManager = LinearLayoutManager(this@MainActivity)
                rvBoardsList.setHasFixedSize(true)

                val adapter = BoardItemsAdapter(this@MainActivity, boardList)
                rvBoardsList.adapter = adapter

                adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener {
                    override fun onClick(position: Int, model: Board) {
                        val taskIntent = Intent(this@MainActivity, TaskListActivity::class.java)
                        taskIntent.putExtra(DOCUMENT_ID, model.documentId)
                        startActivity(taskIntent)
                    }

                })
            }

        } else {
            binding.appBarMain.mainContent.apply {
                rvBoardsList.visibility = View.GONE
                tvNoBoardsAvailable.visibility = View.VISIBLE
            }
        }
    }
}