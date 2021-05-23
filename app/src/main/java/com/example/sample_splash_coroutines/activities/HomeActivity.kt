package com.example.sample_splash_coroutines.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.databinding.ActivityHomeBinding
import com.example.sample_splash_coroutines.fragments.HomeFragment
import com.example.sample_splash_coroutines.fragments.MyActivityFragment
import com.example.sample_splash_coroutines.fragments.SearchFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth  : FirebaseAuth
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val myActivityFragment = MyActivityFragment()
    private lateinit var dialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        auth = Firebase.auth

        updateBottomNavigationView()

        binding.topMenu.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if(auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun updateBottomNavigationView(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.item_home -> {
                    binding.searchView.visibility = View.INVISIBLE
                    binding.twitterIcon.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                }
                R.id.item_search -> {
                    binding.searchView.visibility = View.VISIBLE
                    binding.twitterIcon.visibility = View.INVISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, searchFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                }
                R.id.item_light -> {
                    binding.searchView.visibility = View.INVISIBLE
                    binding.twitterIcon.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, myActivityFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                }
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

//    private fun loadingAnimation(){
//        var builder = android.app.AlertDialog.Builder(this)
//        builder.setView(R.layout.loading)
//        builder.setCancelable(false)
//
//        // 定義した変数に対して代入する
//        dialog = builder.create()
//        dialog.show()
//    }
}