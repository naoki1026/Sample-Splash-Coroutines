package com.example.sample_splash_coroutines.activities

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        auth = Firebase.auth

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

        binding.topMenu.setOnClickListener {
            val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if(auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}