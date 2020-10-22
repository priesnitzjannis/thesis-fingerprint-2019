package de.dali.demonstrator.ui.base.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.dali.demonstrator.R


class FingerSelectionRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val titleLeft: AppCompatTextView
    private val titleRight: AppCompatTextView

    private val titleRow: AppCompatTextView

    val cbLeft: AppCompatCheckBox
    val cbRight: AppCompatCheckBox

    private val rowTitle: String

    private lateinit var onChange: (Int?, Int?) -> Unit

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

    fun setOnChangeListener(onChange: (Int?, Int?) -> Unit) {
        this.onChange = onChange
    }

    fun update(list: List<String>) {
        when (rowTitle) {
            context.getString(R.string.row_multiselect_thumb) -> {
                if (list.contains("1")) cbRight.isChecked = true else if (list.contains("6")) cbLeft.isChecked = true
            }
            context.getString(R.string.row_multiselect_index_finger) -> {
                if (list.contains("2")) cbRight.isChecked = true else if (list.contains("7")) cbLeft.isChecked = true
            }
            context.getString(R.string.row_multiselect_middle_finger) -> {
                if (list.contains("3")) cbRight.isChecked = true else if (list.contains("8")) cbLeft.isChecked = true
            }
            context.getString(R.string.row_multiselect_ring_finger) -> {
                if (list.contains("4")) cbRight.isChecked = true else if (list.contains("9")) cbLeft.isChecked = true
            }
            context.getString(R.string.row_multiselect_little_finger) -> {
                if (list.contains("5")) cbRight.isChecked = true else if (list.contains("10")) cbLeft.isChecked = true
            }
            else -> -1
        }
    }

    private fun getSelection(id: Int): Int {
        return when (rowTitle) {
            context.getString(R.string.row_multiselect_thumb) -> {
                if (id == R.id.cbRight) 1 else if (id == R.id.cbLeft) 6 else -1
            }
            context.getString(R.string.row_multiselect_index_finger) -> {
                if (id == R.id.cbRight) 2 else if (id == R.id.cbLeft) 7 else -1
            }
            context.getString(R.string.row_multiselect_middle_finger) -> {
                if (id == R.id.cbRight) 3 else if (id == R.id.cbLeft) 8 else -1
            }
            context.getString(R.string.row_multiselect_ring_finger) -> {
                if (id == R.id.cbRight) 4 else if (id == R.id.cbLeft) 9 else -1
            }
            context.getString(R.string.row_multiselect_little_finger) -> {
                if (id == R.id.cbRight) 5 else if (id == R.id.cbLeft) 10 else -1
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
                val oldVal: Int?
                val newVal: Int?

                if (!isChecked) {
                    oldVal = getSelection(id)
                    newVal = null
                } else if (cbRight.isChecked) {
                    cbRight.isChecked = false

                    oldVal = getSelection(cbRight.id)
                    newVal = getSelection(id)
                } else {
                    oldVal = null
                    newVal = getSelection(id)
                }

                onChange(oldVal, newVal)
            }
        }

        with(cbRight) {
            setOnClickListener {
                val oldVal: Int?
                val newVal: Int?

                if (!isChecked) {
                    oldVal = getSelection(id)
                    newVal = null
                } else if (cbLeft.isChecked) {
                    cbLeft.isChecked = false

                    oldVal = getSelection(cbLeft.id)
                    newVal = getSelection(id)
                } else {
                    oldVal = null
                    newVal = getSelection(id)
                }

                onChange(oldVal, newVal)
            }
        }
    }

    fun lock(lockUI: Boolean) {
        cbRight.isEnabled = lockUI
        cbLeft.isEnabled = lockUI
    }

}