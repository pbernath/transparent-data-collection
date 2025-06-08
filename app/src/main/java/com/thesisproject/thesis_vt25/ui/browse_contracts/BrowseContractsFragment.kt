package com.thesisproject.thesis_vt25.ui.browse_contracts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thesisproject.thesis_vt25.common.adapter.ContractsListAdapter
import com.thesisproject.thesis_vt25.common.utils.promptOnboarding
import com.thesisproject.thesis_vt25.databinding.FragmentBrowseContractsBinding

class BrowseContractsFragment : Fragment() {

    private var _binding: FragmentBrowseContractsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBrowseContractsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        progressBar = binding.progressBarBrowseContracts

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        promptOnboarding(findNavController())

        val browseContractsViewModel = ViewModelProvider(this)[BrowseContractsViewModel::class.java]
        recyclerView = binding.recyclerViewBrowseContracts
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val navController = findNavController()
        val adapter = ContractsListAdapter(
            emptyList(),
            false
        ) { clickedItem ->
            val action =
                BrowseContractsFragmentDirections.actionNavBrowseContractsToNavContractDetails(
                    clickedItem.id
                )
            navController.navigate(action)
        }
        recyclerView.adapter = adapter

        browseContractsViewModel.contractList.observe(viewLifecycleOwner) { newList ->
            if (newList.isEmpty()){
                binding.textBrowseContractsEmpty.visibility = View.VISIBLE
            }else{
                binding.textBrowseContractsEmpty.visibility = View.GONE
            }
            adapter.setData(newList) // or adapter.setData(newList)
            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        swipeRefreshLayout = binding.swipeRefreshLayoutBrowseContracts
        swipeRefreshLayout.setOnRefreshListener {
            // Reload data

            browseContractsViewModel.updateAvailableContracts()
            swipeRefreshLayout.isRefreshing = false // Stop spinner after reload
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}