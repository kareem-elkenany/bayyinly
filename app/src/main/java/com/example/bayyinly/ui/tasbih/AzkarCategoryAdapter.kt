package com.example.bayyinly.ui.tasbih

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bayyinly.databinding.ItemAzkarCategoryBinding

data class AzkarCategoryConfig(
    val dbCategory: String,
    val title: String,
    val iconRes: Int,
    val colorHex: String,
    val itemCount: Int = 0
)

class AzkarCategoryAdapter(
    private val items: List<AzkarCategoryConfig>,
    private val onClick: (AzkarCategoryConfig) -> Unit
) : RecyclerView.Adapter<AzkarCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAzkarCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(config: AzkarCategoryConfig) {
            binding.tvCategoryTitle.text = config.title
            binding.tvCategorySubtitle.text = "${config.itemCount} azkar"
            binding.ivCategoryIcon.setImageResource(config.iconRes)
            binding.ivCategoryIcon.setColorFilter(Color.parseColor(config.colorHex))
            binding.root.setOnClickListener { onClick(config) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAzkarCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size
}
