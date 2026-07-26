// ==== YANGI QO'SHILGAN KOD BOSHLANISHI ====
// Kiril harflarini rus ovozida o'qish sozlamasi (standart: o'chiq)
SharedPreferences customPrefs = PreferenceManager.getDefaultSharedPreferences(this);
boolean cyrillicToRussianEnabled = customPrefs.getBoolean("custom_cyrillic_to_russian", false);

if (cyrillicToRussianEnabled && !"rus".equals(bestMatch.voice.getLanguage())) {
    AndroidVoiceInfo russianVoice = null;
    for (AndroidVoiceInfo v : tts.voices) {
        if ("rus".equals(v.getSource().getLanguage().getAlpha3Code())) {
            russianVoice = v;
            break;
        }
    }
    if (russianVoice != null) {
        boolean alreadyAdded = false;
        for (Map.Entry<String, LanguageSettings> entry : languageSettings.entrySet()) {
            if (entry.getKey().equals("rus") && entry.getValue().detect && entry.getValue().voice != null) {
                alreadyAdded = true;
                break;
            }
        }
        if (!alreadyAdded) {
            voiceProfileSpecBuilder.append("+").append(russianVoice.getSource().getName());
        }
    } else if (BuildConfig.DEBUG) {
        Log.w(TAG, "Cyrillic-to-Russian yoqilgan, lekin rus ovozi o'rnatilmagan");
    }
}
// ==== YANGI QO'SHILGAN KOD OXIRI ====
