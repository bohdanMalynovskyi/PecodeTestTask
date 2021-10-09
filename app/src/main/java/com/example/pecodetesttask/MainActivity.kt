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

    private lateinit var viewPagerAdapter: NotificationFragmentStateAdapter
    private var notificationsMap = HashMap<Int, Int>()

    companion object {
        const val KEY_FRAGMENT_INDEX = "KEY_FRAGMENT_INDEX"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        NotificationUtils.createNotificationChannel(this)
        setupViewPager()
        addSavedFragments()
    }

    private fun addSavedFragments() {
        val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        coroutineScope.launch {
            val fragmentCount =
                PecodeTestTaskPreferences(this@MainActivity).getFragmentCount().first()
            runOnUiThread {
                repeat(fragmentCount) {
                    viewPagerAdapter.addFragment(this@MainActivity)
                }
                setInitialFragment()
            }
        }
    }

    private fun setInitialFragment() {
        val defaultFragmentIndex = 0
        val initialFragmentIndex = intent.getIntExtra(KEY_FRAGMENT_INDEX, defaultFragmentIndex)
        viewPager.currentItem = initialFragmentIndex
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
            val deletedFragmentIndex = fragmentList.lastIndex
            if (currentPosition == deletedFragmentIndex) {
                viewPager.currentItem = deletedFragmentIndex - 1
            }
            deleteFragment()
            deleteFragmentNotifications(deletedFragmentIndex)
        }
        saveFragmentCountToPref()
    }

    private fun deleteFragmentNotifications(fragmentIndex: Int) {
        notificationsMap.forEach { mapEntry ->
            if (mapEntry.value == fragmentIndex) {
                NotificationUtils.deleteNotification(this, mapEntry.key)
            }
        }
    }

    override fun createNotification(fragmentIndex: Int) {
        val notificationId = notificationsMap.size
        NotificationUtils.showNotification(this, fragmentIndex, notificationId)
        notificationsMap[notificationId] = fragmentIndex
    }

    private fun saveFragmentCountToPref() {
        val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        coroutineScope.launch {
            PecodeTestTaskPreferences(this@MainActivity).saveFragmentCount(viewPagerAdapter.fragmentList.size)
        }
    }
}