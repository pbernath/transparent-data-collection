package com.thesisproject.thesis_vt25.ui.rewards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thesisproject.thesis_vt25.data.model.Reward
import com.thesisproject.thesis_vt25.databinding.RewardsRowItemBinding

class RewardsListAdapter(val rewards: List<Reward>): RecyclerView.Adapter<RewardsListAdapter.ItemViewHolder>(){

    inner class ItemViewHolder(val binding: RewardsRowItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(reward: Reward){
            binding.textRewardItemTitle.text = reward.title
            binding.buttonRewardsItemClaim.text = reward.price.toString()
            binding.textRewardItemDescription.text = reward.description
        }
    }

    var userCredits = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        val binding = RewardsRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = rewards[position]
        holder.bind(item)

        if (userCredits < item.price){
            holder.binding.buttonRewardsItemClaim.apply {
                isEnabled = false
                //setBackgroundColor(resources.getColor(R.color.darker_gray))
            }
        } else {
            holder.binding.buttonRewardsItemClaim.apply {
                isEnabled = true
            }
        }
    }

    fun setCredits(credits: Int){
        userCredits = credits
    }

    override fun getItemCount(): Int {
        return rewards.size
    }
}