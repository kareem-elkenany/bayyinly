package com.example.bayyinly.ui.tasbih

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bayyinly.R
import com.example.bayyinly.databinding.ItemAzkarCategoryBinding
import com.example.bayyinly.model.AzkarCategory
import com.example.bayyinly.model.AzkarCategoryGroup

class AzkarCategoryAdapter(
    private val items: List<AzkarCategoryGroup>,
    private val onClick: (AzkarCategoryGroup) -> Unit
) : RecyclerView.Adapter<AzkarCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAzkarCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: AzkarCategoryGroup) {
            binding.tvCategoryTitle.text = group.category.title
            binding.tvCategorySubtitle.text = "${group.items.size} azkar"

            val (iconRes, colorHex) = iconAndColor(group.category)
            binding.ivCategoryIcon.setImageResource(iconRes)
            binding.ivCategoryIcon.setColorFilter(Color.parseColor(colorHex))

            binding.root.setOnClickListener { onClick(group) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAzkarCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    private fun iconAndColor(category: AzkarCategory): Pair<Int, String> = when (category) {
        AzkarCategory.MORNING    -> R.drawable.ic_mosque  to "#F59E0B"
        AzkarCategory.EVENING    -> R.drawable.ic_mosque  to "#6366F1"
        AzkarCategory.AFTER_PRAYER -> R.drawable.ic_tasbih to "#79AE6F"
        AzkarCategory.SLEEP      -> R.drawable.ic_mosque  to "#8B5CF6"
        AzkarCategory.GENERAL    -> R.drawable.ic_dua     to "#059669"
    }
}