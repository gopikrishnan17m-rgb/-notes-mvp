package com.notesmvp.app

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale

object Store {
    private lateinit var sp: SharedPreferences

    fun init(c: Context) {
        sp = c.getSharedPreferences("notes_store", Context.MODE_PRIVATE)
    }

    var lang: String
        get() = sp.getString("lang", null)
            ?: if (Locale.getDefault().language == "ta") "ta" else "en"
        set(v) { sp.edit().putString("lang", v).apply() }

    var guest: Boolean
        get() = sp.getBoolean("guest", false)
        set(v) { sp.edit().putBoolean("guest", v).apply() }

    val guestNotes = GuestNotes(
        read = { sp.getString("guest_notes", null) },
        write = { sp.edit().putString("guest_notes", it).commit() }
    )

    fun session(): Session? {
        val at = sp.getString("at", null) ?: return null
        val rt = sp.getString("rt", null) ?: return null
        return Session(at, rt, sp.getLong("exp", 0L), sp.getString("em", "") ?: "")
    }

    fun save(s: Session) {
        sp.edit().putBoolean("guest", false).putString("at", s.accessToken).putString("rt", s.refreshToken)
            .putLong("exp", s.expiresAt).putString("em", s.email).apply()
    }

    fun clear() {
        sp.edit().remove("at").remove("rt").remove("exp").remove("em").apply()
    }

    fun token(): String = session()?.accessToken ?: throw ApiException("no session", 401)
}
