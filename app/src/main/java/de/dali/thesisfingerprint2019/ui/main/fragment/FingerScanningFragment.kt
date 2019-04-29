package de.dali.thesisfingerprint2019.ui.main.fragment

import android.Manifest.permission.*
import android.content.Intent
import android.net.Uri.fromParts
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.android.support.AndroidSupportInjection
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.databinding.FragmentFingerScanningBinding
import de.dali.thesisfingerprint2019.ui.base.BaseFragment
import de.dali.thesisfingerprint2019.ui.main.viewmodel.FingerScanningViewModel
import kotlinx.android.synthetic.main.fragment_finger_scanning.view.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat
import javax.inject.Inject

class FingerScanningFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: FragmentFingerScanningBinding

    lateinit var moviesListViewModel: FingerScanningViewModel

    private var listener = object : CameraBridgeViewBase.CvCameraViewListener2 {

        override fun onCameraViewStarted(width: Int, height: Int) {
            mRgba = Mat(height, width, CvType.CV_8UC4)
        }

        override fun onCameraViewStopped() {
            mRgba.release()
        }

        override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
            mRgba = inputFrame.rgba()
            return mRgba
        }

    }

    private val loaderCallback = object : BaseLoaderCallback(context) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    binding.root.javaCamera2View.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        initialiseViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finger_scanning, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.javaCamera2View.visibility = VISIBLE
        binding.root.javaCamera2View.setCvCameraViewListener(listener)
        requestMultiplePermissions()
    }

    override fun onPause() {
        super.onPause()
        binding.root.javaCamera2View.disableView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            requestMultiplePermissions()
        }
    }

    private fun initialiseView() {

    }

    private fun initialiseViewModel() {
        moviesListViewModel = ViewModelProviders.of(this, viewModelFactory).get(FingerScanningViewModel::class.java)
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(activity)
            .withPermissions(
                CAMERA,
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.isAnyPermissionPermanentlyDenied) {
                        openSettingsDialog()
                    } else if (!report.areAllPermissionsGranted()) {
                        showAlertDialog()
                    } else {
                        initOpenCV()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(activity, "Some Error! ", Toast.LENGTH_SHORT).show()
            }
            .onSameThread()
            .check()
    }

    private fun initOpenCV(){
        if (OpenCVLoader.initDebug()) {
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }else {
            showOpenCVDialog()
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(activity)
        with(builder) {
            setTitle("Camera & storage Permission")
            setMessage("All permissions have to be granted, to start the app.")
            setCancelable(false)
            setPositiveButton("Retry") { dialog, _ ->
                dialog.cancel()
                requestMultiplePermissions()
            }
            setNegativeButton("Quit") { dialog, _ ->
                dialog.cancel()
                activity.finish()
            }
            builder.create()
        }.show()
    }

    private fun showOpenCVDialog() {
        val builder = AlertDialog.Builder(activity)
        with(builder) {
            setTitle("OpenCV")
            setMessage("Initialization of OpenCV failed.")
            setCancelable(false)
            setNegativeButton("Quit") { dialog, _ ->
                dialog.cancel()
                activity.finish()
            }
            builder.create()
        }.show()
    }

    private fun openSettingsDialog() {
        val builder = AlertDialog.Builder(activity)
        with(builder) {
            setTitle("Required Permissions")
            setMessage("This app require permission to use awesome feature. Grant them in app settings.")
            setPositiveButton("Settings") { dialog, _ ->
                dialog.cancel()
                openSettings()
            }
            setNegativeButton("Quit") { dialog, _ ->
                dialog.cancel()
            }
            create()
        }.show()
    }

    private fun openSettings() {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = fromParts("package", context?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, REQUEST_CODE)
    }

    companion object {
        val TAG = FingerScanningFragment::class.java.simpleName
        val REQUEST_CODE = 42

        private lateinit var mRgba: Mat
    }

}