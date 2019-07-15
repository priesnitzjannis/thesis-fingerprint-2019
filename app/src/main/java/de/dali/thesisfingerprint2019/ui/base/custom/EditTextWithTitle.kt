package de.dali.thesisfingerprint2019.ui.base.custom

import android.content.Context
import android.text.Editable
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.dali.thesisfingerprint2019.R


class EditTextWithTitle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val title: AppCompatTextView
    private val editText: AppCompatEditText

    init {
        inflate(context, R.layout.row_edittext_with_title, this)

        title = findViewById(R.id.tvTitle)
        editText = findViewById(R.id.editText)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.EditTextWithTitle)
        title.text = attributes.getString(R.styleable.EditTextWithTitle_title)

        val enabled = attributes.getBoolean(R.styleable.EditTextWithTitle_enabled, true)
        val inputType = attributes.getString(R.styleable.EditTextWithTitle_inputType)

        editText.isEnabled = enabled

        if (inputType == "numerical") {
            editText.inputType = TYPE_CLASS_NUMBER
        }

        attributes.recycle()

    }

    fun setCallback(onChange: (String) -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val number = s.toString()
                onChange(number)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun setText(text: SpannableStringBuilder) {
        editText.text = text
    }

    fun lock(lockUI: Boolean) {
        editText.isEnabled = !lockUI
    }

}