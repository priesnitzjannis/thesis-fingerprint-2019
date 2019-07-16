package de.dali.thesisfingerprint2019.ui.main.fragment.testperson

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.android.support.AndroidSupportInjection
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity
import de.dali.thesisfingerprint2019.databinding.FragmentTestPersonOverviewBinding
import de.dali.thesisfingerprint2019.ui.base.BaseFragment
import de.dali.thesisfingerprint2019.ui.base.custom.ListWithLoadingSpinner.LIST.EMPTY
import de.dali.thesisfingerprint2019.ui.base.custom.ListWithLoadingSpinner.LIST.RESULT
import de.dali.thesisfingerprint2019.ui.base.custom.TestPersonOverviewAdapter
import de.dali.thesisfingerprint2019.ui.main.viewmodel.testperson.TestPersonOverviewViewModel
import de.dali.thesisfingerprint2019.utils.Constants
import de.dali.thesisfingerprint2019.utils.PermissionHandling
import kotlinx.android.synthetic.main.childview_list.view.*
import kotlinx.android.synthetic.main.fragment_finger_selection.view.*
import javax.inject.Inject

class TestPersonOverviewFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: FragmentTestPersonOverviewBinding

    private lateinit var progressDialog: ProgressDialog

    private lateinit var testPersonOverviewViewModel: TestPersonOverviewViewModel

    private lateinit var adapter: TestPersonOverviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        initialiseViewModel()
        initialiseAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_person_overview, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.personItems.rvList.adapter = adapter
        binding.personItems.rvList.addItemDecoration(
            DividerItemDecoration(
                binding.personItems.rvList.context,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.btnAdd.setOnClickListener {
            val action =
                TestPersonOverviewFragmentDirections.toTestPersonCreateFragment(null)
            NavHostFragment.findNavController(this).navigate(action)
        }

        testPersonOverviewViewModel.loadTestPerson()
        testPersonOverviewViewModel.listOfTestPerson.observe(this, Observer { updateList(it) })

        PermissionHandling.requestMultiplePermissions(activity, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.SETTINGS_REQUEST_CODE) {
            PermissionHandling.requestMultiplePermissions(activity, this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_main_export -> {
                showProgressDialogWithTitle()
                testPersonOverviewViewModel.exportDB(activity, { this.hideProgressDialog() }, {})
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initialiseViewModel() {
        testPersonOverviewViewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(TestPersonOverviewViewModel::class.java)
    }

    private fun initialiseAdapter() {
        adapter = TestPersonOverviewAdapter()

        adapter.setCallback {
            val action = TestPersonOverviewFragmentDirections.toTestPersonCreateFragment(it)
            NavHostFragment.findNavController(this).navigate(action)
        }
    }

    private fun updateList(testperson: List<TestPersonEntity>) {
        binding.btnAdd.show()

        when {
            testperson.isEmpty() -> {
                binding.personItems.vfSelection.displayedChild = EMPTY.state
            }

            testperson.isNotEmpty() -> {
                adapter.list = testperson
                binding.personItems.vfSelection.displayedChild = RESULT.state
            }
        }
    }

    private fun showProgressDialogWithTitle() {
        progressDialog = ProgressDialog(context)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setMessage("Exporting DB ...")
        progressDialog.show()
    }

    private fun hideProgressDialog(){
        progressDialog.dismiss()
    }


    companion object {
        val TAG = TestPersonOverviewFragment::class.java.simpleName
    }

}