package com.nudriin.fits.ui.allergy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.nudriin.fits.adapter.AllergyAdapter
import com.nudriin.fits.databinding.FragmentAllergyBinding
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory
import com.nudriin.fits.utils.showToast

class AllergyFragment : Fragment() {
    private var _binding: FragmentAllergyBinding? = null
    private val binding get() = _binding!!

    private val allergyViewModel: AllergyViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var allergyAdapter: AllergyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllergyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupView() {
        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        binding.rvAllergy.layoutManager = layoutManager

        allergyViewModel.getAllAllergy().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                }

                is Result.Success -> {
                    allergyAdapter = AllergyAdapter(result.data.data) { selectedAllergies ->
                        binding.btnSave.isEnabled = selectedAllergies.isNotEmpty()
                    }
                    binding.rvAllergy.adapter = allergyAdapter
                }

                is Result.Error -> {
                    result.error.getContentIfNotHandled().let { toastText ->
                        showToast(requireContext(), toastText.toString())
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.btnSave.setOnClickListener {
            val selectedAllergies = allergyAdapter.selectedAllergies
            val allergyIds = selectedAllergies.map { it.id }
            allergyViewModel.saveUserAllergy(allergyIds).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.btnSave.isEnabled = false
                    }

                    is Result.Success -> {
                        Log.d("AllergyFragment", result.data.toString())
                        binding.btnSave.isEnabled = true
                        showToast(requireContext(), result.data.message)
                        val toProfile =
                            AllergyFragmentDirections.actionAllergyFragmentToProfileFragment()
                        Navigation.findNavController(binding.root).navigate(toProfile)
                    }

                    is Result.Error -> {
                        binding.btnSave.isEnabled = true
                        result.error.getContentIfNotHandled().let { toastText ->
                            showToast(requireContext(), toastText.toString())
                        }
                    }
                }
            }
        }
    }
}