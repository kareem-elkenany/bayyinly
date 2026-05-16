package com.example.bayyinly.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bayyinly.databinding.FragmentChatBinding
import com.example.bayyinly.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private val chatAdapter = ChatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupInput()
        observeViewModel()
        setupInsets()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            // Combined System Bars (Nav) and IME (Keyboard)
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())

            binding.btnBack.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + (16 * resources.displayMetrics.density).toInt()
            }

            // This pushes the input layout up exactly by the keyboard height
            binding.layoutInput.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom
            }

            windowInsets
        }
    }

    private fun setupRecyclerView() {
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
            
            // Auto-scroll when keyboard appears
            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    val itemCount = chatAdapter.itemCount
                    if (itemCount > 0) {
                        scrollToPosition(itemCount - 1)
                    }
                }
            }
        }
    }

    private fun setupInput() {
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.sendMessage(text)
                binding.etMessage.setText("")
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messages.collectLatest { messages ->
                if (messages.isNotEmpty()) {
                    chatAdapter.setMessages(messages)
                    binding.rvChat.scrollToPosition(messages.size - 1)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.btnSend.isEnabled = !isLoading
                binding.etMessage.isEnabled = !isLoading
                if (isLoading) {
                    binding.etMessage.hint = "AI is thinking..."
                } else {
                    binding.etMessage.hint = "Ask anything about Islam..."
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}