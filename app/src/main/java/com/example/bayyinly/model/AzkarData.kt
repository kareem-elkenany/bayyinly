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
        Azkar("لَا إِلَهَ إِلَّا اللَّهُ", "There is no god but Allah", 100),
        Azkar(
            "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            "None has the right to be worshipped but Allah alone, who has no partner. His is the dominion and His is the praise, and He is Able to do all things.",
            100
        ),
        Azkar(
            "سُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ وَلَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ",
            "Glory be to Allah, praise be to Allah, there is no god but Allah, and Allah is the Greatest",
            33
        ),
        Azkar("أَسْتَغْفِرُ اللَّهَ", "I seek forgiveness from Allah", 100),
        Azkar("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "Glory and praise be to Allah", 100),
        Azkar("اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", "O Allah, send blessings upon Muhammad ﷺ", 10),
        Azkar("لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ", "No power nor strength except with Allah", 33),
        Azkar(
            "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ",
            "Glory and praise be to Allah, Glory be to Allah the Magnificent",
            33
        ),
    )
}
