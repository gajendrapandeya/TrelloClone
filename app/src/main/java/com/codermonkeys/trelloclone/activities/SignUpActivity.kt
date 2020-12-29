package com.codermonkeys.trelloclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.codermonkeys.trelloclone.R
import com.codermonkeys.trelloclone.databinding.ActivitySignUpBinding
import com.codermonkeys.trelloclone.firebase.FirestoreClass
import com.codermonkeys.trelloclone.models.User
import com.google.firebase.auth.FirebaseAuth
import com.sdsmdg.tastytoast.TastyToast

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun userRegisteredSuccess() {
        hideProgressDialog()
        TastyToast.makeText(
            this,
            "Registration Successfull",
            TastyToast.LENGTH_LONG,
            TastyToast.SUCCESS
        ).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim { it <= ' ' }
        val email = binding.etEmail.text.toString().trim { it <= ' ' }
        val password = binding.etPassword.text.toString()

        if (validateForm(name, email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!

                        val user = User(firebaseUser.uid, name, registeredEmail)

                        FirestoreClass().registerUser(this, user)

                    } else {
                        TastyToast.makeText(this, task.exception?.message, TastyToast.LENGTH_SHORT, TastyToast.ERROR).show()
                    }
                }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please Enter a name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please Enter a email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please Enter a password")
                false
            }
            else -> true
        }
    }
}