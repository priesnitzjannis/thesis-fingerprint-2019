package de.dali.thesisfingerprint2019.ui.base.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.dali.thesisfingerprint2019.R

class ListWithLoadingSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    enum class LIST(val state: Int) {
        LOADING(0),
        RESULT(1),
        EMPTY(2)
    }

    private val txtError: AppCompatTextView

    init {
        inflate(context, R.layout.view_list_with_loading_spinner, this)

        txtError = findViewById(R.id.txtNoEntities)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ListWithLoadingSpinner)
        val errorText = attributes.getString(R.styleable.ListWithLoadingSpinner_error_text)

        txtError.text = errorText

        attributes.recycle()
    }

}