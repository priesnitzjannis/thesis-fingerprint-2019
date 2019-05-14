package de.dali.thesisfingerprint2019.ui.base.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.utils.setString

class SpinnerWithTitle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val title: AppCompatTextView
    private val spinner: AppCompatSpinner

    private val array: Array<out CharSequence>

    init {
        inflate(context, R.layout.row_spinner_with_title, this)

        title = findViewById(R.id.tvTitle)
        spinner = findViewById(R.id.compatSpinner)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SpinnerWithTitle)
        title.text = attributes.getString(R.styleable.SpinnerWithTitle_title)

        array = attributes.getTextArray(R.styleable.SpinnerWithTitle_values)
        val spinnerArrayAdapter = ArrayAdapter(context, R.layout.row_spinner_item, array)
        spinner.adapter = spinnerArrayAdapter

        attributes.recycle()
    }

    fun setCallback(callback: (String) -> Unit) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val selected = array[i].toString()
                callback(selected)
            }
        }
    }

    fun setSelectedItem(item: String) {
        spinner.setString(item)
    }

    fun lock(lockUI: Boolean) {
        spinner.isEnabled = !lockUI
    }

}