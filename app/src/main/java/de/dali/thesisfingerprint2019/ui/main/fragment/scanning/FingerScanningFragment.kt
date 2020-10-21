package de.dali.thesisfingerprint2019.ui.main.fragment.scanning

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import dagger.android.support.AndroidSupportInjection
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.databinding.FragmentFingerScanningBinding
import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.processing.QualityAssuranceThread.IntermediateResults.FAILURE
import de.dali.thesisfingerprint2019.processing.QualityAssuranceThread.IntermediateResults.SUCCESSFUL
import de.dali.thesisfingerprint2019.processing.Utils.HAND.LEFT
import de.dali.thesisfingerprint2019.processing.Utils.HAND.RIGHT
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
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.nio.file.Path
import javax.inject.Inject

// Akquise Workflow
class FingerScanningFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: FragmentFingerScanningBinding

    lateinit var fingerScanningViewModel: FingerScanningViewModel

    lateinit var progressDialog: ProgressDialog

    lateinit var mRgba: Mat

    private var listener = object : CameraBridgeViewBase.CvCameraViewListener2 {

        override fun onCameraViewStarted(width: Int, height: Int) {
            mRgba = Mat(height, width, CvType.CV_8UC4)
            Logging.createLogEntry(Logging.loggingLevel_critical, 1300, "Scanning started.")
        }

        override fun onCameraViewStopped() {
            mRgba.release()
            Logging.createLogEntry(Logging.loggingLevel_critical, 1300, "Scanning stopped.")
            // TODO:
            // distinguish between cancellation (back) & home button (possible to return)
            //Logging.cancelAcquisition()
        }

        override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
            //val start = System.currentTimeMillis()

            fingerScanningViewModel.sucessfullFingersCounter++
            mRgba = inputFrame.rgba()

            if (fingerScanningViewModel.record /* && fingerScanningViewModel.frameCounter % 10 == 0 */)  {
                fingerScanningViewModel.sendToPipeline(mRgba)

                // only leads to spam
                //Logging.createLogEntry(Logging.loggingLevel_debug, 1300, "Sent an image to the pipeline.", mRgba)
            }


            // only spams logging messages in the 2-6ms range, occasionally 10/11/14ms
            //val duration = System.currentTimeMillis() - start
            //Logging.createLogEntry(Logging.loggingLevel_detailed, 1300, "Image acquired in " + duration + "ms.")

            return mRgba
        }

    }

    private val loaderCallback = object : BaseLoaderCallback(context) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    fingerScanningViewModel.startProcessingPipeline()
                    binding.javaCamera2View.enableView()

                    Handler().postDelayed({
                        javaCamera2View.toggleFlash()
                    }, 1000)
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

        arguments?.let {
            val entity = FingerScanningFragmentArgs.fromBundle(it).fingerPrintEntity
            val list = FingerScanningFragmentArgs.fromBundle(it).listIsoID.toList()
            fingerScanningViewModel.entity = entity
            fingerScanningViewModel.list = list

            fingerScanningViewModel.amountOfFinger = list.size
            fingerScanningViewModel.hand = if (list.any { it <= 5 }) RIGHT else LEFT

            if (fingerScanningViewModel.hand == RIGHT){
                javaCamera2ViewOverlay.setImageResource(R.drawable.overlay)
                javaCamera2ViewOverlay.scaleX = (-1).toFloat()
            } else {
                javaCamera2ViewOverlay.setImageResource(R.drawable.overlay)
            }
        }


        binding.javaCamera2View.visibility = VISIBLE
        binding.javaCamera2View.setCvCameraViewListener(listener)

        binding.txtSuccessfulFrames.text = fingerScanningViewModel.processedFingers.toString() + "/" + fingerScanningViewModel.amountOfFinger.toString()

        binding.button.setOnClickListener {
            fingerScanningViewModel.record = true
            binding.button.isEnabled = false
            Logging.createLogEntry(Logging.loggingLevel_critical, 1100, "Processing has been started.")
        }

        binding.buttonfoo.setOnClickListener{
            javaCamera2View.toggleFlash()

            //binding.buttonfoo.isEnabled = false
            Log.e(TAG, "\n\n\nBUTTONFOO PRESSED \n\n\n")
            Logging.createLogEntry(Logging.loggingLevel_critical,100,"The flash has been toggled")
        }

        fingerScanningViewModel.setSensorOrientation(Utils.getSensorOrientation(activity))

        fingerScanningViewModel.setOnSuccess {
            activity.runOnUiThread {
                binding.javaCamera2View.disableView()
                showProgressDialogWithTitle()

                fingerScanningViewModel.processImages(it, {
                    progressDialog.dismiss()
                    NavHostFragment.findNavController(this).navigateUp()
                }, {
                    Log.e(TAG, it.message)
                })
            }
        }

        fingerScanningViewModel.setOnUpdate { result, message, frameNumber ->
            when (result) {
                SUCCESSFUL -> {
                    binding.txtLastFrame.text = message
                    binding.txtSuccessfulFrames.text =
                        fingerScanningViewModel.processedFingers.toString() + "/" + fingerScanningViewModel.amountOfFinger.toString()
                }

                FAILURE -> {
                    binding.txtLastFrame.text = message
                }
            }
        }

        initOpenCV()
    }

    override fun onPause() {
        super.onPause()
        binding.javaCamera2View.disableView()
        fingerScanningViewModel.clearQueue()
        fingerScanningViewModel.stopProcessingPipeline()
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

    private fun showProgressDialogWithTitle() {
        progressDialog = ProgressDialog(context)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setMessage("Processing the image ...")
        progressDialog.show()
    }

    fun storeRecordSetIDs(list: MutableMap<Int, Int>, path: String){
        var filename = "recordSetIDs.txt"
        var text: String = ""

        list.forEach { (index, element) ->
            text = text + index + ":" + element + "_"
        }
        //TODO absoluten Pfad nicht hardcoden
        var file = File("sdcard/$path/$filename")
        file.writeText(text)

    }

    fun readFileDirectlyAsText(fileName: String): String
            = File(fileName).readText(Charsets.UTF_8)

    fun getRecordSetIDs(path: String): MutableMap<Int, Int> {
        var filename = "recordSetIDs.txt"
        var file = File("sdcard/$path/$filename")
        var outText: String
        var out: MutableMap<Int, Int> = mutableMapOf()

        // Einlesen und zu Map verarbeiten korrigieren

        if (file.exists()){
           outText = readFileDirectlyAsText("sdcard/$path/$filename")

            while(outText.isNotEmpty()){
                var key: Int = outText.substring(0, 1).toInt()
                var value: Int = outText.substring(2,3).toInt()
                out[key] = value
                outText = outText.substring(4)
            }
            return out
        } else {
            return out
        }
    }

    companion object {
        val TAG: String = FingerScanningFragment::class.java.simpleName
    }

}