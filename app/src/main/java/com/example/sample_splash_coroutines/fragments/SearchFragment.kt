package com.example.sample_splash_coroutines.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample_splash_coroutines.adapters.TweetListAdapter
import com.example.sample_splash_coroutines.databinding.FragmentSearchBinding
import com.example.sample_splash_coroutines.listener.TwitterListenerImp
import com.example.sample_splash_coroutines.util.*

class SearchFragment : TwitterFragment() {

    var _binding : FragmentSearchBinding? = null
    val binding get() = _binding!!
    private var currentHashtag = ""
    private var hashtagFolllow = false
    private lateinit var tweetList : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tweetList = binding.tweetList
        listener = TwitterListenerImp(binding.tweetList, currentUser, callback)


        db.collection(DATA_USERS).document(userId).get()
            .addOnSuccessListener { documentSnapShop ->
                currentUser = documentSnapShop.toObject(User::class.java)!!
                hashtagFolllow = (!currentUser!!.followHashtags.isNullOrEmpty())
            }

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

        binding.floatingSaveButton.setOnClickListener {
            binding.floatingSaveButton.isClickable = false
            var followed : MutableList<String>? = arrayListOf()
            if (hashtagFolllow) {
                followed = currentUser?.followHashtags
                followed?.remove(currentHashtag)
            } else {
                followed?.add(currentHashtag)
            }
            db.collection(DATA_USERS).document(userId).update(DATA_USER_HASHTAGS, followed)
                .addOnCompleteListener {
                    callback?.onUserUpdated()
                    binding.floatingSaveButton.isClickable = true
                }.addOnFailureListener {
                    binding.floatingSaveButton.isClickable = true
                }
            hashtagFolllow = (!currentUser!!.followHashtags.isNullOrEmpty())
        }
    }

    fun newHashtag(term : String){
        currentHashtag = term
        updateList()
    }

    override fun updateList() {
        tweetList.visibility = View.VISIBLE
        db.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAGS, currentHashtag).get()
            .addOnCompleteListener { list ->
                tweetList.visibility = View.VISIBLE
                val tweets = arrayListOf<Tweet>()
                for (document in list.result!!.documents){
                    val tweet = document.toObject(Tweet::class.java)
                    tweet?.let {tweets.add(it)}
                }
                val sortedTweets = tweets.sortedWith(compareByDescending { it.timestamp })
                tweetsAdapter?.updateTweets(sortedTweets)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}