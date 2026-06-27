package com.example.digitalbankkyc.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.digitalbankkyc.R
import com.example.digitalbankkyc.databinding.ItemCustomerBinding
import com.example.digitalbankkyc.domain.model.Customer
import com.example.digitalbankkyc.domain.model.KycStatus

class CustomerAdapter(
    private val onItemClick: (Customer) -> Unit,
    private val onDoKycClick: (Customer) -> Unit
) : ListAdapter<Customer, CustomerAdapter.CustomerViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = ItemCustomerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CustomerViewHolder(
        private val binding: ItemCustomerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(customer: Customer) {
            binding.tvName.text = "${customer.firstName} ${customer.lastName}"

            // Mask the IBAN — show only last 4 characters
            val masked = "**** ${customer.iban.takeLast(4)}"
            binding.tvAccountNumber.text = masked

            // Format balance
            binding.tvBalance.text = "Rs ${"%,.0f".format(customer.balance)}"

            // Load avatar with Glide
            Glide.with(binding.imgAvatar.context)
                .load(customer.avatarUrl)
                .circleCrop()
                .placeholder(R.drawable.circle_bg)
                .into(binding.imgAvatar)

            // KYC Badge
            if (customer.kycStatus == KycStatus.VERIFIED) {
                binding.tvKycBadge.text = "VERIFIED"
                binding.tvKycBadge.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.colorVerified)
                )
                binding.btnDoKyc.visibility = View.GONE
            } else {
                binding.tvKycBadge.text = "PENDING"
                binding.tvKycBadge.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.colorPending)
                )
                binding.btnDoKyc.visibility = View.VISIBLE
            }

            // Click listeners
            binding.root.setOnClickListener { onItemClick(customer) }
            binding.btnDoKyc.setOnClickListener { onDoKycClick(customer) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Customer, newItem: Customer) =
            oldItem == newItem
    }
}