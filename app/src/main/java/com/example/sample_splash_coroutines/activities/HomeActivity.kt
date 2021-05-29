package com.example.sample_splash_coroutines.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.databinding.ActivityHomeBinding
import com.example.sample_splash_coroutines.fragments.HomeFragment
import com.example.sample_splash_coroutines.fragments.MyActivityFragment
import com.example.sample_splash_coroutines.fragments.SearchFragment
import com.example.sample_splash_coroutines.fragments.TwitterFragment
import com.example.sample_splash_coroutines.listener.HomeCallback
import com.example.sample_splash_coroutines.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity(), HomeCallback {

    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private val auth  = Firebase.auth
    private val uid = auth.uid
    private var db = Firebase.firestore
    private var user : User? = null
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val myActivityFragment = MyActivityFragment()
    private var currentFragment : TwitterFragment = homeFragment
    private lateinit var username: String
    private lateinit var email : String
    private lateinit var imageUrl : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)

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

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrEmpty()){
                    searchFragment.newHashtag(query!!)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if(auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            populateInfo()
        }
    }

    override fun onUserUpdated() {
        super.onUserUpdated()
        populateInfo()
    }

    override fun onRefresh() {
        currentFragment.updateList()
    }

    private fun populateInfo(){
        if(auth.currentUser != null) {
            db.collection(DATA_USERS).document(uid!!).get()
                .addOnSuccessListener{ documentSnapShop ->
                    user = documentSnapShop.toObject(User::class.java)
                    user?.let {
                        username = it.username!!
                        email = it.email!!
                        it.imageUrl?.let { userImageUrl ->
                            imageUrl = userImageUrl
                        }
                    }
                    updateFragmentUser()
                }
                .addOnFailureListener {  e ->
                    e.printStackTrace()
                    finish()
                }
        }

    }

    private fun updateFragmentUser() {
        homeFragment.setUser(user)
        searchFragment.setUser(user)
        myActivityFragment.setUser(user)
        currentFragment.updateList()
    }

    private fun updateBottomNavigationView(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.item_home -> {
                    currentFragment = homeFragment
                    binding.searchView.visibility = View.INVISIBLE
                    binding.twitterIcon.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                }
                R.id.item_search -> {
                    currentFragment = searchFragment
                    binding.searchView.visibility = View.VISIBLE
                    binding.twitterIcon.visibility = View.INVISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, searchFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                }
                R.id.item_light -> {
                    currentFragment = myActivityFragment
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
}