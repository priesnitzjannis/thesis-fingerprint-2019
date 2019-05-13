package de.dali.thesisfingerprint2019.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.Fragment
import de.dali.thesisfingerprint2019.utils.Constants.SETTINGS_REQUEST_CODE

object NavUtil {

    fun navToSettings(fragment: Fragment) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", fragment.activity?.packageName, null)
        intent.data = uri
        fragment.startActivityForResult(intent, SETTINGS_REQUEST_CODE)
    }

}