package com.example.sample_splash_coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sample_splash_coroutines.databinding.ActivityHomeBinding
import com.example.sample_splash_coroutines.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    var _binding : ActivityHomeBinding? = null
    val binding get() = _binding!!
    private lateinit var auth  : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        auth = Firebase.auth
        binding.topMenu.setOnClickListener {
            kotlin.runCatching {
                auth.signOut()
            }.onSuccess {
                Toast.makeText(this, "ログアウトに成功しました", Toast.LENGTH_SHORT).show()
                finish()

            }.onFailure { e ->
                Toast.makeText(this, "${e}", Toast.LENGTH_SHORT).show()
            }
        }
        setContentView(binding.root)
    }
}