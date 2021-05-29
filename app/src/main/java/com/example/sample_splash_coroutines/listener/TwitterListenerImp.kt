package com.example.sample_splash_coroutines.listener

import android.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.sample_splash_coroutines.util.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

// TweetListenerを継承している
class TwitterListenerImp(val tweetList: RecyclerView, var user: User?, val callback : HomeCallback?) : TweetListener {
    private val auth = Firebase.auth
    private val userId = auth.currentUser?.uid
    private var db = Firebase.firestore

    override fun onLayoutClick(tweet: Tweet?) {
        println("click")
        tweet?.let {
            val owner = tweet.userIds?.get(0)
            println("owner : ${owner}")
            println("userId ${userId}")
            if(owner != userId) {
                if (user?.followUsers?.contains(owner) == true) {
                    AlertDialog.Builder(tweetList.context)
                        .setTitle("${tweet.username}のフォローを解除しますか？")
                        .setPositiveButton("はい") { dialog, which ->
                            tweetList.isClickable = false

                            val followedUsers = if (user?.followUsers.isNullOrEmpty()) arrayListOf() else  user?.followUsers
                            followedUsers?.remove(owner)

                            db.collection(DATA_USERS).document(userId!!)
                                .update(DATA_USER_FOLLOW, followedUsers)
                                .addOnSuccessListener {
                                    tweetList.isClickable = true
                                    callback?.onRefresh()
                                }.addOnFailureListener {
                                    tweetList.isClickable = true
                                }
                        }.setNegativeButton("いいえ") { dialog, which -> }
                        .show()
                } else {
                    AlertDialog.Builder(tweetList.context)
                        .setTitle("${tweet.username}をフォローしますか？")
                        .setPositiveButton("はい") { dialog, which ->
                            tweetList.isClickable = false

                            val followedUsers = if (user?.followUsers.isNullOrEmpty()) arrayListOf() else  user?.followUsers
                            followedUsers?.add(owner)

                            db.collection(DATA_USERS).document(userId!!)
                                .update(DATA_USER_FOLLOW, followedUsers)
                                .addOnSuccessListener {
                                    tweetList.isClickable = true
                                    callback?.onRefresh()
                                }.addOnFailureListener {
                                    tweetList.isClickable = true
                                }
                        }.setNegativeButton("いいえ") { dialog, which -> }
                        .show()
                }
            }

        }
    }

    override fun onLike(tweet: Tweet?) {
        tweet?.let {
            tweetList.isClickable = false
            val likes = tweet.likes
            if(tweet.likes?.contains(userId) == true) {
                likes?.remove(userId)
            } else {
                likes?.add(userId!!)
            }
            db.collection(DATA_TWEETS).document(tweet.tweetId!!).update(DATA_TWEET_LIKES, likes)
                .addOnCompleteListener {
                    tweetList.isClickable = true
                    callback?.onRefresh()
                }.addOnFailureListener {
                    tweetList.isClickable = true
                }
        }
    }

    override fun onReTweet(tweet: Tweet?) {
        tweet?.let {
            tweetList.isClickable = false
            val retweet = tweet.userIds
            if(tweet.userIds?.contains(userId) == true) {
                retweet?.remove(userId)
            } else {
                retweet?.add(userId!!)
            }
            db.collection(DATA_TWEETS).document(tweet.tweetId!!).update(DATA_TWEET_USER_IDS, retweet)
                .addOnCompleteListener {
                    tweetList.isClickable = true
                    callback?.onRefresh()
                }.addOnFailureListener {
                    tweetList.isClickable = true
                }

        }

    }
}