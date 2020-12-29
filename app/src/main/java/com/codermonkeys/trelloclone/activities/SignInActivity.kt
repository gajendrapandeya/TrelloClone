package com.codermonkeys.trelloclone.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.codermonkeys.trelloclone.R
import com.codermonkeys.trelloclone.databinding.ActivitySignInBinding
import com.codermonkeys.trelloclone.firebase.FirestoreClass
import com.codermonkeys.trelloclone.models.User
import com.google.firebase.auth.FirebaseAuth
import com.sdsmdg.tastytoast.TastyToast

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        auth = FirebaseAuth.getInstance()

        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignInActivity)
        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun signInSuccess(user: User) {
        hideProgressDialog()
        TastyToast.makeText(this, "Signed In Successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun signInRegisteredUser() {
        val email = binding.etEmail.text.toString().trim { it <= ' '}
        val password = binding.etPassword.text.toString()

        if(validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                hideProgressDialog()
                if(task.isSuccessful) {
                    FirestoreClass().loadUserData(this)
                } else {
                    TastyToast.makeText(this, task.exception?.message, TastyToast.LENGTH_SHORT, TastyToast.ERROR).show()
                }
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email")
                false
            }

            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password")
                false
            }

            else -> true
         }
    }

}