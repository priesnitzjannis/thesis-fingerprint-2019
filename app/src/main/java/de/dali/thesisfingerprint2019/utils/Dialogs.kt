package de.dali.thesisfingerprint2019.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import de.dali.thesisfingerprint2019.R

object Dialogs {

    fun showDialog(
        activity: Activity,
        titleID: Int,
        messageID: Int,
        posBtnID: Int? = null,
        posBtnAction: (() -> Unit)? = null
    ) {

        val builder = AlertDialog.Builder(activity)
        with(builder) {
            setTitle(activity.getString(titleID))
            setMessage(activity.getString(messageID))
            setCancelable(false)

            if (posBtnID != null && posBtnAction != null)
                setPositiveButton(activity.getString(posBtnID)) { dialog, _ ->
                    dialog.cancel()
                    posBtnAction()
                }

            setNegativeButton(activity.getString(R.string.dialog_button_quit)) { dialog, _ ->
                dialog.cancel()
                activity.finish()
            }
            builder.create()
        }.show()

    }

}