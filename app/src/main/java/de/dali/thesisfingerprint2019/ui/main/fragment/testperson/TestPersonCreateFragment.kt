package de.dali.thesisfingerprint2019.ui.main.fragment.testperson


import android.app.ProgressDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import dagger.android.support.AndroidSupportInjection
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity
import de.dali.thesisfingerprint2019.databinding.FragmentTestPersonCreateBinding
import de.dali.thesisfingerprint2019.ui.base.BaseFragment
import de.dali.thesisfingerprint2019.ui.main.viewmodel.testperson.TestPersonCreateViewModel
import javax.inject.Inject

class TestPersonCreateFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: FragmentTestPersonCreateBinding

    lateinit var testPersonCreateViewModel: TestPersonCreateViewModel

    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        initialiseViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_person_create, container, false)

        return binding.root
    }

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

    private fun initOnChange() {
        binding.editAge.setCallback { testPersonCreateViewModel.age = it }
        binding.spinnerGender.setCallback { testPersonCreateViewModel.gender = it }
        binding.spinnerColor.setCallback { testPersonCreateViewModel.color = it }
    }

    private fun updateUI(entity: TestPersonEntity, lockUI: Boolean) {
        binding.spinnerGender.setSelectedItem(entity.gender)
        binding.spinnerGender.lock(lockUI)

        binding.editAge.setText(SpannableStringBuilder(entity.age.toString()))
        binding.editAge.lock(lockUI)

        binding.spinnerColor.setSelectedItem(entity.skinColor)
        binding.spinnerColor.lock(lockUI)

    }

    private fun navToFingerPrintOverViewFrag(entity: TestPersonEntity) {
        val action = TestPersonCreateFragmentDirections
            .actionTestPersonCreateFragmentToFingerPrintOverViewFragment(entity)
        NavHostFragment.findNavController(this).navigate(action)
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
