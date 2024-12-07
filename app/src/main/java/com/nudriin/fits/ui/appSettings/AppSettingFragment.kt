package com.nudriin.fits.ui.appSettings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nudriin.fits.databinding.FragmentAppSettingBinding
import com.nudriin.fits.utils.ViewModelFactory

class AppSettingFragment : Fragment() {
    private var _binding: FragmentAppSettingBinding? = null
    private val binding get() = _binding!!

    private val appSettingsViewModel: AppSettingsViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupView() {
        appSettingsViewModel.getSettings().observe(viewLifecycleOwner) { settings ->
            binding.switchDarkMode.isChecked = settings.darkMode
            binding.switchDiabetes.isChecked = settings.diabetes
            binding.switchInstruction.isChecked = settings.instruction
        }
    }

    private fun setupAction() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            appSettingsViewModel.saveTheme(isChecked)
        }

        binding.switchDiabetes.setOnCheckedChangeListener { _, isChecked ->
            appSettingsViewModel.saveDiabetes(isChecked)
        }

        binding.switchInstruction.setOnCheckedChangeListener { _, isChecked ->
            appSettingsViewModel.saveInstruction(isChecked)
        }
    }
}