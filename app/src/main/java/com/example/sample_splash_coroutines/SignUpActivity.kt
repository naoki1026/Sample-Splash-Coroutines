package com.example.sample_splash_coroutines

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.sample_splash_coroutines.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private var _binding : ActivitySignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var emailET : EditText
    private lateinit var passwordET : EditText
    private lateinit var usernameET : EditText
    private lateinit var loginProcessLayout : LinearLayout
    private lateinit var auth  : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        auth = Firebase.auth
        binding.returnloginTV.setOnClickListener {
            finish()
        }

        binding.signupbutton.setOnClickListener {
            emailET = binding.emailET
            passwordET = binding.emailET
            usernameET = binding.usernameET
            loginProcessLayout = binding.loginProcessLayout
            signup()
        }
        setContentView(binding.root)
    }

    fun signup(){
        if (emailET.text.isNullOrEmpty() && passwordET.text.isNullOrEmpty()) {
            Toast.makeText(this, "メールアドレスとパスワードを入力してください", Toast.LENGTH_SHORT).show()
        } else if (emailET.text.isNullOrEmpty()) {
            Toast.makeText(this, "メールアドレスを入力してください", Toast.LENGTH_SHORT).show()
        } else if (passwordET.text.isNullOrEmpty()) {
            Toast.makeText(this, "パスワードを入力してください", Toast.LENGTH_SHORT).show()
        } else {
            loginProcessLayout.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@SignUpActivity, "登録成功", Toast.LENGTH_SHORT).show()
//                        usernameUpdate()
                        val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                        startActivity(intent)
                        loginProcessLayout.visibility = View.INVISIBLE
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        loginProcessLayout.visibility = View.INVISIBLE
                    }
                }
        }
    }

    fun usernameUpdate(){
        val user = Firebase.auth.currentUser
        val username = usernameET.text.toString()

        if (!username.isEmpty()){
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()

            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                    }
                }
        }
    }
}