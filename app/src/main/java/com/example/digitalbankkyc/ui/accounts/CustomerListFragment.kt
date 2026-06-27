package com.example.digitalbankkyc.ui.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.digitalbankkyc.databinding.FragmentCustomerListBinding
import com.example.digitalbankkyc.domain.model.Customer
import com.example.digitalbankkyc.ui.adapter.CustomerAdapter

class CustomerListFragment(
    private val customers: List<Customer>,
    private val onItemClick: (Customer) -> Unit,
    private val onDoKycClick: (Customer) -> Unit
) : Fragment() {

    private var _binding: FragmentCustomerListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CustomerAdapter(onItemClick, onDoKycClick)

        // 2-column grid
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        updateList(customers)
    }

    fun updateList(newList: List<Customer>) {
        if (_binding == null) return
        adapter.submitList(newList)
        binding.tvEmpty.visibility = if (newList.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (newList.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}