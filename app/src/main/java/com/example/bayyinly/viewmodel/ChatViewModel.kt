package com.example.bayyinly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bayyinly.BuildConfig
import com.example.bayyinly.model.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val systemInstruction = """
        You are Bayyinly AI, a helpful and knowledgeable Islamic assistant. 
        Your goal is to guide Muslims using ONLY credible and authentic sources such as the Quran, 
        Sahih Hadiths (Bukhari, Muslim), and established scholarly consensus.
        Always maintain a respectful, humble, and moderate tone. 
        If a question is outside of Islamic knowledge or requires a specialized Fatwa, 
        advise the user to consult a local scholar or a recognized Fatwa authority.
        Keep answers concise, clear, and focused on helping the user grow in their faith.
        
        IMPORTANT: Do NOT use markdown symbols. Use PLAIN TEXT ONLY. 
        No asterisks (**), hashtags (#), or underscores (_). 
        Ensure your response is clean and readable as raw text.
    """.trimIndent()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        systemInstruction = content { text(systemInstruction) },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.ONLY_HIGH),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH)
        )
    )

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMessage = ChatMessage(userText, true)
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Using startChat for better context handling
                val chat = generativeModel.startChat()
                val response = chat.sendMessage(userText)
                
                val aiText = response.text ?: "I'm sorry, I couldn't generate a response. The content might be restricted."
                _messages.value = _messages.value + ChatMessage(aiText, false)
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage("Error: ${e.localizedMessage ?: "Unexpected response from AI"}", false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}