package de.dali.demonstrator.ui.main.fragment.testperson


import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.facebook.stetho.inspector.protocol.module.Network
import dagger.android.support.AndroidSupportInjection
import de.dali.demonstrator.R
import de.dali.demonstrator.data.local.entity.TestPersonEntity
import de.dali.demonstrator.databinding.FragmentTestPersonCreateBinding
import de.dali.demonstrator.logging.Logging
import de.dali.demonstrator.ui.base.BaseFragment
import de.dali.demonstrator.ui.main.viewmodel.testperson.TestPersonCreateViewModel
import de.dali.demonstrator.utils.Constants.NAME_MAIN_FOLDER
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

// Testperson Einstellungen
class TestPersonCreateFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: FragmentTestPersonCreateBinding

    private lateinit var testPersonCreateViewModel: TestPersonCreateViewModel

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        initialiseViewModel()

        Logging.createLogEntry(Logging.loggingLevel_critical, 100, "Navigation to overview of a testperson.")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_person_create, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initOnChange()

        arguments?.let {
            val entity = TestPersonCreateFragmentArgs.fromBundle(it).testPersonEntity

            if (entity != null) {
                testPersonCreateViewModel.entity = entity
                updateUI(entity, true)
            }
        }

        binding.btnContinue.setOnClickListener { handleOnClick() }
    }

    private fun initialiseViewModel() {
        testPersonCreateViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(TestPersonCreateViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initOnChange() {
        binding.editName.setCallback {
            testPersonCreateViewModel.name = it
            binding.btnContinue.isEnabled = testPersonCreateViewModel.age != -1 && testPersonCreateViewModel.name != ""
        }

        binding.editAge.setCallback {
            testPersonCreateViewModel.age = (if (it == "") -1 else it.toInt()).toInt()
            binding.btnContinue.isEnabled = testPersonCreateViewModel.age != -1 && testPersonCreateViewModel.name != ""
        }
        binding.Autofill.setOnClickListener{
            val current = LocalDateTime.now().toString()
            binding.editName.setText(Editable.Factory.getInstance().newEditable(current))
        }

        binding.spinnerGender.setCallback { testPersonCreateViewModel.gender = it }
        binding.spinnerColor.setCallback { testPersonCreateViewModel.color = it }
    }

    private fun updateUI(entity: TestPersonEntity, lockUI: Boolean) {
        binding.editName.setText(SpannableStringBuilder(entity.name))
        binding.editName.lock(lockUI)

        binding.spinnerGender.setSelectedItem(entity.gender)
        binding.spinnerGender.lock(lockUI)

        binding.editAge.setText(SpannableStringBuilder(entity.age.toString()))
        binding.editAge.lock(lockUI)

        binding.spinnerColor.setSelectedItem(entity.skinColor)
        binding.spinnerColor.lock(lockUI)

        binding.Autofill.isEnabled = false
    }

    private fun navToFingerPrintOverViewFrag(entity: TestPersonEntity) {
        writeTestPersonToConfig(entity)

        val action = TestPersonCreateFragmentDirections
            .toFingerPrintOverViewFragment(entity)
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun writeTestPersonToConfig(entity: TestPersonEntity){
        val filename = "TestPersonEntity.txt"
        var textJSON = JSONObject()
        textJSON.put("personID", entity.personID)
        textJSON.put("name", entity.name)
        textJSON.put("gender", entity.gender)
        textJSON.put("age", entity.age)
        textJSON.put("skinColor", entity.skinColor)
        textJSON.put("timestamp", entity.timestamp)

        val file = File("sdcard/$NAME_MAIN_FOLDER/$filename")

        file.writeText(textJSON.toString())
    }

    private fun handleOnClick() {
        if (testPersonCreateViewModel.isTestPersonInitialised()) {
            navToFingerPrintOverViewFrag(testPersonCreateViewModel.entity)
        } else {
            showProgressDialogWithTitle()
            testPersonCreateViewModel.generateTestPerson()
            testPersonCreateViewModel.insertTestPerson(testPersonCreateViewModel.entity, ::onSuccess, ::onError)
        }
    }

    private fun onSuccess(id: Long) {
        testPersonCreateViewModel.entity.personID = id

        progressDialog.dismiss()
        navToFingerPrintOverViewFrag(testPersonCreateViewModel.entity)
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
        val TAG = TestPersonOverviewFragment::class.java.simpleName
    }

}
