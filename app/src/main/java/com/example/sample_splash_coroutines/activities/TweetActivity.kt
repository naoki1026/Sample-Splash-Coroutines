package com.example.sample_splash_coroutines.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.databinding.ActivityTweetBinding
import com.example.sample_splash_coroutines.util.*
import com.google.android.material.internal.TextWatcherAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TweetActivity : AppCompatActivity() {

    var _binding : ActivityTweetBinding? = null
    val binding get() = _binding!!
    private lateinit var auth  : FirebaseAuth
    private lateinit var uid : String
    private lateinit var username : String
    private lateinit var profileImageUrl : String
    private var tweetImage : Uri? = null
    private lateinit var db : FirebaseFirestore
    private lateinit var dialog : android.app.AlertDialog
    private lateinit var storage : FirebaseStorage



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTweetBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage

        uid = intent.getStringExtra(PARAM_USER_ID)!!
        username = intent.getStringExtra(PARAM_USER_NAME)!!

        intent.getStringExtra(PARAM_USER_IMAGE_URL)?.let { user_image_url ->
            profileImageUrl = user_image_url
        }

        binding.closeButton.setOnClickListener {
            finish()
        }

        binding.tweetButton.setOnClickListener {
            postTweet()
        }

        binding.photoButton.setOnClickListener {
            selectPhoto()
        }
        setTextCounter()
        populateInfo()
        setContentView(binding.root)
    }

    private fun storeImage(imageUri : Uri?){
        imageUri?.let { imageUri ->
            tweetImage = imageUri
            binding.uploadImage.loadUrl(tweetImage.toString())
        }
    }

    private fun populateInfo(){
        if (!profileImageUrl.isNullOrBlank()){
            binding.profileImage.visibility = View.INVISIBLE
            binding.tweetCircleImageView.visibility = View.VISIBLE
            binding.tweetCircleImageView.loadUrl(profileImageUrl)
        } else {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    data?.data?.also { uri ->
                        storeImage(uri)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun postTweet(){
        val text = binding.tweetText.text.toString()
        val hashtags = getHashtags(text)
        val tweetId : DocumentReference = db.collection(DATA_TWEETS).document()
        val tweet = Tweet(tweetId.id, uid, arrayListOf(uid!!), username, text, profileImageUrl, "", System.currentTimeMillis(), hashtags, arrayListOf())

        loadingAnimation()
        binding.tweetButton.isEnabled = false
        tweetId.set(tweet)
            .addOnCompleteListener {
                println("tweetimage : ${tweetImage.toString()}")
                if (tweetImage == null) {
                    dialog.dismiss()
                    binding.tweetButton.isEnabled = false
                    finish()
                } else {
                    // 1.ストレージにアップする
                    val filePath: StorageReference =
                        storage.reference.child(DATA_TWEET_IMAGE).child(tweetId.id)
                    filePath.putFile(tweetImage!!).addOnCompleteListener {
                        // 2.ストレージのURLを反映する
                        filePath.downloadUrl
                            .addOnCompleteListener { uri ->
                            tweetId.update(DATA_USER_IMAGE_URL, uri.result.toString())
                                binding.uploadImage.visibility = View.INVISIBLE
                                binding.tweetButton.isEnabled = false
                                dialog.dismiss()
                                finish()
                            }
                            .addOnFailureListener {
                                binding.uploadImage.visibility = View.INVISIBLE
                                binding.tweetButton.isEnabled = true
                                dialog.dismiss()
                            }
                    }
                }
            }

    }

    private fun getHashtags( source: String): ArrayList<String>{
        val hashtags = arrayListOf<String>()
        var text = source

        // #が含まれている限り処理は行われる
        while(text.contains("#")) {
            var hashtag = ""

            // #の位置を抽出
            val hash = text.indexOf("#")

            // #を含まない文字
            text = text.substring(hash + 1)

            val firstSpace =
                if (text.indexOf("　") >= 0) text.indexOf("　") else text.indexOf(" ")
            val firstHash = text.indexOf("#")

            if(firstSpace == -1 && firstHash == -1){
                hashtag = text.substring(0)
            } else if (firstSpace != -1 && firstSpace < firstHash) {
                hashtag = text.substring(0, firstSpace)
                text = text.substring(firstSpace + 1)
            } else if (firstSpace > 0 && firstHash == -1){
                hashtag = text.substring(0, firstSpace)
                text = text.substring(0)
            } else if (firstSpace >-1 && firstHash == -1) {
                hashtag = text.substring(0, firstHash)
                text = text.substring(firstHash)
            }

            if (!hashtag.isNullOrEmpty()){
                hashtags.add(hashtag)
            }
        }

        return hashtags
    }

    private fun selectPhoto(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
        binding.tweetButton.isEnabled = true
        Toast.makeText(this, "Tapped", Toast.LENGTH_SHORT).show()
    }

    private fun setTextCounter(){
        val editText = binding.tweetText
        val countText = binding.countText
        countText.setText("140")

        editText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val countNumber = 140 - editText.text.length
                countText.setText(countNumber.toString())

                when (countNumber){
                    in 0..10 -> {
                        countText.setTextColor(Color.parseColor("#FFA500"))
                        binding.tweetButton.isEnabled = true
                    }

                    in 11..139 -> {
                        countText.setTextColor(Color.parseColor("#1DA1F2"))
                        binding.tweetButton.isEnabled = true
                    }

                    140 -> {
                        countText.setTextColor(Color.parseColor("#1DA1F2"))
                        binding.tweetButton.isEnabled = false
                    }

                    else -> {
                        countText.setTextColor(Color.RED)
                        binding.tweetButton.isEnabled = false
                    }
                }
            }
        })
    }

    private fun loadingAnimation(){
        var builder = android.app.AlertDialog.Builder(this@TweetActivity)
        builder.setView(R.layout.loading)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}