package com.example.statussaverapplication.adapters.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.savewhatsapp.ui.businesswhatsapp.BusinessWhatsappImagesFragment
import com.example.savewhatsapp.ui.businesswhatsapp.BusinessWhatsappVideosFragment


class BusinessWhatsappViewPagerAdapter(fragmentActivity: FragmentActivity, private var totalCount: Int) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BusinessWhatsappImagesFragment()
            1 -> BusinessWhatsappVideosFragment()
            else -> BusinessWhatsappImagesFragment()
        }
    }
}