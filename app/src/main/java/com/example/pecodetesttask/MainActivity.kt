package com.example.pecodetesttask

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), NotificationFragmentInterface {
    //todo manage packages

    private lateinit var viewPagerAdapter: NotificationFragmentStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewPager()
        addSavedFragments()
    }

    private fun addSavedFragments() {
        val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        coroutineScope.launch {
            //todo test correct using flow
            val fragmentCount =
                PecodeTestTaskPreferences(this@MainActivity).getFragmentCount().first()
            //todo is runOnUiThread okay???
            runOnUiThread {
                repeat(fragmentCount) {
                    addFragment()
                }
            }
        }
    }

    private fun setupViewPager() {
        viewPagerAdapter = NotificationFragmentStateAdapter(this)
        viewPager.apply {
            adapter = viewPagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewPagerAdapter.currentPosition = position
                }
            })
        }
    }

    override fun addFragment() {
        viewPagerAdapter.apply {
            addFragment(this@MainActivity)
            viewPager.currentItem = fragmentList.lastIndex
        }
        saveFragmentCountToPref()
    }

    override fun deleteFragment() {
        viewPagerAdapter.apply {
            if (currentPosition == fragmentList.lastIndex) {
                viewPager.currentItem = fragmentList.lastIndex - 1
            }
            deleteFragment()
            //todo delete fragment notifications too
        }
        saveFragmentCountToPref()
    }

    private fun saveFragmentCountToPref() {
        //todo may be possible observe when data is changed and save it
        val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        coroutineScope.launch {
            PecodeTestTaskPreferences(this@MainActivity).saveFragmentCount(viewPagerAdapter.fragmentList.size)
        }
    }
}