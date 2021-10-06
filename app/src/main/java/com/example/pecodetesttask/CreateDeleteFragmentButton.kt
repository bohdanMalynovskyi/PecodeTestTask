package com.example.pecodetesttask

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.create_delete_fragment_button.view.*

class CreateDeleteFragmentButton(
    context: Context,
    attributeSet: AttributeSet
) : ConstraintLayout(
    context,
    attributeSet
) {

    init {
        View.inflate(context, R.layout.create_delete_fragment_button, this)
        getAttributes(context, attributeSet)
    }

    private lateinit var type: Type

    private fun setType(type: Type) {
        when (type) {
            Type.CREATE -> {
                createDeleteFragmentButtonPlus.visibility = View.VISIBLE
                createDeleteFragmentButtonMinus.visibility = View.GONE
            }
            Type.DELETE -> {
                createDeleteFragmentButtonPlus.visibility = View.GONE
                createDeleteFragmentButtonMinus.visibility = View.VISIBLE
            }
        }
    }

    private fun getAttributes(context: Context, attributeSet: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.CreateDeleteFragmentButton
        )
        try {
            type =
                when (attributes.getInteger(R.styleable.CreateDeleteFragmentButton_type, 0)) {
                    0 -> Type.CREATE
                    else -> Type.DELETE
                }
            setType(type)
        } finally {
            attributes.recycle()
        }
    }

    enum class Type {
        CREATE,
        DELETE
    }
}