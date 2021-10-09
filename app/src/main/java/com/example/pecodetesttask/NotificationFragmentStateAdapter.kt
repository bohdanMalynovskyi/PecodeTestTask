package com.example.pecodetesttask

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class NotificationFragmentStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    var fragmentList = ArrayList<NotificationFragment>()
        private set
    var currentPosition = 0

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    fun addFragment(notificationFragmentInterface: NotificationFragmentInterface) {
        val newFragmentPosition = fragmentList.size + 1
        fragmentList.add(NotificationFragment(newFragmentPosition, notificationFragmentInterface))
        notifyDataSetChanged()
    }

    fun deleteFragment() {
        fragmentList.removeAt(fragmentList.lastIndex)
        notifyDataSetChanged()
    }
}