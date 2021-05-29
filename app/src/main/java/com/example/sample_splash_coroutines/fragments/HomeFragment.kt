package com.example.sample_splash_coroutines.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.adapters.TweetListAdapter
import com.example.sample_splash_coroutines.databinding.FragmentHomeBinding
import com.example.sample_splash_coroutines.listener.TwitterListenerImp
import com.example.sample_splash_coroutines.util.DATA_TWEETS
import com.example.sample_splash_coroutines.util.DATA_TWEET_HASHTAGS
import com.example.sample_splash_coroutines.util.DATA_TWEET_USER_IDS
import com.example.sample_splash_coroutines.util.Tweet

class HomeFragment : TwitterFragment() {

    var _binding : FragmentHomeBinding? = null
    val binding get() =  _binding!!
    private lateinit var tweetList : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tweetList = binding.tweetList
        listener = TwitterListenerImp(binding.tweetList, currentUser, callback)

        tweetsAdapter = TweetListAdapter(userId!!, arrayListOf())
        tweetsAdapter?.setListener(listener)
        binding.tweetList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tweetsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        // 下にスワイプした場合の処理
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            updateList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun updateList() {
        tweetList.visibility = View.VISIBLE
        currentUser?.let {
            val tweets : ArrayList<Tweet> = arrayListOf()

            if (!it.followHashtags.isNullOrEmpty()){
                for(hashtag in it.followHashtags!!) {
                    db.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAGS, hashtag).get()
                        .addOnSuccessListener { list ->
                            for(document in list.documents){
                                val tweet = document.toObject(Tweet::class.java)
                                tweet?.let {
                                    tweets.add(it)
                                }
                                updateAdapter(tweets)
                                tweetList?.visibility = View.VISIBLE
                            }

                        }.addOnFailureListener { e ->
                            e.printStackTrace()
                            tweetList?.visibility = View.VISIBLE
                        }
                }
            }


            for( followusers in it.followUsers!!) {
                db.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_USER_IDS, followusers).get()
                    .addOnSuccessListener { list ->
                        for(document in list.documents){
                            val tweet = document.toObject(Tweet::class.java)
                            tweet?.let {
                                tweets.add(it)
                            }
                            updateAdapter(tweets)
                            tweetList?.visibility = View.VISIBLE
                        }

                    }.addOnFailureListener { e ->
                        e.printStackTrace()
                        tweetList?.visibility = View.VISIBLE
                    }
            }
        }
//        tweetList.visibility = View.VISIBLE
//        db.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAGS, currentHashtag).get()
//            .addOnCompleteListener { list ->
//                tweetList.visibility = View.VISIBLE
//                val tweets = arrayListOf<Tweet>()
//                for (document in list.result!!.documents){
//                    val tweet = document.toObject(Tweet::class.java)
//                    tweet?.let {tweets.add(it)}
//                }
//                val sortedTweets = tweets.sortedWith(compareByDescending { it.timestamp })
//                tweetsAdapter?.updateTweets(sortedTweets)
//            }
//            .addOnFailureListener { e ->
//                e.printStackTrace()
//            }
    }

    private fun updateAdapter(tweets: java.util.ArrayList<Tweet>) {
        val sortedTweets = tweets.sortedWith(compareByDescending { it.timestamp })
        tweetsAdapter?.updateTweets(removeDuplication(sortedTweets))
    }

    private fun removeDuplication(originalList : List<Tweet>) = originalList.distinctBy { it.tweetId }

}