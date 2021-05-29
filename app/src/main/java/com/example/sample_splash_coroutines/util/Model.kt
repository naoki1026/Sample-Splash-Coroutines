package com.example.sample_splash_coroutines.util

data class User (
    val email : String? = null,
    val username : String? = null,
    val imageUrl : String? = null,
    val followHashtags : ArrayList<String>? = arrayListOf(),
    val followUsers : ArrayList<String>? = arrayListOf()
)

data class Tweet (
    val tweetId : String? = null,
    val userId : String? = null,
    val userIds : ArrayList<String> = arrayListOf(),
    val username : String? = null,
    val text : String? = null,
    val profileUrl : String? = null,
    val imageUrl : String? = null,
    val timestamp : Long? = null,
    val hashtags : ArrayList<String> = arrayListOf(),
    val likes : ArrayList<String> = arrayListOf()
        )