package com.nudriin.fits.ui.scanHistoryDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nudriin.fits.databinding.FragmentScanHistoryDetailBinding


class ScanHistoryDetailFragment : Fragment() {
    private var _binding: FragmentScanHistoryDetailBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
}