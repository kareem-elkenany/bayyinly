package com.example.bayyinly.ui.quran

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bayyinly.databinding.ItemSurahBinding
import com.example.bayyinly.model.SurahItem

class SurahAdapter(
    private var surahs: List<SurahItem>,
    private val onSurahClick: (Int) -> Unit
) : RecyclerView.Adapter<SurahAdapter.SurahViewHolder>() {

    // 1. The ViewHolder now takes the generated ItemSurahBinding
    class SurahViewHolder(val binding: ItemSurahBinding) : RecyclerView.ViewHolder(binding.root)

    // 2. Inflate the layout using the Binding class
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
        val binding = ItemSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SurahViewHolder(binding)
    }

    // 3. Bind data directly to the views via the binding object
    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        val surah = surahs[position]

        // No more findViewById! Just use holder.binding
        holder.binding.tvSurahNumber.text = surah.id.toString()
        holder.binding.tvEnglishName.text = surah.englishName
        holder.binding.tvArabicName.text = surah.arabicName

        // Handle clicks on the root layout of the item
        holder.binding.root.setOnClickListener {
            onSurahClick(surah.id)
        }
    }

    override fun getItemCount(): Int {
        return surahs.size
    }

    fun updateList(filteredList: List<SurahItem>) {
        surahs = filteredList
        notifyDataSetChanged()
    }
}