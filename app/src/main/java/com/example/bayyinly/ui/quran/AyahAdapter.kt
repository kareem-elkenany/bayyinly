package com.example.bayyinly.ui.quran

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bayyinly.databinding.ItemAyahBinding // Make sure this matches your XML file name
import com.example.bayyinly.model.CombinedVerse

class AyahAdapter : RecyclerView.Adapter<AyahAdapter.AyahViewHolder>() {

    // We start with an empty list, and the ViewModel will fill it later
    private var verses: List<CombinedVerse> = emptyList()

    class AyahViewHolder(val binding: ItemAyahBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyahViewHolder {
        val binding = ItemAyahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AyahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AyahViewHolder, position: Int) {
        val verse = verses[position]

        // IMPORTANT: Change these to match the actual IDs in your item_ayah.xml!
        holder.binding.tvAyahNumber.text = verse.ayah.toString()
        holder.binding.tvArabicText.text = verse.arabicText
        holder.binding.tvEnglishText.text = verse.englishText
    }

    override fun getItemCount(): Int {
        return verses.size
    }

    // A special function to update the list when the database finishes loading
    fun submitList(newVerses: List<CombinedVerse>) {
        verses = newVerses
        notifyDataSetChanged() // Tells the RecyclerView to redraw the screen with new data
    }
}