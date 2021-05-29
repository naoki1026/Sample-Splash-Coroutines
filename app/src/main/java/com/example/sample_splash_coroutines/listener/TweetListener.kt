package com.example.sample_splash_coroutines.listener

import com.example.sample_splash_coroutines.util.Tweet

interface TweetListener {
    fun onLayoutClick(tweet: Tweet?)
    fun onLike(tweet: Tweet?)
    fun onReTweet(tweet: Tweet?)
}
