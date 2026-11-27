package com.notesmvp.app

import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Notes for guest mode. They stay on this phone and are never sent to the server.
 * Storage is passed in as two functions so the logic can be unit tested.
 */
class GuestNotes(
    private val read: () -> String?,
    private val write: (String) -> Boolean,
    private val today: () -> String = { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
) {
    private fun load(): JSONArray = JSONArray(read() ?: "[]")

    private fun store(a: JSONArray) {
        if (!write(a.toString())) throw IllegalStateException("Could not save on this phone")
    }

    private fun item(id: String, title: String, body: String) = JSONObject()
        .put("id", id).put("title", title).put("body", body).put("updated_at", today())

    /** Newest note first. */
    fun list(): List<Note> {
        val a = load()
        return (0 until a.length()).map {
            val o = a.getJSONObject(it)
            Note(o.getString("id"), o.getString("title"), o.optString("body", ""), o.optString("updated_at", ""))
        }
    }

    fun add(title: String, body: String) {
        val old = load()
        val out = JSONArray().put(item(UUID.randomUUID().toString(), title, body))
        for (i in 0 until old.length()) out.put(old.getJSONObject(i))
        store(out)
    }

    fun update(id: String, title: String, body: String) {
        val a = load()
        for (i in 0 until a.length()) {
            if (a.getJSONObject(i).getString("id") == id) a.put(i, item(id, title, body))
        }
        store(a)
    }

    fun delete(id: String) {
        val a = load()
        val out = JSONArray()
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            if (o.getString("id") != id) out.put(o)
        }
        store(out)
    }
}
