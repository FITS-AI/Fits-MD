package com.nudriin.fits.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nudriin.fits.data.dto.product.UserHistoryItem
import com.nudriin.fits.databinding.ScanHistoryHomeCardBinding

class ScanHistoryHomeAdapter(private val scanHistoryList: List<UserHistoryItem>) :
    RecyclerView.Adapter<ScanHistoryHomeAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ScanHistoryHomeCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(scanHistory: UserHistoryItem) {
            binding.tvScanHistoryTitle.text = scanHistory.product.name
            binding.tvScanHistoryDescription.text = scanHistory.product.grade.gradeDesc
            binding.tvScanHistoryLabel.text = scanHistory.product.grade.gradeName
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ScanHistoryHomeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = scanHistoryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scanHistory = scanHistoryList[position]
        holder.bind(scanHistory)
    }
}