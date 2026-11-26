package com.notesmvp.app

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class Note(val id: String, val title: String, val body: String, val updatedAt: String)
data class Session(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val email: String
)

class ApiException(message: String, val code: Int) : Exception(message)

object Api {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    private fun builder(path: String, token: String?): Request.Builder {
        val b = Request.Builder()
            .url(Config.SUPABASE_URL + path)
            .header("apikey", Config.PUBLISHABLE_KEY)
        // Only a real user JWT goes into Authorization. The publishable key is never sent as Bearer.
        if (token != null) b.header("Authorization", "Bearer $token")
        return b
    }

    private fun body(o: JSONObject) = o.toString().toRequestBody(JSON)

    private fun send(req: Request): String {
        try {
            client.newCall(req).execute().use { r ->
                val txt = r.body?.string() ?: ""
                if (!r.isSuccessful) throw ApiException(errorText(txt, r.message), r.code)
                return txt
            }
        } catch (e: IOException) {
            throw ApiException("NETWORK", 0)
        }
    }

    private fun errorText(txt: String, fallback: String): String {
        return try {
            val o = JSONObject(txt)
            listOf("error_description", "msg", "message", "error")
                .map { o.optString(it, "") }
                .firstOrNull { it.isNotEmpty() } ?: fallback
        } catch (e: Exception) {
            fallback
        }
    }

    private fun parseSession(o: JSONObject, fallbackEmail: String): Session {
        val exp = System.currentTimeMillis() + o.optLong("expires_in", 3600L) * 1000L
        val email = o.optJSONObject("user")?.optString("email", fallbackEmail) ?: fallbackEmail
        return Session(o.getString("access_token"), o.getString("refresh_token"), exp, email)
    }

    // ---------- Auth ----------
    fun login(email: String, password: String): Session {
        val req = builder("/auth/v1/token?grant_type=password", null)
            .post(body(JSONObject().put("email", email).put("password", password))).build()
        return parseSession(JSONObject(send(req)), email)
    }

    /** Returns null when the project requires e-mail confirmation (no session yet). */
    fun signUp(email: String, password: String): Session? {
        val req = builder("/auth/v1/signup", null)
            .post(body(JSONObject().put("email", email).put("password", password))).build()
        val o = JSONObject(send(req))
        return if (o.has("access_token")) parseSession(o, email) else null
    }

    fun refresh(refreshToken: String, email: String): Session {
        val req = builder("/auth/v1/token?grant_type=refresh_token", null)
            .post(body(JSONObject().put("refresh_token", refreshToken))).build()
        return parseSession(JSONObject(send(req)), email)
    }

    fun logout(token: String) {
        try {
            val req = builder("/auth/v1/logout", token)
                .post("".toRequestBody(JSON)).build()
            send(req)
        } catch (e: Exception) {
            // best effort
        }
    }

    // ---------- Notes ----------
    fun listNotes(token: String): List<Note> {
        val req = builder(
            "/rest/v1/notes?select=id,title,body,updated_at&order=created_at.desc", token
        ).get().build()
        val arr = JSONArray(send(req))
        val out = ArrayList<Note>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(
                Note(
                    o.getString("id"),
                    o.optString("title", ""),
                    o.optString("body", ""),
                    o.optString("updated_at", "")
                )
            )
        }
        return out
    }

    fun addNote(token: String, title: String, text: String) {
        val req = builder("/rest/v1/notes", token)
            .header("Prefer", "return=minimal")
            .post(body(JSONObject().put("title", title).put("body", text))).build()
        send(req)
    }

    fun updateNote(token: String, id: String, title: String, text: String) {
        val req = builder("/rest/v1/notes?id=eq.$id", token)
            .header("Prefer", "return=minimal")
            .patch(body(JSONObject().put("title", title).put("body", text))).build()
        send(req)
    }

    fun deleteNote(token: String, id: String) {
        val req = builder("/rest/v1/notes?id=eq.$id", token).delete().build()
        send(req)
    }
}
