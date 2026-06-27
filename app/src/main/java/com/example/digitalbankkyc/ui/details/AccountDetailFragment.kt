package com.example.digitalbankkyc.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.digitalbankkyc.R
import com.example.digitalbankkyc.databinding.FragmentAccountDetailBinding
import com.example.digitalbankkyc.domain.model.Customer
import com.example.digitalbankkyc.domain.model.KycStatus
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AccountDetailFragment : Fragment() {

    private var _binding: FragmentAccountDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountDetailViewModel by viewModels()
    private var customerId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get customerId from arguments
        customerId = arguments?.getInt("customerId") ?: -1

        observeViewModel()
        viewModel.loadCustomer(customerId)
    }

    // Called when returning from CameraFragment after selfie
    override fun onResume() {
        super.onResume()
        if (customerId != -1) {
            viewModel.refreshCustomer(customerId)
        }
    }

    private fun observeViewModel() {
        viewModel.detailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DetailUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is DetailUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    bindCustomer(state.customer)
                }
                is DetailUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    com.google.android.material.snackbar.Snackbar
                        .make(binding.root, state.message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

        viewModel.ifscState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is IfscUiState.Loading -> {
                    binding.progressIfsc.visibility = View.VISIBLE
                    binding.tvBankBranch.visibility = View.GONE
                }
                is IfscUiState.Success -> {
                    binding.progressIfsc.visibility = View.GONE
                    binding.tvIfscError.visibility = View.GONE
                    binding.tvBankBranch.visibility = View.VISIBLE
                    binding.tvBankBranch.text = "${state.data.bank}, ${state.data.branch}"
                }
                is IfscUiState.Error -> {
                    binding.progressIfsc.visibility = View.GONE
                    binding.tvIfscError.visibility = View.VISIBLE
                    binding.tvIfscError.text = state.message
                }
            }
        }
    }

    private fun bindCustomer(customer: Customer) {
        binding.tvName.text = "${customer.firstName} ${customer.lastName}"
        binding.tvAccountNumber.text = "**** ${customer.iban.takeLast(4)}"
        binding.tvBalance.text = "Rs ${"%,.0f".format(customer.balance)}"
        binding.tvDob.text = customer.dateOfBirth
        binding.tvNationality.text = customer.nationality
        binding.tvAddress.text = customer.address
        binding.tvContact.text = customer.phone
        binding.tvIfsc.text = customer.ifscCode

        // Load avatar or selfie
        if (customer.kycStatus == KycStatus.VERIFIED && customer.selfieImagePath != null) {
            val selfieFile = File(customer.selfieImagePath)
            if (selfieFile.exists()) {
                Glide.with(this)
                    .load(selfieFile)
                    .circleCrop()
                    .into(binding.imgAvatar)
            }
        } else {
            Glide.with(this)
                .load(customer.avatarUrl)
                .circleCrop()
                .placeholder(R.drawable.circle_bg)
                .into(binding.imgAvatar)
        }

        // KYC Badge
        if (customer.kycStatus == KycStatus.VERIFIED) {
            binding.tvKycBadge.text = "KYC VERIFIED"
            binding.tvKycBadge.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.colorVerified)
            )
            // Show selfie image and retake button
            binding.btnDoKyc.visibility = View.GONE
            binding.btnRetakeSelfie.visibility = View.VISIBLE
            if (customer.selfieImagePath != null) {
                val selfieFile = File(customer.selfieImagePath)
                if (selfieFile.exists()) {
                    binding.imgSelfie.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(selfieFile)
                        .circleCrop()
                        .into(binding.imgSelfie)
                }
            }
        } else {
            binding.tvKycBadge.text = "PENDING"
            binding.tvKycBadge.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.colorPending)
            )
            binding.btnDoKyc.visibility = View.VISIBLE
            binding.btnRetakeSelfie.visibility = View.GONE
            binding.imgSelfie.visibility = View.GONE
        }

        // Button click listeners
        binding.btnDoKyc.setOnClickListener {
            navigateToCamera()
        }
        binding.btnRetakeSelfie.setOnClickListener {
            navigateToCamera()
        }
    }

    private fun navigateToCamera() {
        val bundle = Bundle().apply {
            putInt("customerId", customerId)
        }
        findNavController().navigate(R.id.action_detail_to_camera, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}