package com.example.pecodetesttask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.notification_fragment.*

class NotificationFragment(
    private val position: Int,
    private val notificationFragmentInterface: NotificationFragmentInterface
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notification_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        pageNumberTextView.text = position.toString()
        createFragmentButton.setOnClickListener { notificationFragmentInterface.addFragment() }
        if (position > 1) {
            deleteFragmentButton.apply {
                visibility = View.VISIBLE
                setOnClickListener { notificationFragmentInterface.deleteFragment() }
            }
        }
    }
}