package de.dali.demonstrator.utils

import android.Manifest
import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import de.dali.demonstrator.R
import de.dali.demonstrator.ui.main.fragment.testperson.TestPersonOverviewFragment

object PermissionHandling {

    fun requestMultiplePermissions(activity: Activity, fragment: Fragment) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Dialogs.showDialog(
                            activity,
                            R.string.dialog_title_requierments,
                            R.string.dialog_message_requierments,
                            R.string.dialog_button_requierments
                        ) { NavUtil.navToSettings(fragment) }
                    } else if (!report.areAllPermissionsGranted()) {
                        Dialogs.showDialog(
                            activity,
                            R.string.dialog_title_permission,
                            R.string.dialog_message_permission,
                            R.string.dialog_button_permission
                        ) { requestMultiplePermissions(activity, fragment) }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Log.e(TestPersonOverviewFragment.TAG, it.name)
            }
            .onSameThread()
            .check()
    }

}