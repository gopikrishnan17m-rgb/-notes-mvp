NOTES MVP - Upload & Test Guide (தமிழ்)
=======================================

இது என்ன?
  Login + Notes ஆப் (Android, Kotlin) - Supabase backend உடன்.
  Signup / Login / குறிப்பு சேர்க்க-திருத்த-நீக்க / Tamil-English / Logout.
  ஒவ்வொரு user-உம் தன் குறிப்புகளை மட்டுமே பார்க்க முடியும் (RLS பாதுகாப்பு).

Backend (நான் ஏற்கனவே செய்தது)
  Supabase project: notes-mvp   |   Table: notes   |   RLS: ON
  Security advisor: எந்த எச்சரிக்கையும் இல்லை.

படி 1: Supabase-ல் "Confirm email" OFF செய்யுங்கள் (சோதனைக்கு மட்டும்)
  Supabase Dashboard -> Authentication -> Sign In / Providers -> Email
  -> "Confirm email" ஐ OFF -> Save.
  (இதை நான் செய்ய முடியாது. OFF இல்லையென்றால் signup பிறகு email confirm செய்ய வேண்டும்.)

படி 2: GitHub-ல் புதிய PRIVATE repo உருவாக்குங்கள் (பெயர்: notes-mvp)

படி 3: இந்த zip-ஐ extract செய்து, உள்ளே உள்ள எல்லா files/folders-ஐயும்
  repo-வின் ROOT-ல் upload செய்யுங்கள் (".github" folder உட்பட!).
  settings.gradle repo-வின் மேல் மட்டத்தில் தெரிய வேண்டும்.

படி 4: Actions tab -> "Build APK and AAB" தானாக ஓடும் (சுமார் 4-6 நிமிடம்).
  பச்சை ✔ வந்தால்: Releases -> "latest" -> NotesMVP.apk பதிவிறக்குங்கள்.
  சிவப்பு ✖ வந்தால்: அந்த log-ன் கடைசி 40 வரிகளை எனக்கு அனுப்புங்கள், சரி செய்கிறேன்.

படி 5: Phone-ல் install செய்து சோதியுங்கள்
  [ ] Signup (புதிய email + 6+ எழுத்து password)
  [ ] குறிப்பு சேர்த்தல் (+)
  [ ] குறிப்பைத் தொட்டு திருத்துதல்
  [ ] நீக்குதல்
  [ ] ஆப்பை மூடி மீண்டும் திறந்தால் login தேவையில்லாமல் குறிப்புகள் வருகிறதா
  [ ] Logout -> வேறு email-ல் signup -> முதல் user-ன் குறிப்புகள் தெரியக்கூடாது (முக்கியம்!)
  [ ] தமிழ் / English மாற்றம்
  [ ] Internet off செய்து முயற்சி -> பிழை செய்தி வருகிறதா

கவனிக்க:
  * இந்தக் code-ஐ இங்கே compile செய்து சோதிக்க முடியவில்லை (sandbox-ல் Android SDK இல்லை).
    அதனால் முதல் build-ல் சிறிய பிழை வரலாம் - log அனுப்பினால் உடனே சரி செய்வேன்.
  * KEYSTORE secrets சேர்க்காவிட்டால் debug key-ல் sign ஆகும் (சோதனைக்கு போதும்).
    Client-க்கு கொடுக்கும் முன் உங்கள் release key secrets-ஐ சேர்க்கவும்.
