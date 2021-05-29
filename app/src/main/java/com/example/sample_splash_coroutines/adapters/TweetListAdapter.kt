package com.example.sample_splash_coroutines.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.util.Tweet
import com.example.sample_splash_coroutines.databinding.ItemTweetBinding
import com.example.sample_splash_coroutines.listener.TweetListener
import com.example.sample_splash_coroutines.util.getDate
import com.example.sample_splash_coroutines.util.loadUrl

class TweetListAdapter(val userId : String, val tweets : ArrayList<Tweet>) : RecyclerView.Adapter<TweetListAdapter.ViewHolder>() {
    private var listener : TweetListener? = null

    fun setListener(listener: TweetListener?){
        this.listener = listener
    }

    fun updateTweets(newTweets: List<Tweet>){
        tweets.clear()
        tweets.addAll(newTweets)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemTweetBinding) : RecyclerView.ViewHolder(binding.root) {

        private val layout = binding.tweetLayout
        private val username = binding.username
        private val text = binding.tweetText
        private val image = binding.tweetImage
        private val date = binding.timestamp
        private val like = binding.likeImage
        private val likeCount = binding.likeText
        private val retweet = binding.retweetImage
        private val retweetCount = binding.retweetText
        private val userid = binding.userid
        private val profile = binding.profileImage
        private val circleImage = binding.tweetCircleImageView

        fun bind(userId: String, tweet: Tweet, listener: TweetListener?){
            username.text = tweet.username
            userid.text = tweet.userId
            text.text = tweet.text

            if(!tweet.profileUrl.isNullOrEmpty()){
                profile.visibility = View.INVISIBLE
                circleImage.visibility = View.VISIBLE
                circleImage.loadUrl(tweet.profileUrl)
            } else {
                profile.visibility = View.VISIBLE
                circleImage.visibility = View.INVISIBLE
            }

            if (!tweet.imageUrl.isNullOrEmpty()){
                image.visibility = View.VISIBLE
                image.loadUrl(tweet.imageUrl)
            } else {
                image.visibility = View.INVISIBLE
            }

            date.text = getDate(tweet.timestamp)
            likeCount.text = tweet.likes?.size.toString()
            retweetCount.text = tweet.userIds?.size?.minus(1).toString()

            circleImage.setOnClickListener { listener?.onLayoutClick(tweet) }
            like.setOnClickListener { listener?.onLike(tweet)}


            retweet.setOnClickListener {
                listener?.onReTweet(tweet)
            }

            if(tweet.likes.contains(userId) == true){
                like.setImageDrawable(ContextCompat.getDrawable(like.context, R.drawable.ic_baseline_favorite_24))
            } else {
                like.setImageDrawable(ContextCompat.getDrawable(like.context, R.drawable.ic_baseline_favorite_border_24))
            }

            if(tweet.userIds?.get(0).equals(userId)){
                retweet.isClickable = false
                retweet.setImageDrawable(ContextCompat.getDrawable(like.context, R.drawable.ic_baseline_refresh_24))
            } else if (tweet.userIds?.contains(userId) == true) {
                retweet.isClickable = true
                retweet.setImageDrawable(ContextCompat.getDrawable(like.context, R.drawable.ic_baseline_refresh_green))
            } else {
                retweet.isClickable = true
                retweet.setImageDrawable(ContextCompat.getDrawable(like.context, R.drawable.ic_baseline_refresh_24))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userId, tweets[position],listener)
    }

    override fun getItemCount(): Int = tweets.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTweetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}
