package com.nudriin.fits.ui.scanHistoryDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nudriin.fits.adapter.ScanHistoryDetailAdapter
import com.nudriin.fits.data.domain.HealthAnalysis
import com.nudriin.fits.databinding.FragmentScanHistoryDetailBinding


class ScanHistoryDetailFragment : Fragment() {
    private var _binding: FragmentScanHistoryDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var analysisData: List<HealthAnalysis>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupView() {
        val historyData = ScanHistoryDetailFragmentArgs.fromBundle(arguments as Bundle)

        val label = historyData.label
        val name = historyData.name
        val overall = historyData.overall
        val sugar = historyData.sugar
        val fat = historyData.fat
        val protein = historyData.protein
        val calories = historyData.calories
        val assessment = historyData.assessment.ifEmpty {
            "No health assessment"
        }

        analysisData = listOf(sugar, fat, protein, calories)

        val layoutManager = LinearLayoutManager(context)
        binding.rvAnalysisResult.layoutManager = layoutManager

        binding.tvLabel.text = label
        binding.tvProductTitle.text = name
        binding.layoutOverall.tvProductIngredientTitle.text = "Overall"
        binding.layoutOverall.tvProductIngredientDescription.text = overall
        binding.layoutOverall.tvAssessmentProduct.text = assessment

        val adapter = ScanHistoryDetailAdapter(requireContext(), analysisData)
        binding.rvAnalysisResult.adapter = adapter
    }

    private fun setupAction() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}