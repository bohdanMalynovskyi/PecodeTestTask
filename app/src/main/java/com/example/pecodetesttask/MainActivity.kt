package com.example.pecodetesttask

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first


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
            var fragmentCount =
                    PecodeTestTaskPreferences(this@MainActivity).getFragmentCount().first()
            withContext(Dispatchers.Main) {
                val initialFragmentIndex = intent.getIntExtra(KEY_FRAGMENT_INDEX, -1)
                if (initialFragmentIndex != -1) {
                    fragmentCount = initialFragmentIndex + 1
                }
                repeat(fragmentCount) {
                    viewPagerAdapter.addFragment(this@MainActivity)
                }
                viewPager.currentItem = if (initialFragmentIndex != -1) initialFragmentIndex else 0
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