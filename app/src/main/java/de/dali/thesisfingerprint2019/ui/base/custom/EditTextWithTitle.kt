package de.dali.thesisfingerprint2019.ui.base.custom

import android.content.Context
import android.text.InputType.*
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import de.dali.thesisfingerprint2019.R
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView


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

        val enabled = attributes.getBoolean(R.styleable.EditTextWithTitle_enabled,true)
        val inputType = attributes.getString(R.styleable.EditTextWithTitle_inputType)

        editText.isEnabled = enabled

        if (inputType == "numerical"){
            editText.inputType = TYPE_CLASS_NUMBER
        }

        attributes.recycle()

    }

    fun getEditTextValue() : String = editText.text.toString()

}