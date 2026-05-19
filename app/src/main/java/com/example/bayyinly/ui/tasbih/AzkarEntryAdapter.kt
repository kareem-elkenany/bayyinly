package com.example.bayyinly.ui.tasbih

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.HapticFeedbackConstants
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

    // Store the remaining count for each item in this session
    private val itemCounts = mutableMapOf<Int, Int>()

    inner class ViewHolder(private val binding: ItemAzkarEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: AzkarEntry, position: Int) {
            val id = entry.id ?: position
            val total = if (entry.count != null && entry.count!! > 0) entry.count!! else 1
            
            // Initialize count if not already tracked in this session
            if (!itemCounts.containsKey(id)) {
                itemCounts[id] = total
            }
            
            val remaining = itemCounts[id] ?: total
            val themeColor = try { Color.parseColor(accentColor) } catch (e: Exception) { Color.parseColor("#1B5E20") }

            // 1. Number Badge (Circle)
            binding.tvAzkarNumber.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(themeColor)
            }
            binding.tvAzkarNumber.text = (position + 1).toString()

            // 2. Arabic Text
            binding.tvAzkarArabicEntry.text = entry.zekr ?: ""

            // 3. Repeat Count Badge (Highly Visible)
            updateCountUi(remaining, themeColor)

            // 4. Tap to count logic (Tasbih feel)
            binding.root.setOnClickListener {
                val current = itemCounts[id] ?: total
                if (current > 0) {
                    val next = current - 1
                    itemCounts[id] = next
                    updateCountUi(next, themeColor)
                    it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            }

            // 5. Description / Translation
            if (!entry.description.isNullOrEmpty()) {
                binding.tvAzkarTranslationEntry.text = entry.description
                binding.tvAzkarTranslationEntry.visibility = View.VISIBLE
            } else {
                binding.tvAzkarTranslationEntry.visibility = View.GONE
            }

            // 6. Source / Reference
            if (!entry.reference.isNullOrEmpty()) {
                binding.tvAzkarSource.text = entry.reference
                binding.tvAzkarSource.visibility = View.VISIBLE
            } else {
                binding.tvAzkarSource.visibility = View.GONE
            }
        }

        private fun updateCountUi(count: Int, color: Int) {
            if (count > 0) {
                binding.tvAzkarRepeatCount.text = "${count}x"
                binding.tvAzkarRepeatCount.setTextColor(color)
                binding.tvAzkarRepeatCount.background = GradientDrawable().apply {
                    cornerRadius = 32f
                    setColor(color)
                    alpha = 60 // semi-opaque tint for visibility
                }
            } else {
                binding.tvAzkarRepeatCount.text = "✓"
                binding.tvAzkarRepeatCount.setTextColor(Color.WHITE)
                binding.tvAzkarRepeatCount.background = GradientDrawable().apply {
                    cornerRadius = 32f
                    setColor(Color.parseColor("#2E7D32")) // Success Green
                }
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
