package com.nudriin.fits.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.nudriin.fits.data.dto.user.UsersAllergyItem
import com.nudriin.fits.databinding.AllergyChipBinding

class SavedAllergyAdapter(
    private val allergyList: List<UsersAllergyItem>,
) : RecyclerView.Adapter<SavedAllergyAdapter.SavedAllergyViewHolder>() {


    inner class SavedAllergyViewHolder(private val binding: AllergyChipBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val chip: Chip = binding.allergyChip
        fun bind(allergyItem: UsersAllergyItem) {
            chip.text = allergyItem.allergy.allergyName
            chip.isCheckable = true
            chip.isChecked = false
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedAllergyViewHolder {
        val binding =
            AllergyChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedAllergyViewHolder(binding)
    }

    override fun getItemCount(): Int = allergyList.size

    override fun onBindViewHolder(holder: SavedAllergyViewHolder, position: Int) {
        val allergy = allergyList[position]
        holder.bind(allergy)
    }
}