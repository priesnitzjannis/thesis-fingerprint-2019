package de.dali.thesisfingerprint2019.ui.main.fragment.fingerprint

import android.app.ProgressDialog
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import dagger.android.support.AndroidSupportInjection
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.data.local.entity.ImageEntity
import de.dali.thesisfingerprint2019.databinding.FragmentFingerPrintCreateBinding
import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.ui.base.BaseFragment
import de.dali.thesisfingerprint2019.ui.base.custom.FingerPrintAdapter
import de.dali.thesisfingerprint2019.ui.main.viewmodel.fingerprint.FingerPrintCreateViewModel
import de.dali.thesisfingerprint2019.utils.Utils
import de.dali.thesisfingerprint2019.utils.update
import kotlinx.android.synthetic.main.fragment_finger_print_create.*
import kotlinx.android.synthetic.main.row_multiselect.*
import kotlinx.android.synthetic.main.row_multiselect.view.*
import javax.inject.Inject

// Fingerabdruck aufnehmen - Einstellungen
class FingerPrintCreateFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sensorManager: SensorManager

    @Inject
    lateinit var lightSensor: Sensor

    lateinit var binding: FragmentFingerPrintCreateBinding

    lateinit var fingerPrintCreateViewModel: FingerPrintCreateViewModel

    lateinit var adapter: FingerPrintAdapter

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

            adapter = FingerPrintAdapter(context)

            resultImage.layoutManager = GridLayoutManager(context, 1)
            resultImage.adapter = adapter

            fingerPrintCreateViewModel.listOfImages.observe(this, Observer {
                binding.scrollViewCreate.visibility = VISIBLE
                binding.btnScan.visibility = VISIBLE
                binding.pbLoading.visibility = GONE

                fingerPrintCreateViewModel.selectedFinger.observe(this, Observer { fingers ->
                    binding.btnScan.isEnabled = fingers.isNotEmpty()

                    checkPossibility(fingers)

                })



                if (entity != null) {
                    fingerPrintCreateViewModel.fingerPrintEntity = entity
                    updateUI(entity, it, lockUI = true, lockCB = it.isEmpty())
                    adapter.listOfFingerPrints = it
                } else {
                    setVendor()
                    hideParts()
                    fingerPrintCreateViewModel.personID = personEntity.personID ?: -1
                    sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_UI)
                }
            })

            fingerPrintCreateViewModel.loadImages(entity?.fingerPrintId ?: -1)
        }
    }

    private fun checkPossibility(fingers: MutableList<Int>) {
        if (fingers.isNotEmpty()) {
            if (fingers.contains(6) || fingers.contains(1)) {
                binding.multiSelect.fingerSelectionIndexFinger.visibility = INVISIBLE
                binding.multiSelect.fingerSelectionMiddleFinger.visibility = INVISIBLE
                binding.multiSelect.fingerSelectionRingFinger.visibility = INVISIBLE
                binding.multiSelect.fingerSelectionLittleFinger.visibility = INVISIBLE
            } else {
                binding.multiSelect.fingerSelectionIndexFinger.visibility = VISIBLE
                binding.multiSelect.fingerSelectionMiddleFinger.visibility = VISIBLE
                binding.multiSelect.fingerSelectionRingFinger.visibility = VISIBLE
                binding.multiSelect.fingerSelectionLittleFinger.visibility = VISIBLE
            }

            if ( fingers.contains(2) || fingers.contains(3) || fingers.contains(4) || fingers.contains(5)
                || fingers.contains(7) || fingers.contains(8) || fingers.contains(9) || fingers.contains(10)) {
                binding.multiSelect.fingerSelectionThumb.visibility = INVISIBLE
            } else {
                binding.multiSelect.fingerSelectionThumb.visibility = VISIBLE
            }
        } else {
            binding.multiSelect.fingerSelectionThumb.visibility = VISIBLE
            binding.multiSelect.fingerSelectionIndexFinger.visibility = VISIBLE
            binding.multiSelect.fingerSelectionMiddleFinger.visibility = VISIBLE
            binding.multiSelect.fingerSelectionRingFinger.visibility = VISIBLE
            binding.multiSelect.fingerSelectionLittleFinger.visibility = VISIBLE
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
            fingerPrintCreateViewModel.illumination = it.toFloat()
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

    private fun updateUI(
        e: FingerPrintEntity,
        entity: List<ImageEntity>,
        lockUI: Boolean,
        lockCB: Boolean
    ) {

        val listOfFingerIds = entity.map { it.biometricalID.toString() }

        binding.spinnerLocation.setSelectedItem(e.location)
        binding.spinnerLocation.lock(lockUI)

        binding.editIllumination.setText(SpannableStringBuilder(e.illumination.toString()))
        binding.editIllumination.lock(lockUI)

        binding.editVendor.setText(SpannableStringBuilder(e.vendor))
        binding.editVendor.lock(lockUI)

        binding.multiSelect.fingerSelectionThumb.update(listOfFingerIds)
        binding.multiSelect.fingerSelectionThumb.lock(lockCB)

        binding.multiSelect.fingerSelectionIndexFinger.update(listOfFingerIds)
        binding.multiSelect.fingerSelectionIndexFinger.lock(lockCB)

        binding.multiSelect.fingerSelectionMiddleFinger.update(listOfFingerIds)
        binding.multiSelect.fingerSelectionMiddleFinger.lock(lockCB)

        binding.multiSelect.fingerSelectionRingFinger.update(listOfFingerIds)
        binding.multiSelect.fingerSelectionRingFinger.lock(lockCB)

        binding.multiSelect.fingerSelectionLittleFinger.update(listOfFingerIds)
        binding.multiSelect.fingerSelectionLittleFinger.lock(lockCB)

    }

    private fun hideParts() {
        binding.resultImage.visibility = GONE
    }

    private fun navToFingerScanningFrag(entity: FingerPrintEntity, list: List<Int>) {
        // TODO
        // Check that the finger numbering is correct
        var fingers = ""
        for (currentFinger in list) {
            fingers += "#" + currentFinger + "; "
        }
        fingers = fingers.dropLast(2)

        Logging.startAcquisition(fingerPrintCreateViewModel.location, fingerPrintCreateViewModel.illumination.toDouble(), fingers)

        Logging.createLogEntry(Logging.loggingLevel_critical, 100, "Finger selection includes finger" + fingers)


        val action = FingerPrintCreateFragmentDirections
            .toFingerScanningFragment(entity, list.toIntArray())
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun handleOnClick() {
        if (fingerPrintCreateViewModel.isEntityInitialized()) {
            navToFingerScanningFrag(
                fingerPrintCreateViewModel.fingerPrintEntity,
                fingerPrintCreateViewModel.getSortedStringList() ?: emptyList()
            )
        } else {
            fingerPrintCreateViewModel.createFingerPrintEntity()
            navToFingerScanningFrag(
                fingerPrintCreateViewModel.fingerPrintEntity,
                fingerPrintCreateViewModel.getSortedStringList() ?: emptyList()
            )
        }
    }

    companion object {
        val TAG: String = FingerPrintCreateFragment::class.java.simpleName
    }

}

