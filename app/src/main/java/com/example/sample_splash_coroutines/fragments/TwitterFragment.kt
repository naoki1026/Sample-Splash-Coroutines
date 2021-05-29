package com.example.sample_splash_coroutines.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.sample_splash_coroutines.adapters.TweetListAdapter
import com.example.sample_splash_coroutines.listener.HomeCallback
import com.example.sample_splash_coroutines.listener.TweetListener
import com.example.sample_splash_coroutines.listener.TwitterListenerImp
import com.example.sample_splash_coroutines.util.Tweet
import com.example.sample_splash_coroutines.util.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.RuntimeException

abstract class TwitterFragment : Fragment() {

    // このクラスを継承しているクラスから以下の変数にアクセスすることができるようになる
    protected var tweetsAdapter: TweetListAdapter? = null
    protected var currentUser: User? = null
    protected val db: FirebaseFirestore = Firebase.firestore
    protected val userId = Firebase.auth.currentUser!!.uid
    protected var listener: TwitterListenerImp? = null
    protected var callback : HomeCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is HomeCallback ) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + "must implement HomeCallback")
        }
    }

    fun setUser(user: User?) {
        this.currentUser = user
        listener?.user = user
    }

    abstract fun updateList()

    // 継承先のクラスにおけるonResume
    override fun onResume() {
        super.onResume()
        println("onResume")
        updateList()
    }
}