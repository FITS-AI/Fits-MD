package com.nudriin.fits.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nudriin.fits.R
import com.nudriin.fits.data.domain.HealthAnalysis
import com.nudriin.fits.databinding.ProductDetailsCardBinding

class ScanHistoryDetailAdapter(
    private val context: Context,
    private val analysisList: List<HealthAnalysis>
) :
    RecyclerView.Adapter<ScanHistoryDetailAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ProductDetailsCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(analysis: HealthAnalysis) {
            binding.tvProductIngredientTitle.text = analysis.name
            binding.tvProductIngredientDescription.text = analysis.description
            binding.tvProductIngredient.text =
                context.resources.getString(R.string.content_ing, analysis.ingredient)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ProductDetailsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = analysisList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val analysis = analysisList[position]
        holder.bind(analysis)
    }
}