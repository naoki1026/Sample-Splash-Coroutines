package com.example.sample_splash_coroutines.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sample_splash_coroutines.R
import com.example.sample_splash_coroutines.databinding.FragmentHomeBinding

class HomeFragment : TwitterFragment() {

    var _binding : FragmentHomeBinding? = null
    val binding get() =  _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}