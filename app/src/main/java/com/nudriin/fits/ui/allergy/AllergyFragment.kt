package com.nudriin.fits.ui.allergy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nudriin.fits.adapter.AllergyAdapter
import com.nudriin.fits.adapter.SavedAllergyAdapter
import com.nudriin.fits.data.dto.allergy.AllergyItem
import com.nudriin.fits.data.dto.user.UsersAllergyItem
import com.nudriin.fits.databinding.FragmentAllergyBinding
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory
import com.nudriin.fits.utils.showToast

class AllergyFragment : Fragment() {
    private var _binding: FragmentAllergyBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private val allergyViewModel: AllergyViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var allergyAdapter: AllergyAdapter
    private lateinit var savedAllergyAdapter: SavedAllergyAdapter

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
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }

        val savedAllergyLayoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        binding.rvAllergy.layoutManager = layoutManager
        binding.rvSavedAllergy.layoutManager = savedAllergyLayoutManager

        allergyViewModel.getAllAllergy().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    allergyAdapter = AllergyAdapter(result.data.data) { selectedAllergies ->
                        binding.btnSave.isEnabled = selectedAllergies.isNotEmpty()
                    }
                    binding.rvAllergy.adapter = allergyAdapter

                    if (result.data.data.isEmpty()) {
                        binding.rvAllergy.visibility = View.GONE
                        binding.ivAllergyNotFound.visibility = View.VISIBLE
                        binding.tvAllergyNotFound.visibility = View.VISIBLE
                    }
                }

                is Result.Error -> {
                    showLoading(false)
                    binding.rvAllergy.visibility = View.GONE
                    binding.ivAllergyNotFound.visibility = View.VISIBLE
                    binding.tvAllergyNotFound.visibility = View.VISIBLE
                    result.error.getContentIfNotHandled().let { toastText ->
                        showToast(requireContext(), toastText.toString())
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSavedAllergy.setOnClickListener {

            allergyViewModel.getAllergyByUserId().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.btnSave.isEnabled = false
                    }

                    is Result.Success -> {
                        savedAllergyAdapter =
                            SavedAllergyAdapter(result.data.user.first().usersAllergy)
                        binding.rvSavedAllergy.adapter = savedAllergyAdapter
                        bottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_EXPANDED

                        if (result.data.user.first().usersAllergy.isEmpty()) {
                            binding.rvSavedAllergy.visibility = View.GONE
                            binding.ivNotFound.visibility = View.VISIBLE
                            binding.tvNotFound.visibility = View.VISIBLE
                        }
                    }

                    is Result.Error -> {
                        binding.btnSave.isEnabled = true
                        binding.rvSavedAllergy.visibility = View.GONE
                        binding.ivNotFound.visibility = View.VISIBLE
                        binding.tvNotFound.visibility = View.VISIBLE
                        result.error.getContentIfNotHandled().let { toastText ->
                            showToast(requireContext(), toastText.toString())
                        }
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val selectedAllergies = allergyAdapter.selectedAllergies
            val allergyIds = selectedAllergies.map { it.id }
            allergyViewModel.saveUserAllergy(allergyIds).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.btnSave.isEnabled = false
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        Log.d("AllergyFragment", result.data.toString())
                        binding.btnSave.isEnabled = true
                        showToast(requireContext(), result.data.message)
                        val toProfile =
                            AllergyFragmentDirections.actionAllergyFragmentToProfileFragment()
                        Navigation.findNavController(binding.root).navigate(toProfile)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        binding.btnSave.isEnabled = true
                        result.error.getContentIfNotHandled().let { toastText ->
                            showToast(requireContext(), toastText.toString())
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}