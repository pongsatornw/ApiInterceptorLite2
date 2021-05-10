package com.yggdrasil.apitrackerlite.ui.interceptor.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class InterceptorDetailsPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> InterceptorOverallFragment.newInstance()
        1 -> InterceptorRequestFragment.newInstance()
        2 -> InterceptorResponseFragment.newInstance()
        else -> throw Exception("Invalid Position")
    }
}