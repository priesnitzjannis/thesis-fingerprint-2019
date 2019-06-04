package de.dali.thesisfingerprint2019.ui.main.fragment.fingerprint

import android.app.ProgressDialog
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import dagger.android.support.AndroidSupportInjection
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.databinding.FragmentFingerPrintCreateBinding
import de.dali.thesisfingerprint2019.ui.base.BaseFragment
import de.dali.thesisfingerprint2019.ui.main.viewmodel.fingerprint.FingerPrintCreateViewModel
import de.dali.thesisfingerprint2019.utils.Constants.NAME_MAIN_FOLDER
import de.dali.thesisfingerprint2019.utils.Utils
import de.dali.thesisfingerprint2019.utils.update
import kotlinx.android.synthetic.main.childview_result_imageview.view.*
import kotlinx.android.synthetic.main.row_multiselect.view.*
import java.io.File
import javax.inject.Inject

class FingerPrintCreateFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sensorManager: SensorManager

    @Inject
    lateinit var lightSensor: Sensor

    lateinit var binding: FragmentFingerPrintCreateBinding

    lateinit var fingerPrintCreateViewModel: FingerPrintCreateViewModel

    lateinit var lightEventListener: SensorEventListener

    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        initialiseViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finger_print_create, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseListener()
        initOnCLick()
        initOnChange()

        arguments?.let {
            val entity = FingerPrintCreateFragmentArgs.fromBundle(it).fingerPrintEntity
            val personEntity = FingerPrintCreateFragmentArgs.fromBundle(it).testPersonEntity

            if (entity != null) {
                fingerPrintCreateViewModel.fingerPrintEntity = entity

                if (entity.correctionDegree == null &&
                    entity.imageList == null &&
                    entity.resolution == null
                ) {
                    hideParts()
                }

                updateUI(fingerPrintCreateViewModel.fingerPrintEntity, true)
                binding.btnScan.isEnabled = entity.imageList == null
            } else {
                setVendor()
                hideParts()
                fingerPrintCreateViewModel.personID = personEntity.personID ?: -1
                sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_UI)

                fingerPrintCreateViewModel.selectedFinger.observe(this, Observer {
                    binding.btnScan.isEnabled = it.isNotEmpty()
                })
            }
        }
    }

    private fun initialiseListener() {
        lightEventListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val value = sensorEvent.values[0]
                binding.editIllumination.setText(SpannableStringBuilder(value.toString()))
                sensorManager.unregisterListener(lightEventListener)
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {

            }
        }
    }

    private fun setVendor() {
        fingerPrintCreateViewModel.vendor = Utils.getDeviceName()
        binding.editVendor.setText(SpannableStringBuilder(fingerPrintCreateViewModel.vendor))
    }

    private fun initOnChange() {
        binding.editIllumination.setCallback {
            fingerPrintCreateViewModel.illumination = it
        }

        binding.spinnerLocation.setCallback {
            fingerPrintCreateViewModel.location = it
        }

        binding.multiSelect.fingerSelectionThumb.setOnChangeListener { i, j ->
            fingerPrintCreateViewModel.selectedFinger.update(i, j)
        }
        binding.multiSelect.fingerSelectionIndexFinger.setOnChangeListener { i, j ->
            fingerPrintCreateViewModel.selectedFinger.update(i, j)
        }
        binding.multiSelect.fingerSelectionMiddleFinger.setOnChangeListener { i, j ->
            fingerPrintCreateViewModel.selectedFinger.update(i, j)
        }
        binding.multiSelect.fingerSelectionRingFinger.setOnChangeListener { i, j ->
            fingerPrintCreateViewModel.selectedFinger.update(i, j)
        }
        binding.multiSelect.fingerSelectionLittleFinger.setOnChangeListener { i, j ->
            fingerPrintCreateViewModel.selectedFinger.update(i, j)
        }
    }

    private fun initOnCLick() {
        binding.btnScan.setOnClickListener {
            handleOnClick()
        }
    }

    private fun initialiseViewModel() {
        fingerPrintCreateViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FingerPrintCreateViewModel::class.java)
    }

    private fun updateUI(entity: FingerPrintEntity, lockUI: Boolean) {
        binding.spinnerLocation.setSelectedItem(entity.location)
        binding.spinnerLocation.lock(lockUI)

        binding.editIllumination.setText(SpannableStringBuilder(entity.illumination.toString()))
        binding.editIllumination.lock(lockUI)

        binding.editVendor.setText(SpannableStringBuilder(entity.vendor))
        binding.editVendor.lock(lockUI)

        binding.multiSelect.fingerSelectionThumb.update(entity.listOfFingerIds)
        binding.multiSelect.fingerSelectionThumb.lock(lockUI)

        binding.multiSelect.fingerSelectionIndexFinger.update(entity.listOfFingerIds)
        binding.multiSelect.fingerSelectionIndexFinger.lock(lockUI)

        binding.multiSelect.fingerSelectionMiddleFinger.update(entity.listOfFingerIds)
        binding.multiSelect.fingerSelectionMiddleFinger.lock(lockUI)

        binding.multiSelect.fingerSelectionRingFinger.update(entity.listOfFingerIds)
        binding.multiSelect.fingerSelectionRingFinger.lock(lockUI)

        binding.multiSelect.fingerSelectionLittleFinger.update(entity.listOfFingerIds)
        binding.multiSelect.fingerSelectionLittleFinger.lock(lockUI)

        entity.resolution?.let {
            binding.editResolution.setText(SpannableStringBuilder(entity.resolution))
            binding.editResolution.lock(lockUI)
        }

        entity.correctionDegree?.let {
            binding.editCorrectionDegree.setText(SpannableStringBuilder(it.toString()))
            binding.editCorrectionDegree.lock(lockUI)
        }

        entity.imageList?.let {
            val imgFile = File("${Environment.getExternalStorageDirectory()}/$NAME_MAIN_FOLDER/${it[0]}")
            binding.resultImage.ivResult.setImageURI(Uri.fromFile(imgFile))
        }
    }

    private fun hideParts() {
        binding.editCorrectionDegree.visibility = GONE
        binding.editResolution.visibility = GONE
        binding.resultImage.visibility = GONE
    }

    private fun navToFingerScanningFrag(entity: FingerPrintEntity) {
        val action = FingerPrintCreateFragmentDirections
            .toFingerScanningFragment(entity)
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun handleOnClick() {
        if (fingerPrintCreateViewModel.isEntityInitialized()) {
            navToFingerScanningFrag(fingerPrintCreateViewModel.fingerPrintEntity)
        } else {
            showProgressDialogWithTitle()
            fingerPrintCreateViewModel.createFingerPrintEntity()
            fingerPrintCreateViewModel.insertFingerPrint(
                fingerPrintCreateViewModel.fingerPrintEntity,
                ::onSuccess,
                ::onError
            )
        }
    }

    private fun onSuccess(id: Long) {
        fingerPrintCreateViewModel.fingerPrintEntity.id = id

        progressDialog.dismiss()
        navToFingerScanningFrag(fingerPrintCreateViewModel.fingerPrintEntity)
    }

    private fun onError(t: Throwable) {
        Log.e(TAG, t.message)
    }

    private fun showProgressDialogWithTitle() {
        progressDialog = ProgressDialog(context)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setMessage("Preparing to download ...")
        progressDialog.show()
    }

    companion object {
        val TAG: String = FingerPrintCreateFragment::class.java.simpleName
    }

}

