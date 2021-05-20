package com.example.sample_splash_coroutines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.sample_splash_coroutines.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth  : FirebaseAuth
    private lateinit var emailET : EditText
    private lateinit var passwordET : EditText
    private lateinit var loginProcessLayout : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        emailET = binding.emailET
        passwordET = binding.passwordET
        loginProcessLayout = binding.loginProcessLayout
        auth = Firebase.auth

        binding.loginbutton.setOnClickListener {
            login()
        }

        binding.signupTV.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val view = binding.root
        setContentView(view)
    }

    fun login(){

        println(emailET.text.toString())
        println(passwordET.text.toString())


        if (emailET.text.isNullOrEmpty() && passwordET.text.isNullOrEmpty()) {
            Toast.makeText(this, "メールアドレスとパスワードを入力してください", Toast.LENGTH_SHORT).show()
        } else if (emailET.text.isNullOrEmpty()) {
            Toast.makeText(this, "メールアドレスを入力してください", Toast.LENGTH_SHORT).show()
        } else if (passwordET.text.isNullOrEmpty()) {
            Toast.makeText(this, "パスワードを入力してください", Toast.LENGTH_SHORT).show()
        } else {

            loginProcessLayout.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            delay(1000)
//                            Toast.makeText(this@LoginActivity, "ログインに成功しました", Toast.LENGTH_SHORT).show()
//                            val intent = Intent(this@LoginActivity, HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                            startActivity(intent)
//                            loginProcessLayout.visibility = View.INVISIBLE
//                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        loginProcessLayout.visibility = View.INVISIBLE
                        println(emailET.text.toString())
                        println(passwordET.text.toString())
                        Toast.makeText(this@LoginActivity, "ログインできませんでした", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

}
