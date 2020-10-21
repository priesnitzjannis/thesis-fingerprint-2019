package de.dali.thesisfingerprint2019.ui.main.fragment.fingerprint

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.android.support.AndroidSupportInjection
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.databinding.FragmentFingerPrintOverviewBinding
import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.ui.base.BaseFragment
import de.dali.thesisfingerprint2019.ui.base.custom.FingerPrintListAdapter
import de.dali.thesisfingerprint2019.ui.base.custom.ListWithLoadingSpinner.LIST.EMPTY
import de.dali.thesisfingerprint2019.ui.base.custom.ListWithLoadingSpinner.LIST.RESULT
import de.dali.thesisfingerprint2019.ui.main.activity.MainActivity
import de.dali.thesisfingerprint2019.ui.main.fragment.testperson.TestPersonOverviewFragment
import de.dali.thesisfingerprint2019.ui.main.fragment.testperson.TestPersonOverviewFragmentDirections
import de.dali.thesisfingerprint2019.ui.main.viewmodel.fingerprint.FingerPrintOverviewViewModel
import kotlinx.android.synthetic.main.childview_list.view.*
import kotlinx.android.synthetic.main.fragment_finger_selection.view.*
import javax.inject.Inject

// Recordsetübersicht einer einzelnen Person
class FingerPrintOverViewFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: FragmentFingerPrintOverviewBinding

    lateinit var fingerPrintOverViewModel: FingerPrintOverviewViewModel

    lateinit var testPersonOverviewFragment: TestPersonOverviewFragment

    lateinit var adapter: FingerPrintListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        initialiseViewModel()
        initialiseAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finger_print_overview, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            fingerPrintOverViewModel.entity = FingerPrintOverViewFragmentArgs.fromBundle(it).testPersonEntity
        }

        binding.fingerItems.rvList.adapter = adapter
        binding.fingerItems.rvList.addItemDecoration(
            DividerItemDecoration(
                binding.fingerItems.rvList.context,
                DividerItemDecoration.VERTICAL
            )
        )



        binding.btnAddFingerprint.setOnClickListener {
            val action = FingerPrintOverViewFragmentDirections.toFingerPrintCreateFragment(
                null,
                fingerPrintOverViewModel.entity
            )
            NavHostFragment.findNavController(this).navigate(action)
        }

        fingerPrintOverViewModel.loadFingerPrints(fingerPrintOverViewModel.entity.personID!!)
        fingerPrintOverViewModel.listOfFingerPrints.observe(this, Observer { updateList(it) })

        Logging.cancelAcquisition()


        // Button to Main Menu einbauen
        binding.btnBackToMainMenu.setOnClickListener {
            val intent = Intent(getActivity(), MainActivity::class.java)
            startActivity(intent)
            }

    }

    private fun initialiseViewModel() {
        fingerPrintOverViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FingerPrintOverviewViewModel::class.java)
    }

    private fun initialiseAdapter() {
        adapter = FingerPrintListAdapter()

        adapter.setCallback {
            val action = FingerPrintOverViewFragmentDirections
                .toFingerPrintCreateFragment(
                    null,
                    fingerPrintOverViewModel.entity
                )
            action.fingerPrintEntity = it
            NavHostFragment.findNavController(this).navigate(action)
        }
    }

    private fun updateList(fingerprints: List<FingerPrintEntity>) {
        binding.btnAddFingerprint.show()
        binding.btnBackToMainMenu.show()

        when {
            fingerprints.isEmpty() -> {
                binding.fingerItems.vfSelection.displayedChild = EMPTY.state
            }

            fingerprints.isNotEmpty() -> {
                adapter.list = fingerprints
                binding.fingerItems.vfSelection.displayedChild = RESULT.state
            }
        }
    }

}