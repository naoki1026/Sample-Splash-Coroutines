package com.example.sample_splash_coroutines.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.databinding.ActivityHomeBinding
import com.example.sample_splash_coroutines.fragments.HomeFragment
import com.example.sample_splash_coroutines.fragments.MyActivityFragment
import com.example.sample_splash_coroutines.fragments.SearchFragment
import com.example.sample_splash_coroutines.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth  : FirebaseAuth
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val myActivityFragment = MyActivityFragment()
    private lateinit var db : FirebaseFirestore
    private lateinit var imageUrl : String
    private lateinit var username: String
    private lateinit var email : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore

        populateInfo()
        updateBottomNavigationView()

        binding.topMenu.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra( PARAM_USER_NAME, username)
            intent.putExtra( PARAM_USER_EMAIL, email)
            intent.putExtra( PARAM_USER_IMAGE_URL, imageUrl)
            startActivity(intent)
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, TweetActivity::class.java)
            intent.putExtra(PARAM_USER_NAME, username)
            intent.putExtra(PARAM_USER_ID, auth.currentUser!!.uid)
            intent.putExtra(PARAM_USER_IMAGE_URL, imageUrl)
            startActivity(intent)
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

    private fun populateInfo(){
        db.collection(DATA_USERS).document("${auth.currentUser!!.uid}").get()
            .addOnSuccessListener{ documentSnapShop ->
                val user : User? = documentSnapShop.toObject(User::class.java)
                user?.let {
                    username = it.username!!
                    email = it.email!!
                    it.imageUrl?.let { userImageUrl ->
                        imageUrl = userImageUrl
                    }
                    println("username : ${username}")
                    println("email : ${email}")
                }
            }
            .addOnFailureListener {  e ->
                e.printStackTrace()
                finish()
            }

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