package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.databinding.FragmentHomeViewPagerBinding

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-19 - 18:22
 */
class HomeViewPagerFragment : AbsFragment() {

    private lateinit var binding: FragmentHomeViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)
        binding.homeViewPager.adapter = HomePagerAdapter(this)
        return binding.root
    }
}