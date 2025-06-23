package com.kazanion

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kazanion.databinding.FragmentEditProfileBinding
import com.kazanion.models.Result
import com.kazanion.viewmodels.EditProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()
    private var startTime: Calendar? = null
    private var endTime: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSave.setOnClickListener {
            saveProfile()
        }

        binding.buttonChangePhoto.setOnClickListener {
            // TODO: Implement photo change functionality
        }

        // Setup time selection buttons
        binding.buttonSelectStartTime.setOnClickListener {
            showTimePicker(true)
        }

        binding.buttonSelectEndTime.setOnClickListener {
            showTimePicker(false)
        }

        // Load user data
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.editTextName.setText(it.displayName)
                binding.editTextEmail.setText(it.email)
                binding.editTextPhone.setText(it.username)
                binding.editTextAddress.setText("")
            }
        }
    }

    private fun observeViewModel() {
        viewModel.profileUpdateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(context, "Profil başarıyla güncellendi", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Result.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun saveProfile() {
        val displayName = binding.editTextName.text.toString()
        val email = binding.editTextEmail.text.toString()
        val username = binding.editTextPhone.text.toString()
        
        val nameParts = displayName.split(" ", limit = 2)
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.getOrNull(1) ?: ""
        
        viewModel.updateProfile(
            firstName = firstName,
            lastName = lastName,
            email = email,
            username = username
        )
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = if (isStartTime) startTime ?: Calendar.getInstance() else endTime ?: Calendar.getInstance()
        
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                if (isStartTime) {
                    startTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }
                } else {
                    endTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }
                }
                updateTimeButtons()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun updateTimeButtons() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        
        startTime?.let {
            binding.buttonSelectStartTime.text = timeFormat.format(it.time)
        }
        
        endTime?.let {
            binding.buttonSelectEndTime.text = timeFormat.format(it.time)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 