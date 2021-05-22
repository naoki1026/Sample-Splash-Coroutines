package com.example.sample_splash_coroutines.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.util.ValidateInput
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
    private lateinit var dialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        var emailET = binding.emailET
        var passwordET = binding.passwordET

        auth = Firebase.auth
        binding.loginbutton.setOnClickListener {
            var email_validate = ValidateInput().emailValidate(emailET, this)
            var password_validate = ValidateInput().passwordValidate(passwordET, this)
            if (email_validate && password_validate){
                CoroutineScope(Dispatchers.Main).launch {
                    loadingAnimation()
                    signIn(emailET.text.toString(), passwordET.text.toString())
                    delay(1000)
                    dialog.dismiss()
                }
            }
        }

        binding.signupTV.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val view = binding.root
        setContentView(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    val user = auth.currentUser

                    user?.let { user ->
                        intent.putExtra("email", user.email)
                        intent.putExtra("uid", user.uid)
                    }

                    startActivity(intent)
                    dialog.dismiss()

                } else {
                    Toast.makeText(baseContext, "入力内容が不正です",
                        Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
    }

    private fun loadingAnimation(){
        var builder = android.app.AlertDialog.Builder(this)
        builder.setView(R.layout.loading)
        builder.setCancelable(false)

        // 定義した変数に対して代入する
        dialog = builder.create()
        dialog.show()
    }
}
