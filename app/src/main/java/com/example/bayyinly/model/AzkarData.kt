package com.example.bayyinly.model

data class Azkar(
    val arabic: String,
    val translation: String,
    val count: Int = 33
)

object AzkarData {
    // Add, remove, or reorder entries here freely.
    val list = listOf(
        Azkar("سُبْحَانَ اللَّهِ", "Glory be to Allah", 33),
        Azkar("الْحَمْدُ لِلَّهِ", "All praise is due to Allah", 33),
        Azkar("اللَّهُ أَكْبَرُ", "Allah is the Greatest", 33),
        Azkar(
            "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            "None has the right to be worshipped but Allah alone, who has no partner. His is the dominion and His is the praise, and He is Able to do all things.",
            1
        ),
    )
}
