package com.example.sample_splash_coroutines.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.util.ValidateInput
import com.example.sample_splash_coroutines.databinding.ActivitySignUpBinding
import com.example.sample_splash_coroutines.util.DATA_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private var _binding : ActivitySignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog : android.app.AlertDialog
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db  = Firebase.firestore

        var emailET = binding.emailET
        var passwordET = binding.passwordET
        var usernameET = binding.usernameET

        binding.returnloginTV.setOnClickListener {
            finish()
        }

        binding.signupbutton.setOnClickListener {
            var email_validate = ValidateInput().emailValidate(emailET, this)
            var password_validate = ValidateInput().passwordValidate(passwordET, this)

            CoroutineScope(Dispatchers.Main).launch {
                loadingAnimation()
                if (email_validate && password_validate){
                    createAccount(emailET.text.toString(), passwordET.text.toString(), usernameET.text.toString())
                }
            }
        }
        setContentView(binding.root)
    }

    private fun createAccount(email: String, password: String, userName : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = com.example.sample_splash_coroutines.util.User(email, userName, "", null, null)
                    db.collection(DATA_USERS).document(auth.uid!!).set(user)

                    Toast.makeText(baseContext, "アカウントが登録されました",
                            Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "すでに登録済みのメールアドレスです",
                        Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
    }


    fun loadingAnimation(){
        var builder = android.app.AlertDialog.Builder(this)
        builder.setView(R.layout.loading)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()

    }
}