package de.dali.thesisfingerprint2019.ui.base.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.dali.thesisfingerprint2019.R


class FingerSelectionRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val titleLeft: AppCompatTextView
    private val titleRight: AppCompatTextView

    private val titleRow: AppCompatTextView

    private val cbLeft: AppCompatCheckBox
    private val cbRight: AppCompatCheckBox

    private val rowTitle: String

    init {
        inflate(context, R.layout.row_finger_selection, this)

        titleLeft = findViewById(R.id.txtTitleLeft)
        titleRight = findViewById(R.id.txtTitleRight)

        titleRow = findViewById(R.id.txtTitleFinger)

        cbLeft = findViewById(R.id.cbLeft)
        cbRight = findViewById(R.id.cbRight)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.FingerSelectionRow)
        val showHeader = attributes.getBoolean(R.styleable.FingerSelectionRow_showHeader, false)

        rowTitle = attributes.getString(R.styleable.FingerSelectionRow_row_title) ?: ""

        initClickHandling()

        showHeader(showHeader)
        setRowTitle(rowTitle)

        attributes.recycle()
    }

    fun getSelection(): Int {
        return when (rowTitle) {
            context.getString(R.string.row_multiselect_thumb) -> {
                if (cbLeft.isChecked) 0 else 1
            }
            context.getString(R.string.row_multiselect_thumb) -> {
                if (cbLeft.isChecked) 0 else 1
            }
            context.getString(R.string.row_multiselect_thumb) -> {
                if (cbLeft.isChecked) 0 else 1
            }
            context.getString(R.string.row_multiselect_thumb) -> {
                if (cbLeft.isChecked) 0 else 1
            }
            context.getString(R.string.row_multiselect_thumb) -> {
                if (cbLeft.isChecked) 0 else 1
            }
            else -> -1
        }
    }

    private fun showHeader(value: Boolean) {
        titleLeft.visibility = if (value) VISIBLE else GONE
        titleRight.visibility = if (value) VISIBLE else GONE
    }

    private fun setRowTitle(value: String) {
        titleRow.text = value
    }

    private fun initClickHandling() {
        with(cbLeft) {
            setOnClickListener {
                if (cbRight.isChecked) {
                    cbRight.isChecked = false
                }
            }
        }

        with(cbRight) {
            setOnClickListener {
                if (cbLeft.isChecked) {
                    cbLeft.isChecked = false
                }
            }
        }
    }

}