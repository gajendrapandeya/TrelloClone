package com.codermonkeys.trelloclone.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.codermonkeys.trelloclone.R
import com.codermonkeys.trelloclone.databinding.ActivityCreateBoardBinding
import com.codermonkeys.trelloclone.firebase.FirestoreClass
import com.codermonkeys.trelloclone.models.Board
import com.codermonkeys.trelloclone.utils.Constants
import com.codermonkeys.trelloclone.utils.Constants.NAME
import com.codermonkeys.trelloclone.utils.Constants.PICK_IMAGE_REQUEST_CODE
import com.codermonkeys.trelloclone.utils.Constants.READ_STORAGE_PERMISSION_CODE
import com.google.firebase.storage.FirebaseStorage
import com.sdsmdg.tastytoast.TastyToast
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateBoardBinding
    private lateinit var mUserName: String
    private var mSelectedImageFileUri: Uri? = null
    private var mBoardImageUrl = ""

    companion object {
        private const val TAG = "CreateBoardActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        if (intent.hasExtra(NAME)) {
            mUserName = intent.getStringExtra(NAME)!!
        }

        binding.ivBoardImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnCreate.setOnClickListener {
            //Here we are able to create board without seleced image or board name
            if(mSelectedImageFileUri != null) {
                uploadBoardImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                TastyToast.makeText(
                    this,
                    "Oops!! You just denied the permission for storage. You can also allow it from the settings ",
                    TastyToast.LENGTH_LONG, TastyToast.INFO
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data

            try {
                Glide.with(this)
                    .load(mSelectedImageFileUri)
                    .fitCenter()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivBoardImage)
            } catch (exception: IOException) {
                exception.printStackTrace()
            }

        }
    }


    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            it.title = resources.getString(R.string.create_board_title)
        }
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun createBoard() {
        val assignedUserArrayList = ArrayList<String>()
        assignedUserArrayList.add(getCurrentUserId())

        val board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageUrl,
            mUserName,
            assignedUserArrayList
        )

        FirestoreClass().createBoard(this, board)
    }

    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        mSelectedImageFileUri?.let {
            val sRef = FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE ${System.currentTimeMillis()}.${
                    Constants.getFileExtension(this, it)
                }"
            )
            sRef.putFile(it).addOnSuccessListener { taskSnapshot ->
                Log.i(TAG, taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.i(TAG, uri.toString())
                    mBoardImageUrl = uri.toString()

                    createBoard()
                }
            }.addOnFailureListener { exception ->
                hideProgressDialog()
                TastyToast.makeText(
                    this,
                    exception.message,
                    TastyToast.LENGTH_SHORT,
                    TastyToast.ERROR
                ).show()
            }
        }
    }

    fun boardCreateSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}