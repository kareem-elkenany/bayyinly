package com.example.bayyinly.model

enum class AzkarCategory(val title: String, val subtitle: String) {
    MORNING("Morning Azkar", "أذكار الصباح"),
    EVENING("Evening Azkar", "أذكار المساء"),
    AFTER_PRAYER("After Prayer", "أذكار بعد الصلاة"),
    SLEEP("Before Sleep", "أذكار النوم"),
    GENERAL("General Dhikr", "الأذكار العامة")
}

data class Azkar(
    val arabic: String,
    val translation: String,
    val count: Int = 1,
    val transliteration: String = "",
    val source: String = "",
    val category: AzkarCategory = AzkarCategory.GENERAL
)

data class AzkarCategoryGroup(
    val category: AzkarCategory,
    val items: List<Azkar>
)

object AzkarData {

    // Rotating list used by TasbihViewModel
    val tasbihList = listOf(
        Azkar("سُبْحَانَ اللَّهِ", "Glory be to Allah", 33, "Subhaan Allah", "Muslim 597", AzkarCategory.AFTER_PRAYER),
        Azkar("الْحَمْدُ لِلَّهِ", "All praise is due to Allah", 33, "Alhamdulillah", "Muslim 597", AzkarCategory.AFTER_PRAYER),
        Azkar("اللَّهُ أَكْبَرُ", "Allah is the Greatest", 33, "Allahu Akbar", "Muslim 597", AzkarCategory.AFTER_PRAYER),
        Azkar("لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قديرُ", "There is no god but Allah alone, without partner. To Him belongs all sovereignty and praise, and He is over all things omnipotent.", 1, "Laa ilaaha illallaahu wahdahu laa shareeka lah, lahul-mulku wa lahul-hamdu wa huwa 'alaa kulli shay'in qadeer", "Muslim 597", AzkarCategory.AFTER_PRAYER),
        Azkar("لَا إِلَهَ إِلَّا اللَّهُ", "There is no god but Allah", 100, "Laa ilaaha illallah", "Bukhari 6405", AzkarCategory.GENERAL),
        Azkar("أَسْتَغْفِرُ اللَّهَ", "I seek forgiveness from Allah", 100, "Astaghfirullah", "Bukhari 6307", AzkarCategory.GENERAL),
        Azkar("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "Glory and praise be to Allah", 100 , "Subhaan Allahi wa bihamdihi", "Bukhari 6042", AzkarCategory.MORNING),
        Azkar("اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", "O Allah, send blessings upon Muhammad ﷺ", 10, "Allahumma salli ala Muhammad", "Tirmidhi 484", AzkarCategory.GENERAL),
        Azkar("لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ", "No power nor strength except with Allah", 33, "Laa hawla wa laa quwwata illaa billaah", "", AzkarCategory.GENERAL),
        Azkar("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ", "Glory and praise be to Allah; Glory be to Allah the Magnificent", 33, "Subhaan Allahi wa bihamdihi, subhaan Allahil-Adheem", "Bukhari 6042", AzkarCategory.GENERAL),
    )

    val categories: List<AzkarCategoryGroup> = listOf(

        // ─── Morning Azkar ────────────────────────────────────────────────────
        AzkarCategoryGroup(
            category = AzkarCategory.MORNING,
            items = listOf(
                Azkar(
                    arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                    translation = "We have reached the morning and at this very time all sovereignty belongs to Allah. All praise is for Allah. None has the right to be worshipped but Allah, alone, without partner. To Him belongs all sovereignty and praise, and He is over all things omnipotent.",
                    count = 1, source = "Abu Dawud 5078", category = AzkarCategory.MORNING
                ),
                Azkar(
                    arabic = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ",
                    translation = "O Allah, by You we enter the morning and by You we enter the evening, by You we live and by You we die, and to You is the resurrection.",
                    count = 1, source = "Abu Dawud 5068", category = AzkarCategory.MORNING
                ),
                Azkar(
                    arabic = "اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لَا إِلَهَ إِلَّا أَنْتَ",
                    translation = "O Allah, grant me health in my body. O Allah, grant me health in my hearing. O Allah, grant me health in my sight. None has the right to be worshipped but You.",
                    count = 3, source = "Abu Dawud 5090", category = AzkarCategory.MORNING
                ),
                Azkar(
                    arabic = " رَضِيتُ بِاللَّهِ رَبًّا، وَبِالإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا و رسولًا",
                    translation = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad ﷺ as my Prophet.",
                    count = 3, transliteration = "Radeetu billaahi rabban wa bil-Islaami deenan wa bi-Muhammadin nabiyyan", source = "Abu Dawud 5072", category = AzkarCategory.MORNING
                ),
                Azkar(
                    arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                    translation = "Glory and praise be to Allah.",
                    count = 100, transliteration = "Subhaan Allahi wa bihamdihi", source = "Bukhari 6042", category = AzkarCategory.MORNING
                ),
                Azkar(
                    arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                    translation = "None has the right to be worshipped but Allah alone, without partner. To Him belongs all sovereignty and praise, and He is over all things omnipotent.",
                    count = 10, transliteration = "Laa ilaaha illallahu wahdahu laa shareeka lah", source = "Bukhari 3293", category = AzkarCategory.MORNING
                ),
                Azkar(
                    arabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
                    translation = "I seek refuge in the perfect words of Allah from the evil of what He has created.",
                    count = 3, transliteration = "A'oodhu bi kalimaatillahit-taammaati min sharri maa khalaq", source = "Muslim 2709", category = AzkarCategory.MORNING
                ),
            )
        ),

        // ─── Evening Azkar ────────────────────────────────────────────────────
        AzkarCategoryGroup(
            category = AzkarCategory.EVENING,
            items = listOf(
                Azkar(
                    arabic = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                    translation = "We have reached the evening and at this very time all sovereignty belongs to Allah. All praise is for Allah. None has the right to be worshipped but Allah, alone, without partner. To Him belongs all sovereignty and praise, and He is over all things omnipotent.",
                    count = 1, source = "Abu Dawud 5078", category = AzkarCategory.EVENING
                ),
                Azkar(
                    arabic = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ",
                    translation = "O Allah, by You we enter the evening and by You we enter the morning, by You we live and by You we die, and to You is the final return.",
                    count = 1, source = "Abu Dawud 5068", category = AzkarCategory.EVENING
                ),
                Azkar(
                    arabic = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
                    translation = "Allah is sufficient for me. None has the right to be worshipped but Him. I have put my trust in Him. He is the Lord of the Mighty Throne.",
                    count = 7, transliteration = "Hasbiyallaahu laa ilaaha illaa huwa 'alayhi tawakkaltu wa huwa rabbul-'arshil-'adheem", source = "Abu Dawud 5081", category = AzkarCategory.EVENING
                ),
                Azkar(
                    arabic = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا و رسولًا",
                    translation = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad ﷺ as my Prophet.",
                    count = 3, transliteration = "Radeetu billaahi rabban wa bil-Islaami deenan wa bi-Muhammadin nabiyyan", source = "Abu Dawud 5072", category = AzkarCategory.EVENING
                ),
                Azkar(
                    arabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
                    translation = "I seek refuge in the perfect words of Allah from the evil of what He has created.",
                    count = 3, transliteration = "A'oodhu bi kalimaatillahit-taammaati min sharri maa khalaq", source = "Muslim 2709", category = AzkarCategory.EVENING
                ),
                Azkar(
                    arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ",
                    translation = "O Allah, I ask You for pardon and well-being in this world and the Hereafter.",
                    count = 1, source = "Abu Dawud 5074", category = AzkarCategory.EVENING
                ),
                Azkar(
                    arabic = "اللَّهُمَّ فَاطِرَ السَّمَاوَاتِ وَالْأَرْضِ، عَالِمَ الْغَيْبِ وَالشَّهَادَةِ، أَنْتَ رَبُّ كُلِّ شَيْءٍ وَمَلِيكُهُ",
                    translation = "O Allah, Originator of the heavens and the earth, Knower of the unseen and the seen, Lord of everything and its Owner.",
                    count = 1, source = "Muslim 2710", category = AzkarCategory.EVENING
                ),
            )
        ),

        // ─── After Prayer Azkar ───────────────────────────────────────────────
        AzkarCategoryGroup(
            category = AzkarCategory.AFTER_PRAYER,
            items = listOf(
                Azkar(
                    arabic = "أَسْتَغْفِرُ اللَّهَ",
                    translation = "I seek forgiveness from Allah.",
                    count = 3, transliteration = "Astaghfirullah", source = "Muslim 591", category = AzkarCategory.AFTER_PRAYER
                ),
                Azkar(
                    arabic = "اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
                    translation = "O Allah, You are As-Salam and from You is all peace. Blessed are You, O Owner of majesty and honor.",
                    count = 1, source = "Muslim 591", category = AzkarCategory.AFTER_PRAYER
                ),
                Azkar(
                    arabic = "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ، لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ، لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ",
                    translation = "Allah — none has the right to be worshipped but He, the Ever-Living, the Sustainer of existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. (Ayat al-Kursi — Al-Baqarah 2:255)",
                    count = 1, source = "Nasai (authentic)", category = AzkarCategory.AFTER_PRAYER
                ),
                Azkar(
                    arabic = "سُبْحَانَ اللَّهِ",
                    translation = "Glory be to Allah.",
                    count = 33, transliteration = "Subhaan Allah", source = "Muslim 597", category = AzkarCategory.AFTER_PRAYER
                ),
                Azkar(
                    arabic = "الْحَمْدُ لِلَّهِ",
                    translation = "All praise is due to Allah.",
                    count = 33, transliteration = "Alhamdulillah", source = "Muslim 597", category = AzkarCategory.AFTER_PRAYER
                ),
                Azkar(
                    arabic = "اللَّهُ أَكْبَرُ",
                    translation = "Allah is the Greatest.",
                    count = 34, transliteration = "Allahu Akbar", source = "Muslim 597", category = AzkarCategory.AFTER_PRAYER
                ),
                Azkar(
                    arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                    translation = "None has the right to be worshipped but Allah alone, without partner. To Him belongs all sovereignty and praise, and He is over all things omnipotent.",
                    count = 1, source = "Muslim 597", category = AzkarCategory.AFTER_PRAYER
                ),
                Azkar(
                    arabic = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ",
                    translation = "O Allah, help me to remember You, to be grateful to You, and to worship You in an excellent manner.",
                    count = 1, transliteration = "Allahumma a'inni 'alaa dhikrika wa shukrika wa husni 'ibaadatik", source = "Abu Dawud 1522", category = AzkarCategory.AFTER_PRAYER
                ),
                Azkar(
                    arabic = "اللَّهُمَّ لَا مَانِعَ لِمَا أَعْطَيْتَ، وَلَا مُعْطِيَ لِمَا مَنَعْتَ، وَلَا يَنْفَعُ ذَا الْجَدِّ مِنْكَ الْجَدُّ",
                    translation = "O Allah, none can withhold what You give, and none can give what You withhold, and wealth cannot benefit its owner against Your will.",
                    count = 1, source = "Bukhari 844", category = AzkarCategory.AFTER_PRAYER
                ),
            )
        ),

        // ─── Before Sleep Azkar ───────────────────────────────────────────────
        AzkarCategoryGroup(
            category = AzkarCategory.SLEEP,
            items = listOf(
                Azkar(
                    arabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                    translation = "In Your name, O Allah, I die and I live.",
                    count = 1, transliteration = "Bismika Allahumma amootu wa ahyaa", source = "Bukhari 6312", category = AzkarCategory.SLEEP
                ),
                Azkar(
                    arabic = "بِسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
                    translation = "In Your name, my Lord, I lay down my side, and by You I raise it. If You take my soul, then have mercy on it, and if You release it, then protect it with what You protect Your righteous servants.",
                    count = 1, source = "Bukhari 6320", category = AzkarCategory.SLEEP
                ),
                Azkar(
                    arabic = "اللَّهُمَّ أَسْلَمْتُ نَفْسِي إِلَيْكَ، وَوَجَّهْتُ وَجْهِي إِلَيْكَ، وَفَوَّضْتُ أَمْرِي إِلَيْكَ، وَأَلْجَأْتُ ظَهْرِي إِلَيْكَ، رَغْبَةً وَرَهْبَةً إِلَيْكَ، لَا مَلْجَأَ وَلَا مَنْجَا مِنْكَ إِلَّا إِلَيْكَ",
                    translation = "O Allah, I have submitted myself to You, turned my face to You, entrusted my affairs to You, and laid my back upon You — in hope and fear of You. There is no refuge or escape from You except to You.",
                    count = 1, source = "Bukhari 247", category = AzkarCategory.SLEEP
                ),
                Azkar(
                    arabic = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
                    translation = "O Allah, protect me from Your punishment on the Day You resurrect Your servants.",
                    count = 3, source = "Abu Dawud 5045", category = AzkarCategory.SLEEP
                ),
                Azkar(
                    arabic = "سُبْحَانَ اللَّهِ",
                    translation = "Glory be to Allah.",
                    count = 33, transliteration = "Subhaan Allah", source = "Bukhari 3113", category = AzkarCategory.SLEEP
                ),
                Azkar(
                    arabic = "الْحَمْدُ لِلَّهِ",
                    translation = "All praise is due to Allah.",
                    count = 33, transliteration = "Alhamdulillah", source = "Bukhari 3113", category = AzkarCategory.SLEEP
                ),
                Azkar(
                    arabic = "اللَّهُ أَكْبَرُ",
                    translation = "Allah is the Greatest.",
                    count = 34, transliteration = "Allahu Akbar", source = "Bukhari 3113", category = AzkarCategory.SLEEP
                ),
            )
        ),

        // ─── General Dhikr ────────────────────────────────────────────────────
        AzkarCategoryGroup(
            category = AzkarCategory.GENERAL,
            items = listOf(
                Azkar(
                    arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ",
                    translation = "Glory and praise be to Allah; Glory be to Allah the Magnificent.",
                    count = 100, transliteration = "Subhaan Allahi wa bihamdihi, subhaan Allahil-Adheem", source = "Bukhari 6042", category = AzkarCategory.GENERAL
                ),
                Azkar(
                    arabic = "لَا إِلَهَ إِلَّا اللَّهُ",
                    translation = "There is no god but Allah.",
                    count = 100, transliteration = "Laa ilaaha illallah", source = "Bukhari 6405", category = AzkarCategory.GENERAL
                ),
                Azkar(
                    arabic = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
                    translation = "I seek forgiveness from Allah and repent to Him.",
                    count = 100, transliteration = "Astaghfirullaha wa atoobu ilayh", source = "Bukhari 6307", category = AzkarCategory.GENERAL
                ),
                Azkar(
                    arabic = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
                    translation = "O Allah, send blessings and peace upon our Prophet Muhammad ﷺ.",
                    count = 10, transliteration = "Allahumma salli wa sallim 'alaa nabiyyina Muhammad", source = "Tirmidhi 484", category = AzkarCategory.GENERAL
                ),
                Azkar(
                    arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
                    translation = "There is no power nor strength except with Allah.",
                    count = 100, transliteration = "Laa hawla wa laa quwwata illaa billaah", category = AzkarCategory.GENERAL
                ),
            )
        ),
    )
}