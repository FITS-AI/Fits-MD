package com.nudriin.fits.ui.scanHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nudriin.fits.adapter.ScanHistoryAdapter
import com.nudriin.fits.data.domain.HealthAnalysis
import com.nudriin.fits.data.dto.product.UserHistoryItem
import com.nudriin.fits.databinding.FragmentScanHistoryBinding
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory
import com.nudriin.fits.utils.showToast

class ScanHistoryFragment : Fragment() {
    private var _binding: FragmentScanHistoryBinding? = null
    private val binding get() = _binding!!

    private val scanHistoryViewModel: ScanHistoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupView() {
        val layoutManager = LinearLayoutManager(context)
        binding.rvScanHistory.layoutManager = layoutManager


        scanHistoryViewModel.getAllProduct().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    if (result.data.userHistory.isNotEmpty()) {
                        binding.rvScanHistory.visibility = View.VISIBLE
                        binding.ivNotFound.visibility = View.GONE
                        binding.tvNotFound.visibility = View.GONE
                    }
                    setScanHistory(result.data.userHistory)
                }

                is Result.Error -> {
                    showLoading(false)
                    result.error.getContentIfNotHandled().let { toastText ->
                        showToast(requireContext(), toastText.toString())
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setScanHistory(scanHistory: List<UserHistoryItem>) {
        val adapter = ScanHistoryAdapter(scanHistory)
        binding.rvScanHistory.adapter = adapter
        adapter.setOnItemClickCallback(object : ScanHistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(
                label: String,
                name: String,
                overall: String,
                sugar: HealthAnalysis,
                fat: HealthAnalysis,
                protein: HealthAnalysis,
                calories: HealthAnalysis
            ) {
                moveToScanHistoryDetail(label, name, overall, sugar, fat, protein, calories)
            }

        })
    }

    private fun moveToScanHistoryDetail(
        label: String,
        name: String,
        overall: String,
        sugar: HealthAnalysis,
        fat: HealthAnalysis,
        protein: HealthAnalysis,
        calories: HealthAnalysis
    ) {
        val toDetail =
            ScanHistoryFragmentDirections.actionScanHistoryFragmentToScanHistoryDetailFragment(
                label, name, overall, sugar, fat, protein, calories
            )

        Navigation.findNavController(binding.root).navigate(toDetail)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}