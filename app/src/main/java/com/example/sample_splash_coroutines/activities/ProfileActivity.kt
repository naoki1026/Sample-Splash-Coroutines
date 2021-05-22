package com.example.sample_splash_coroutines.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.databinding.ActivityProfileBinding
import com.example.sample_splash_coroutines.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private var _binding : ActivityProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth  : FirebaseAuth
    private lateinit var db : FirebaseFirestore
//    private lateinit var dialog : AlertDialog
    private lateinit var storage : FirebaseStorage
    private var imageUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db  = Firebase.firestore
        storage = Firebase.storage

        var emailET = binding.emailET
        var usernameET = binding.usernameET

        CoroutineScope(Dispatchers.Main).launch {
            populateInfo()
        }

        binding.circleImageView.setOnClickListener{
            selectPhoto()

        }

        binding.signoutButton.setOnClickListener {
            auth.signOut()
            finish()
        }

        binding.applyButton.setOnClickListener {
            var email_validate = ValidateInput().emailValidate(emailET, this)
            var username_validate = ValidateInput().usernameValidater(usernameET, this)

            CoroutineScope(Dispatchers.Main).launch {
                if ( username_validate && email_validate ){
                    onApply()
                }
            }
        }

        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
                        val inputStream = contentResolver?.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        val imageView = findViewById<ImageView>(R.id.circleImageView)
                        imageView.setImageBitmap(image)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun populateInfo(){
        db.collection(DATA_USERS).document("${auth.currentUser!!.uid}").get()
            .addOnSuccessListener{ documentSnapShop ->
                val user : User? = documentSnapShop.toObject(User::class.java)
                binding.usernameET.setText(user?.username)
                binding.emailET.setText(user?.email)
                imageUrl?.let {

                }
            }
            .addOnFailureListener {  e ->
                e.printStackTrace()
                finish()
            }
    }

    private fun onApply(){
        val username = binding.usernameET.text.toString()
        val email = binding.emailET.text.toString()
        val map = HashMap<String, Any>()
        map[DATA_USER_USERNAME] = username
        map[DATA_USER_EMAIL] = email

        db.collection(DATA_USERS).document(auth.currentUser!!.uid).update(map)
            .addOnSuccessListener {
                auth.currentUser!!.updateEmail(email)
                Toast.makeText(this, "更新されました", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {  e ->
                e.printStackTrace()
                Toast.makeText(this, "更新されませんでした", Toast.LENGTH_SHORT).show()
            }
    }

    private fun selectPhoto(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
        Toast.makeText(this, "Tapped", Toast.LENGTH_SHORT).show()
    }

//    private fun loadingAnimation() {
//        var builder = android.app.AlertDialog.Builder(this)
//        builder.setView(R.layout.loading)
//        builder.setCancelable(false)
//
//        // 定義した変数に対して代入する
//        dialog = builder.create()
//        dialog.show()
//    }


}