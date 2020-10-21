package de.dali.thesisfingerprint2019.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.databinding.FragmentFingerPrintCreateBinding
import kotlinx.android.synthetic.main.row_multiselect.view.*



fun AppCompatSpinner.setString(value: String) {
    var pos = 0

    for (i in 0 until this.count) {
        if (this.getItemAtPosition(i).toString().equals(value, ignoreCase = true)) {
            pos = i
            break
        }
    }
    this.setSelection(pos)
}

fun MutableLiveData<MutableList<Int>>.update(oldVal: Int?, newVal: Int?) {
    if (oldVal != null) this.value?.remove(oldVal)
    if (newVal != null) this.value?.add(newVal)


    this.value = this.value

}