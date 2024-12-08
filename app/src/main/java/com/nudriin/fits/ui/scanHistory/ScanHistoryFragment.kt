package com.nudriin.fits.ui.scanHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nudriin.fits.adapter.ScanHistoryAdapter
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
    }

    private fun setupView() {
        val layoutManager = LinearLayoutManager(context)
        binding.rvScanHistory.layoutManager = layoutManager


        scanHistoryViewModel.getAllProduct().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
//                    showLoading(true)
                }

                is Result.Success -> {
//                    showLoading(false)
                    setScanHistory(result.data.userHistory)
                }

                is Result.Error -> {
//                    showLoading(false)
                    result.error.getContentIfNotHandled().let { toastText ->
                        showToast(requireContext(), toastText.toString())
                    }
                }
            }
        }
    }

    private fun setScanHistory(scanHistory: List<UserHistoryItem>) {
        val adapter = ScanHistoryAdapter(scanHistory)
        binding.rvScanHistory.adapter = adapter
    }

}