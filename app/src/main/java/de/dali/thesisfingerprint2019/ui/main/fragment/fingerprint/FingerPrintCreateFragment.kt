package de.dali.thesisfingerprint2019.ui.main.fragment.fingerprint

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import dagger.android.support.AndroidSupportInjection
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.databinding.FragmentFingerPrintCreateBinding
import de.dali.thesisfingerprint2019.ui.base.BaseFragment
import de.dali.thesisfingerprint2019.ui.main.viewmodel.fingerprint.FingerPrintCreateViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        initialiseViewModel()
        initialiseListener()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finger_print_create, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnScan.setOnClickListener {
            val action = FingerPrintCreateFragmentDirections.actionFingerPrintCreateFragmentToFingerScanningFragment(
                fingerPrintCreateViewModel.fingerPrintEntity
            )
            NavHostFragment.findNavController(this).navigate(action)
        }

        arguments?.let {
            val entity = FingerPrintCreateFragmentArgs.fromBundle(it).fingerPrintEntity
            if (entity != null) {
                fingerPrintCreateViewModel.fingerPrintEntity = entity
            } else {
                fingerPrintCreateViewModel.fingerPrintEntity = FingerPrintEntity()
                sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    private fun initialiseListener() {
        lightEventListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val value = sensorEvent.values[0]
                fingerPrintCreateViewModel.fingerPrintEntity.illumination = value
                sensorManager.unregisterListener(lightEventListener)
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {

            }
        }
    }

    private fun initialiseViewModel() {
        fingerPrintCreateViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FingerPrintCreateViewModel::class.java)
    }

    private fun updateUI(entity: FingerPrintEntity) {
        binding.spinnerLocation.setSelectedItem(entity.location ?: "")
        binding.editIllumination.setText(SpannableStringBuilder(entity.illumination.toString()))
        binding.editVendor.setText(SpannableStringBuilder(entity.vendor ?: ""))
    }

    companion object {
        val TAG: String = FingerPrintCreateFragment::class.java.simpleName
    }

}

