package com.example.statussaverapplication.adapters.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.savewhatsapp.ui.whatsapp.ImagesFragment
import com.example.savewhatsapp.ui.whatsapp.VideoFragment


class WhatsappViewPagerAdapter(fragmentActivity: FragmentActivity, private var totalCount: Int) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ImagesFragment()
            1 -> VideoFragment()
            else -> ImagesFragment()
        }
    }
}
