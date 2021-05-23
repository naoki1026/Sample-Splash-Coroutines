package com.example.sample_splash_coroutines.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.databinding.ActivityProfileBinding
import com.example.sample_splash_coroutines.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private var _binding : ActivityProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth  : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var dialog : android.app.AlertDialog
    private lateinit var storage : FirebaseStorage
    private lateinit var imageUrl : String
    private val scope = CoroutineScope(Dispatchers.Default)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db  = Firebase.firestore
        storage = Firebase.storage


        var emailET = binding.emailET
        var usernameET = binding.usernameET
        binding.circleImageView.setImageDrawable(null)
        binding.circleImageView.destroyDrawingCache()

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

    override fun onResume() {
        super.onResume()
        auth = Firebase.auth
        db  = Firebase.firestore
        storage = Firebase.storage
        println("読み込み : resume")
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        db  = Firebase.firestore
        storage = Firebase.storage
        println("読み込み : onStart")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                loadingAnimation()
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

    private fun populateInfo(){
        db.collection(DATA_USERS).document("${auth.currentUser!!.uid}").get()
            .addOnSuccessListener{ documentSnapShop ->
                val user : User? = documentSnapShop.toObject(User::class.java)
                binding.usernameET.setText(user?.username)
                binding.emailET.setText(user?.email)

                val storageRef = FirebaseStorage.getInstance().reference
                val imageRef = storageRef.child("ProfileImage/${auth.currentUser!!.uid}")

                Glide.with(this)
                    .load(imageRef)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(findViewById<ImageView>(R.id.circleImageView))
            }
            .addOnFailureListener {  e ->
                e.printStackTrace()
                finish()
            }
    }

    private fun storeImage(imageUri : Uri?){
            imageUri?.let { imageUri ->
                val filePath : StorageReference = storage.reference.child(DATA_IMAGES).child(auth.currentUser!!.uid)
                    filePath.putFile(imageUri).addOnCompleteListener {
                        println("アップ成功")
                        filePath.downloadUrl
                            .addOnCompleteListener { uri ->
                                val url = uri.toString()
                                db.collection(DATA_USERS).document(auth.currentUser!!.uid).update(
                                    DATA_USER_IMAGE_URL, url)
                                imageUrl = imageUri.toString()

                                scope.launch {
                                    myTask()
                                }

                            }
                        dialog.dismiss()
                    }
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
                    .addOnCompleteListener {
                        Toast.makeText(this, "更新されました", Toast.LENGTH_SHORT).show()
                    }
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

    fun loadingAnimation(){
        var builder = android.app.AlertDialog.Builder(this@ProfileActivity)
        builder.setView(R.layout.loading)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    private suspend fun myTask() {
        try {
            binding.circleImageView.setImageURI(null)
            binding.circleImageView.destroyDrawingCache()

            // doInBackgroundメソッドと同等の処理
            Glide.get(this).clearDiskCache()

            Thread.sleep(800)
            // onPreExecuteと同等の処理

            // onPostExecuteメソッドと同等の処理
            withContext(Dispatchers.Main) {
                Glide.get(this@ProfileActivity).clearMemory()
                Glide.with(this@ProfileActivity)
                    .load(imageUrl)
                    .into(binding.circleImageView)
                Toast.makeText(this@ProfileActivity, "画像を変更しました", Toast.LENGTH_SHORT).show()

            }
        } catch (e: Exception) {
            // onCancelledメソッドと同等の処理
            Log.e(localClassName, "ここにキャンセル時の処理を記述", e)
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}