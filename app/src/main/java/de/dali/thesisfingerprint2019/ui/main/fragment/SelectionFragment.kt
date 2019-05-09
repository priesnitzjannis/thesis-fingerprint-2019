package de.dali.thesisfingerprint2019.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import dagger.android.support.AndroidSupportInjection
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.databinding.FragmentFingerSelectionBinding
import de.dali.thesisfingerprint2019.ui.base.BaseFragment
import de.dali.thesisfingerprint2019.ui.base.custom.FingerPrintListAdapter
import de.dali.thesisfingerprint2019.ui.main.fragment.SelectionFragment.LIST.EMPTY
import de.dali.thesisfingerprint2019.ui.main.fragment.SelectionFragment.LIST.RESULT
import de.dali.thesisfingerprint2019.ui.main.viewmodel.SelectionViewModel
import kotlinx.android.synthetic.main.childview_list.view.*
import kotlinx.android.synthetic.main.fragment_finger_selection.view.*
import javax.inject.Inject


class SelectionFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: FragmentFingerSelectionBinding

    lateinit var detailsViewModel: SelectionViewModel

    lateinit var adapter: FingerPrintListAdapter

    enum class LIST(val state: Int) {
        LOADING(0),
        RESULT(1),
        EMPTY(2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        initialiseViewModel()
        initialiseAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finger_selection, container, false)

        binding.root.rvListFingerprint.adapter = adapter
        binding.root.btnAddFingerprint.setOnClickListener {
            val action = SelectionFragmentDirections.toDetailsFragment()
            findNavController(this).navigate(action)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailsViewModel.loadFingerPrints()
        detailsViewModel.listOfFingerPrints.observe(this, Observer { updateList(it) })
    }


    private fun initialiseViewModel() {
        detailsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SelectionViewModel::class.java)
    }

    private fun initialiseAdapter() {
        adapter = FingerPrintListAdapter()

        adapter.setCallback {
            val action = SelectionFragmentDirections.toDetailsFragment()
            action.fingerPrintEntity = it
            findNavController(this).navigate(action)
        }
    }

    private fun updateList(fingerprints : List<FingerPrintEntity>){
        binding.root.btnAddFingerprint.show()

        when{
            fingerprints.isEmpty() -> {
                binding.root.vfSelection.displayedChild = EMPTY.state
            }

            fingerprints.isNotEmpty() -> {
                adapter.list = fingerprints
                binding.root.vfSelection.displayedChild = RESULT.state
            }
        }
    }

}