package de.dali.thesisfingerprint2019.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import de.dali.thesisfingerprint2019.utils.Constants.SETTINGS_REQUEST_CODE

object NavUtil {

    fun navToSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivityForResult(intent, SETTINGS_REQUEST_CODE)
    }

}