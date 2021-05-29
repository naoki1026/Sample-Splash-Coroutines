package com.example.sample_splash_coroutines.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sample_splash_coroutines.R
import java.text.DateFormat
import java.util.*

fun ImageView.loadUrl(url: String?) {
    context?.let {
        Glide.with(context.applicationContext)
            .load(url)
            .into(this)
    }
}

fun getDate(s: Long?) : String {
    s?.let {
        val df = DateFormat.getDateInstance()
        return df.format(Date(it))
    }
    return "不明"
}