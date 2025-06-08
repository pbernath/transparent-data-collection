package com.thesisproject.thesis_vt25.ui.rewards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thesisproject.thesis_vt25.common.utils.promptOnboarding
import com.thesisproject.thesis_vt25.databinding.FragmentRewardsBinding

class RewardsFragment : Fragment() {

    private var _binding: FragmentRewardsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rewardsViewModel =
            ViewModelProvider(this).get(RewardsViewModel::class.java)

        _binding = FragmentRewardsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        promptOnboarding(findNavController())

        val rewardsViewModel =
            ViewModelProvider(this).get(RewardsViewModel::class.java)
        rewardsViewModel.updateUserInfo()

        val recyclerView = binding.recyclerViewRewards

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        val adapter = RewardsListAdapter(rewardsViewModel.getRewards())
        recyclerView.adapter = adapter
        rewardsViewModel.user.observe(viewLifecycleOwner) { user ->
            adapter.setCredits(user?.credits?: 0)
            adapter.notifyDataSetChanged()
            binding.textUserCredits.text = user?.credits.toString()
        }


        swipeRefreshLayout = binding.swipeRefreshLayoutRewards
        swipeRefreshLayout.setOnRefreshListener {
            // Reload data
            rewardsViewModel.updateUserInfo()
            swipeRefreshLayout.isRefreshing = false // Stop spinner after reload
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}