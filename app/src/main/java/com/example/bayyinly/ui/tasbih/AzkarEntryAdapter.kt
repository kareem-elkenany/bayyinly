package com.example.bayyinly.ui.tasbih

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bayyinly.databinding.ItemAzkarEntryBinding
import com.example.bayyinly.model.Azkar

class AzkarEntryAdapter(
    private val items: List<Azkar>,
    private val accentColor: String
) : RecyclerView.Adapter<AzkarEntryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAzkarEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(azkar: Azkar, position: Int) {
            val badge = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(accentColor))
            }
            binding.tvAzkarNumber.background = badge
            binding.tvAzkarNumber.text = (position + 1).toString()

            binding.tvAzkarArabicEntry.text = azkar.arabic

            if (azkar.transliteration.isNotEmpty()) {
                binding.tvAzkarTranslit.text = azkar.transliteration
                binding.tvAzkarTranslit.visibility = View.VISIBLE
            } else {
                binding.tvAzkarTranslit.visibility = View.GONE
            }

            binding.tvAzkarTranslationEntry.text = azkar.translation

            if (azkar.source.isNotEmpty()) {
                binding.tvAzkarSource.text = azkar.source
                binding.tvAzkarSource.visibility = View.VISIBLE
            } else {
                binding.tvAzkarSource.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAzkarEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position], position)

    override fun getItemCount() = items.size
}