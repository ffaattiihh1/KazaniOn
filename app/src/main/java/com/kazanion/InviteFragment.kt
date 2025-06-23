package com.kazanion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kazanion.databinding.FragmentInviteBinding
import com.kazanion.viewmodels.InviteViewModel

class InviteFragment : Fragment() {
    private var _binding: FragmentInviteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InviteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteBinding.inflate(inflater, container, false)
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

        binding.textInviteLink.setOnClickListener {
            // Copy to clipboard
            viewModel.copyInviteLinkToClipboard(requireContext())
        }

        binding.buttonShare.setOnClickListener {
            shareInviteLink()
        }
    }

    private fun observeViewModel() {
        viewModel.inviteLink.observe(viewLifecycleOwner) { link ->
            binding.textInviteLink.text = link
        }
    }

    private fun shareInviteLink() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, binding.textInviteLink.text.toString())
        }
        startActivity(Intent.createChooser(shareIntent, "Davet Linkini Paylaş"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 