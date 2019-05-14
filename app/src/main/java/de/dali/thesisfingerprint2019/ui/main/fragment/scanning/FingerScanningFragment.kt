package de.dali.thesisfingerprint2019.ui.main.fragment.scanning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.AndroidSupportInjection
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.databinding.FragmentFingerScanningBinding
import de.dali.thesisfingerprint2019.ui.base.BaseFragment
import de.dali.thesisfingerprint2019.ui.main.viewmodel.scanning.FingerScanningViewModel
import de.dali.thesisfingerprint2019.utils.Dialogs
import de.dali.thesisfingerprint2019.utils.Utils
import kotlinx.android.synthetic.main.fragment_finger_scanning.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat
import javax.inject.Inject

class FingerScanningFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: FragmentFingerScanningBinding

    lateinit var fingerScanningViewModel: FingerScanningViewModel

    lateinit var mRgba: Mat

    var frameCounter = 0

    private var listener = object : CameraBridgeViewBase.CvCameraViewListener2 {

        override fun onCameraViewStarted(width: Int, height: Int) {
            mRgba = Mat(height, width, CvType.CV_8UC4)
        }

        override fun onCameraViewStopped() {
            mRgba.release()
        }

        override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
            frameCounter++
            mRgba = inputFrame.rgba()

            if (frameCounter % 10 == 0) {
                fingerScanningViewModel.processImage(mRgba)
            }

            return mRgba
        }

    }

    private val loaderCallback = object : BaseLoaderCallback(context) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    fingerScanningViewModel.startProcessingPipeline()
                    binding.javaCamera2View.enableView()
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
        binding.javaCamera2View.visibility = VISIBLE
        binding.javaCamera2View.setCvCameraViewListener(listener)
        binding.buttonFlash.setOnClickListener { javaCamera2View.toggleFlash() }

        fingerScanningViewModel.setViews(
            binding.resultView,
            binding.resultView2
        )

        fingerScanningViewModel.setSensorOrientation(Utils.getSensorOrientation(activity))

        initOpenCV()
    }

    override fun onPause() {
        super.onPause()
        binding.javaCamera2View.disableView()
        fingerScanningViewModel.stopProcessingPipeline()
    }

    override fun onDestroy() {
        super.onDestroy()
        fingerScanningViewModel.clearQueue()
    }

    private fun initialiseViewModel() {
        fingerScanningViewModel = ViewModelProviders.of(this, viewModelFactory).get(FingerScanningViewModel::class.java)
    }


    private fun initOpenCV() {
        if (OpenCVLoader.initDebug()) {
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        } else {
            Dialogs.showDialog(
                activity,
                R.string.dialog_title_opencv,
                R.string.dialog_message_opencv
            )
        }
    }

    companion object {
        val TAG: String = FingerScanningFragment::class.java.simpleName
    }

}