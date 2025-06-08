package com.thesisproject.thesis_vt25.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thesisproject.thesis_vt25.common.utils.removeBrackets
import com.thesisproject.thesis_vt25.common.utils.toReadable
import com.thesisproject.thesis_vt25.data.model.Contract
import com.thesisproject.thesis_vt25.databinding.ContractsRowItemBinding

class ContractsListAdapter(val contracts: List<Contract>, val showStatus: Boolean,
                           val onItemClicked: (Contract) -> Unit) :
                            RecyclerView.Adapter<ContractsListAdapter.ItemViewHolder>(){

    private var contractItems: List<Contract> = emptyList()

    inner class ItemViewHolder(val binding: ContractsRowItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(contract: Contract){
            binding.textItemTitle.text = contract.title
            binding.textItemReward.text = contract.reward.toString()
            binding.textItemStatus.text = contract.status.toString()
            binding.textItemPermissions.text = contract.permissions.toReadable().toString().removeBrackets()
            binding.textItemStart.text = contract.start
            binding.textItemEnd.text  = contract.end
            binding.textItemOrganization.text = contract.organization
            binding.root.setOnClickListener {
                onItemClicked(contract)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        val binding = ContractsRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        val item = contractItems[position]
        if (showStatus) {
            holder.binding.containerRowItemStatus.visibility = View.VISIBLE

        } else {
            holder.binding.containerRowItemStatus.visibility = View.GONE
        }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return contractItems.size
    }

    fun setData(newList: List<Contract>) {
        contractItems = newList
    }
}