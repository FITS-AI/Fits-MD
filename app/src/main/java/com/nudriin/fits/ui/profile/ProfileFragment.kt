package com.nudriin.fits.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.nudriin.fits.R
import com.nudriin.fits.common.AuthViewModel
import com.nudriin.fits.databinding.FragmentProfileBinding
import com.nudriin.fits.utils.ViewModelFactory

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupAction()
    }

    private fun setupView() {
        authViewModel.getSession().observe(viewLifecycleOwner) { session ->
            binding.tvName.text = session.name
            binding.tvEmail.text = session.email
        }
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.llHealthPreferences.setOnClickListener {
            moveToAllergy()
        }

        binding.llAppSetting.setOnClickListener {
            moveToAppSettings()
        }

        binding.llScanHistory.setOnClickListener {
            moveToScanHistory()
        }
    }

    private fun moveToAllergy() {
        val toAllergy = ProfileFragmentDirections.actionProfileFragmentToAllergyFragment()
        Navigation.findNavController(binding.root).navigate(toAllergy)
    }

    private fun moveToAppSettings() {
        val toAppSettings = ProfileFragmentDirections.actionProfileFragmentToAppSettingFragment()
        Navigation.findNavController(binding.root).navigate(toAppSettings)
    }

    private fun moveToScanHistory() {
        val toScanHistory = ProfileFragmentDirections.actionProfileFragmentToScanHistoryFragment()
        Navigation.findNavController(binding.root).navigate(toScanHistory)
    }

}