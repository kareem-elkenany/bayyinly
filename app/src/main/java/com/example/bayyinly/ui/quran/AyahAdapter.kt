package com.example.bayyinly.ui.quran

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bayyinly.databinding.ItemAyahBinding // Ensure this matches your XML name!
import com.example.bayyinly.model.CombinedVerse

class AyahAdapter(private val onAyahClick: () -> Unit) :
    ListAdapter<CombinedVerse, AyahAdapter.AyahViewHolder>(AyahDiffCallback()) {

    // 1. MUST BE AT THE TOP OF THE CLASS: The variable to track the active ID
    private var currentPlayingId: Int? = null

    // 2. MUST BE AT THE TOP OF THE CLASS: The function to update the ID
    fun setActiveVerse(verseId: Int?) {
        currentPlayingId = verseId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyahViewHolder {
        val binding = ItemAyahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AyahViewHolder(binding)
    }

    // 3. COLOR LOGIC MUST BE HERE: Inside onBindViewHolder
    override fun onBindViewHolder(holder: AyahViewHolder, position: Int) {
        // We define currentAyah right here so the code knows what it is!
        val currentAyah = getItem(position)

        // Pass the data to the layout
        holder.bind(currentAyah)

        // Highlight logic
        if (currentAyah.id == currentPlayingId) {
            // Active Ayah = Light Green Background
            holder.itemView.setBackgroundColor(Color.parseColor("#E8F5E9"))
        } else {
            // Inactive Ayahs = Transparent Background
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    // 4. YOUR VIEWHOLDER STAYS AT THE BOTTOM
    inner class AyahViewHolder(private val binding: ItemAyahBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onAyahClick() }
        }

        fun bind(ayah: CombinedVerse) {
            // Example: Put the Arabic text into the Arabic TextView
            binding.tvArabicText.text = ayah.arabicText

            // Example: Put the English translation into the Translation TextView
            binding.tvEnglishText.text = ayah.englishText

            // Example: Put the Ayah number in the Number TextView
            binding.tvAyahNumber.text = ayah.ayah.toString()
        }
    }
}

// 5. DIFF CALLBACK STAYS AT THE VERY BOTTOM
class AyahDiffCallback : DiffUtil.ItemCallback<CombinedVerse>() {
    override fun areItemsTheSame(oldItem: CombinedVerse, newItem: CombinedVerse): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: CombinedVerse, newItem: CombinedVerse): Boolean {
        return oldItem == newItem
    }
}