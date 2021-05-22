package com.example.sample_splash_coroutines.activities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.sample_splash_coroutines.fragments.HomeFragment
import com.example.sample_splash_coroutines.fragments.MyActivityFragment
import com.example.sample_splash_coroutines.fragments.SearchFragment

class MainFragmentStatePagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> HomeFragment()
            1 -> SearchFragment()
            else -> MyActivityFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}