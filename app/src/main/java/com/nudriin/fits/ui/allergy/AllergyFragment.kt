package com.nudriin.fits.ui.allergy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nudriin.fits.R
import com.nudriin.fits.databinding.FragmentAllergyBinding
import com.nudriin.fits.databinding.FragmentProfileBinding

class AllergyFragment : Fragment() {
    private var _binding: FragmentAllergyBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllergyBinding.inflate(inflater, container, false)
        return binding.root
    }
}