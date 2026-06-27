package com.example.digitalbankkyc.ui.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.example.digitalbankkyc.databinding.FragmentAccountsBinding
import com.example.digitalbankkyc.domain.model.Customer
import dagger.hilt.android.AndroidEntryPoint
import com.example.digitalbankkyc.R

@AndroidEntryPoint
class AccountsFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountsViewModel by viewModels()

    private var verifiedFragment: CustomerListFragment? = null
    private var pendingFragment: CustomerListFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        observeViewModel()
        setupSearch()
    }

    private fun setupViewPager() {
        verifiedFragment = CustomerListFragment(
            emptyList(),
            onItemClick = { navigateToDetail(it) },
            onDoKycClick = { navigateToDetail(it) }
        )
        pendingFragment = CustomerListFragment(
            emptyList(),
            onItemClick = { navigateToDetail(it) },
            onDoKycClick = { navigateToDetail(it) }
        )

        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int): Fragment =
                if (position == 0) verifiedFragment!! else pendingFragment!!
        }

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "VERIFIED" else "PENDING"
        }.attach()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AccountsUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is AccountsUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val verified = viewModel.getVerifiedCustomers(state.customers)
                    val pending  = viewModel.getPendingCustomers(state.customers)
                    verifiedFragment?.updateList(verified)
                    pendingFragment?.updateList(pending)
                }
                is AccountsUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    com.google.android.material.snackbar.Snackbar
                        .make(binding.root, state.message,
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            viewModel.search(editable.toString())
        }
    }

    private fun navigateToDetail(customer: Customer) {
        val bundle = android.os.Bundle().apply {
            putInt("customerId", customer.id)
        }
        findNavController().navigate(
            R.id.action_accounts_to_detail,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}