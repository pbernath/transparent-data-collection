package com.thesisproject.thesis_vt25.ui.my_contracts

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
import com.thesisproject.thesis_vt25.databinding.FragmentAcceptedContractsPageBinding


class AcceptedContractsPageFragment : Fragment() {

    private var _binding: FragmentAcceptedContractsPageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var recyclerViewAcceptedContracts: RecyclerView
    lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAcceptedContractsPageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        progressBar = binding.progressBarAcceptedContracts

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myContractsViewModel =
            ViewModelProvider(this)[MyContractsViewModel::class.java]
        recyclerViewAcceptedContracts = binding.recyclerViewAcceptedContracts
        recyclerViewAcceptedContracts.layoutManager = LinearLayoutManager(this.context)
        recyclerViewAcceptedContracts.setHasFixedSize(true)
        val navController = findNavController()
        val adapter = ContractsListAdapter(
            emptyList(),
            true
        ) { clickedItem ->
            val action =
                MyContractsFragmentDirections.actionNavMyContractsToNavContractDetails(
                    clickedItem.id
                )
            navController.navigate(action)
        }
        recyclerViewAcceptedContracts.adapter = adapter

        myContractsViewModel.acceptedContractList.observe(viewLifecycleOwner) { newList ->
            if (newList.isEmpty()){
                binding.textAcceptedContractsEmpty.visibility = View.VISIBLE
            }else{
                binding.textAcceptedContractsEmpty.visibility = View.GONE
            }
            adapter.setData(newList)
            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
            recyclerViewAcceptedContracts.visibility = View.VISIBLE
        }

        swipeRefreshLayout = binding.swipeRefreshLayoutAcceptedContractsPage
        swipeRefreshLayout.setOnRefreshListener {
            // Reload data
            myContractsViewModel.updateMyContracts()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}