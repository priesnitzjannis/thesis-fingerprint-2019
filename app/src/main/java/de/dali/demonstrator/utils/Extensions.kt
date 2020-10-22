package de.dali.demonstrator.utils

import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.MutableLiveData
import de.dali.demonstrator.databinding.FragmentFingerPrintCreateBinding


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