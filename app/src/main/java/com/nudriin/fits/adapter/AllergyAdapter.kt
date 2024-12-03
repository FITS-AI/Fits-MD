package com.nudriin.fits.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.nudriin.fits.data.dto.allergy.AllergyItem
import com.nudriin.fits.databinding.AllergyChipBinding

class AllergyAdapter(
    private val allergyList: List<AllergyItem>,
    private val onSelectionChanged: (List<AllergyItem>) -> Unit
) : RecyclerView.Adapter<AllergyAdapter.AllergyViewHolder>() {

    private val selectedAllergies = mutableListOf<AllergyItem>()

    inner class AllergyViewHolder(private val binding: AllergyChipBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val chip: Chip = binding.allergyChip
        fun bind(allergyItem: AllergyItem) {
            chip.text = allergyItem.allergyName
            chip.isCheckable = true
            chip.isChecked = allergyItem.isSelected

            chip.setOnCheckedChangeListener { _, isChecked ->
                allergyItem.isSelected = isChecked
                if (isChecked) {
                    selectedAllergies.add(allergyItem)
                } else {
                    selectedAllergies.remove(allergyItem)
                }
                onSelectionChanged(selectedAllergies)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyViewHolder {
        val binding =
            AllergyChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllergyViewHolder(binding)
    }

    override fun getItemCount(): Int = allergyList.size

    override fun onBindViewHolder(holder: AllergyViewHolder, position: Int) {
        val allergy = allergyList[position]
        holder.bind(allergy)
    }
}