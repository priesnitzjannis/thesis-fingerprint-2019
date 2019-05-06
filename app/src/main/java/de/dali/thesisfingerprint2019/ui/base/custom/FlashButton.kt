package de.dali.thesisfingerprint2019.ui.base.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import de.dali.thesisfingerprint2019.R

class FlashButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var messageUnread: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                refreshDrawableState()
            }
        }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (event.action == MotionEvent.ACTION_UP) {
            performClick()
        } else true
    }

    override fun performClick(): Boolean {
        messageUnread = !messageUnread
        return super.performClick()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        return if (messageUnread) {
            val drawableState = super.onCreateDrawableState(extraSpace + 1)

            View.mergeDrawableStates(drawableState, STATE_MESSAGE_UNREAD)
            drawableState
        } else {
            super.onCreateDrawableState(extraSpace)
        }
    }

    companion object {
        private val STATE_MESSAGE_UNREAD = intArrayOf(R.attr.state_flash_enabled)
    }

}

