package com.example.sample_splash_coroutines.util

data class User (
    val email : String? = null,
    val username : String? = null,
    val imageUrl : String? = null,
    val followHashtags : Array<String>? = null,
    val followUsers : Array<String>? = null
)