package com.example.bayyinly.ui.tasbih

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bayyinly.databinding.ItemAzkarEntryBinding
import com.example.bayyinly.model.entity.AzkarEntry

class AzkarEntryAdapter(
    private val items: List<AzkarEntry>,
    private val accentColor: String
) : RecyclerView.Adapter<AzkarEntryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAzkarEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: AzkarEntry, position: Int) {
            binding.tvAzkarNumber.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(accentColor))
            }
            binding.tvAzkarNumber.text = (position + 1).toString()

            binding.tvAzkarArabicEntry.text = entry.zekr

            binding.tvAzkarTranslit.visibility = View.GONE

            if (!entry.description.isNullOrEmpty()) {
                binding.tvAzkarTranslationEntry.text = entry.description
                binding.tvAzkarTranslationEntry.visibility = View.VISIBLE
            } else {
                binding.tvAzkarTranslationEntry.visibility = View.GONE
            }

            if (!entry.reference.isNullOrEmpty()) {
                binding.tvAzkarSource.text = entry.reference
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], position)

    override fun getItemCount() = items.size
}
