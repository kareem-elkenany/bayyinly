package com.example.bayyinly.ui.dua

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bayyinly.databinding.ItemDuaBinding
import com.example.bayyinly.model.entity.Dua

class DuaAdapter : RecyclerView.Adapter<DuaAdapter.DuaViewHolder>() {

    private var duas: List<Dua> = emptyList()

    class DuaViewHolder(val binding: ItemDuaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuaViewHolder {
        val binding = ItemDuaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DuaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DuaViewHolder, position: Int) {
        val dua = duas[position]
        holder.binding.tvDuaCategory.text = dua.category
        holder.binding.tvDuaTitle.text = dua.title
        holder.binding.tvArabicDua.text = dua.arabicText
        holder.binding.tvTransliteration.text = dua.transliteration
        holder.binding.tvTranslation.text = dua.translation
        holder.binding.tvSource.text = dua.source
        holder.binding.root.contentDescription =
            "${dua.category}. ${dua.title}. ${dua.translation}. Source: ${dua.source}"
    }

    override fun getItemCount(): Int = duas.size

    fun submitList(updatedDuas: List<Dua>) {
        duas = updatedDuas
        notifyDataSetChanged()
    }
}
