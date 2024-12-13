package com.nudriin.fits.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nudriin.fits.data.domain.HealthAnalysis
import com.nudriin.fits.databinding.AnalysisResultCardBinding

class AnalysisAdapter(
    private val analysisList: List<HealthAnalysis>
) : RecyclerView.Adapter<AnalysisAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: AnalysisResultCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(analysisItem: HealthAnalysis) {
            binding.tvAnalysisResultHeader.text = analysisItem.name
            binding.tvAnalysisResultDescription.text = analysisItem.description
            binding.tvAnalysisResultContent.text = analysisItem.ingredient
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AnalysisResultCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = analysisList.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val analysis = analysisList[position]
        holder.bind(analysis)
    }
}