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
import com.thesisproject.thesis_vt25.databinding.FragmentPastContractsPageBinding


class PastContractsPageFragment : Fragment() {

    private var _binding: FragmentPastContractsPageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var recyclerViewPastContracts: RecyclerView
    lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPastContractsPageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        progressBar = binding.progressBarPastContracts
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myContractsViewModel =
            ViewModelProvider(this)[MyContractsViewModel::class.java]
        recyclerViewPastContracts = binding.recyclerViewPastContracts
        recyclerViewPastContracts.layoutManager = LinearLayoutManager(this.context)
        recyclerViewPastContracts.setHasFixedSize(true)
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
        recyclerViewPastContracts.adapter = adapter

        myContractsViewModel.pastContractList.observe(viewLifecycleOwner) { newList ->
            if (newList.isEmpty()){
                binding.textPastContractsEmpty.visibility = View.VISIBLE
            }else{
                binding.textPastContractsEmpty.visibility = View.GONE
            }
            adapter.setData(newList) // or adapter.setData(newList)
            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
            recyclerViewPastContracts.visibility = View.VISIBLE
        }

        swipeRefreshLayout = binding.swipeRefreshLayoutPastContractsPage
        swipeRefreshLayout.setOnRefreshListener {
            // Reload data
            myContractsViewModel.updateMyContracts()
            swipeRefreshLayout.isRefreshing = false // Stop spinner after reload
        }

        recyclerViewPastContracts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val canScrollUp = recyclerView.canScrollVertically(-1)
                swipeRefreshLayout.isEnabled = !canScrollUp
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}