package de.dali.thesisfingerprint2019.ui.base

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    protected lateinit var activity: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context as Activity
    }
}
