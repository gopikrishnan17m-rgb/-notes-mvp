package com.notesmvp.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private val io = Executors.newSingleThreadExecutor()
    private lateinit var root: FrameLayout
    private var signupMode = false
    private var notesBox: LinearLayout? = null
    private var statusTv: TextView? = null

    private val cBg = Color.parseColor("#0F1626")
    private val cCard = Color.parseColor("#18223A")
    private val cField = Color.parseColor("#223052")
    private val cAccent = Color.parseColor("#4C8DFF")
    private val cText = Color.parseColor("#EAF0FF")
    private val cMuted = Color.parseColor("#8FA0C4")
    private val cError = Color.parseColor("#FF6B6B")

    // ---------------------------------------------------------------- strings
    private val strings = mapOf(
        "en" to mapOf(
            "app" to "Notes",
            "tagline" to "Your private notes, synced to the cloud",
            "login" to "Log in",
            "signup" to "Create account",
            "email" to "Email",
            "password" to "Password (min 6 characters)",
            "to_signup" to "New here? Create an account",
            "to_login" to "Already have an account? Log in",
            "my_notes" to "My notes",
            "empty" to "No notes yet.\nTap + to add your first note.",
            "loading" to "Loading…",
            "new_note" to "New note",
            "edit_note" to "Edit note",
            "title" to "Title",
            "body" to "Write something…",
            "save" to "Save",
            "cancel" to "Cancel",
            "delete" to "Delete",
            "del_q" to "Delete this note permanently?",
            "logout" to "Log out",
            "refresh" to "Refresh",
            "lang_btn" to "தமிழ்",
            "need_fields" to "Enter email and password.",
            "need_title" to "Title is required.",
            "confirm_mail" to "Account created. Check your email to confirm, then log in.",
            "saved" to "Saved",
            "deleted" to "Deleted",
            "err_network" to "No internet connection. Please try again.",
            "err_creds" to "Wrong email or password.",
            "err_exists" to "This email is already registered. Try logging in.",
            "err_weak" to "Password is too weak. Use at least 6 characters.",
            "err_email" to "Please enter a valid email address.",
            "err_unconfirmed" to "Email not confirmed yet. Check your inbox.",
            "err_rate" to "Too many attempts. Please wait a minute.",
            "err_generic" to "Something went wrong"
        ),
        "ta" to mapOf(
            "app" to "குறிப்புகள்",
            "tagline" to "உங்கள் தனிப்பட்ட குறிப்புகள், கிளவுடில் சேமிக்கப்படும்",
            "login" to "உள்நுழை",
            "signup" to "கணக்கு உருவாக்கு",
            "email" to "மின்னஞ்சல்",
            "password" to "கடவுச்சொல் (குறைந்தது 6 எழுத்துகள்)",
            "to_signup" to "புதியவரா? கணக்கு உருவாக்குங்கள்",
            "to_login" to "ஏற்கனவே கணக்கு உள்ளதா? உள்நுழையுங்கள்",
            "my_notes" to "என் குறிப்புகள்",
            "empty" to "இன்னும் குறிப்புகள் இல்லை.\nமுதல் குறிப்பைச் சேர்க்க + அழுத்தவும்.",
            "loading" to "ஏற்றுகிறது…",
            "new_note" to "புதிய குறிப்பு",
            "edit_note" to "குறிப்பைத் திருத்து",
            "title" to "தலைப்பு",
            "body" to "ஏதாவது எழுதுங்கள்…",
            "save" to "சேமி",
            "cancel" to "ரத்து",
            "delete" to "நீக்கு",
            "del_q" to "இந்தக் குறிப்பை நிரந்தரமாக நீக்கவா?",
            "logout" to "வெளியேறு",
            "refresh" to "புதுப்பி",
            "lang_btn" to "English",
            "need_fields" to "மின்னஞ்சலும் கடவுச்சொல்லும் உள்ளிடவும்.",
            "need_title" to "தலைப்பு அவசியம்.",
            "confirm_mail" to "கணக்கு உருவானது. மின்னஞ்சலை உறுதிப்படுத்தி, பிறகு உள்நுழையுங்கள்.",
            "saved" to "சேமிக்கப்பட்டது",
            "deleted" to "நீக்கப்பட்டது",
            "err_network" to "இணைய இணைப்பு இல்லை. மீண்டும் முயற்சிக்கவும்.",
            "err_creds" to "மின்னஞ்சல் அல்லது கடவுச்சொல் தவறு.",
            "err_exists" to "இந்த மின்னஞ்சல் ஏற்கனவே பதிவு செய்யப்பட்டுள்ளது. உள்நுழையுங்கள்.",
            "err_weak" to "கடவுச்சொல் பலவீனமாக உள்ளது. குறைந்தது 6 எழுத்துகள் தேவை.",
            "err_email" to "சரியான மின்னஞ்சலை உள்ளிடவும்.",
            "err_unconfirmed" to "மின்னஞ்சல் இன்னும் உறுதிப்படுத்தப்படவில்லை.",
            "err_rate" to "அதிக முயற்சிகள். ஒரு நிமிடம் காத்திருக்கவும்.",
            "err_generic" to "ஏதோ தவறு நடந்தது"
        )
    )

    private fun t(k: String): String =
        strings[Store.lang]?.get(k) ?: strings["en"]?.get(k) ?: k

    private fun errText(e: ApiException): String {
        val m = e.message ?: ""
        val l = m.lowercase()
        return when {
            m == "NETWORK" -> t("err_network")
            l.contains("invalid login") -> t("err_creds")
            l.contains("already registered") || l.contains("already been registered") -> t("err_exists")
            l.contains("email not confirmed") -> t("err_unconfirmed")
            l.contains("rate limit") || l.contains("too many") -> t("err_rate")
            l.contains("password") && (l.contains("least") || l.contains("short") || l.contains("weak")) -> t("err_weak")
            l.contains("email") && (l.contains("invalid") || l.contains("valid")) -> t("err_email")
            else -> t("err_generic") + (if (m.isNotEmpty()) ": $m" else "")
        }
    }

    // ---------------------------------------------------------------- helpers
    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun rounded(color: Int, radiusDp: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp).toFloat()
        }

    private fun tv(text: String, size: Float, color: Int, bold: Boolean = false): TextView =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
        }

    private fun button(text: String, filled: Boolean, onClick: () -> Unit): TextView =
        tv(text, 16f, if (filled) Color.WHITE else cAccent, true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = if (filled) rounded(cAccent, 14) else null
            setOnClickListener { onClick() }
        }

    private fun chip(text: String, onClick: () -> Unit): TextView =
        tv(text, 13f, cText).apply {
            setPadding(dp(14), dp(8), dp(14), dp(8))
            background = rounded(cField, 20)
            setOnClickListener { onClick() }
        }

    private fun field(hint: String, multi: Boolean = false): EditText =
        EditText(this).apply {
            this.hint = hint
            setHintTextColor(Color.parseColor("#6F7C9A"))
            setTextColor(Color.WHITE)
            textSize = 16f
            background = rounded(cField, 12)
            setPadding(dp(14), dp(12), dp(14), dp(12))
            if (multi) {
                inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                minLines = 5
                gravity = Gravity.TOP or Gravity.START
            }
        }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val v = currentFocus ?: return
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun ui(block: () -> Unit) {
        runOnUiThread { if (!isFinishing && !isDestroyed) block() }
    }

    // ---------------------------------------------------------------- lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Store.init(applicationContext)
        root = FrameLayout(this).apply { setBackgroundColor(cBg) }
        setContentView(root)
        if (Store.session() != null) showNotes() else showAuth()
    }

    override fun onDestroy() {
        super.onDestroy()
        io.shutdown()
    }

    // ---------------------------------------------------------------- auth screen
    private fun showAuth() {
        notesBox = null
        statusTv = null
        root.removeAllViews()

        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(40), dp(24), dp(24))
        }
        val scroll = ScrollView(this).apply {
            isFillViewport = true
            addView(col)
        }

        val langRow = LinearLayout(this).apply { gravity = Gravity.END }
        langRow.addView(chip(t("lang_btn")) {
            Store.lang = if (Store.lang == "ta") "en" else "ta"
            showAuth()
        })
        col.addView(langRow)

        col.addView(tv(t("app"), 34f, cText, true).apply { setPadding(0, dp(40), 0, dp(6)) })
        col.addView(tv(t("tagline"), 15f, cMuted).apply { setPadding(0, 0, 0, dp(28)) })

        val email = field(t("email")).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        val pass = field(t("password")).apply {
            // Do not call setSingleLine() here: it breaks the password transformation.
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        col.addView(email, LinearLayout.LayoutParams(-1, -2))
        col.addView(pass, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(12) })

        val err = tv("", 14f, cError).apply {
            setPadding(0, dp(12), 0, 0)
            visibility = View.GONE
        }
        col.addView(err)

        val main = button(if (signupMode) t("signup") else t("login"), true) {}
        main.setOnClickListener {
            val e = email.text.toString().trim()
            val p = pass.text.toString()
            if (e.isEmpty() || p.isEmpty()) {
                err.text = t("need_fields")
                err.setTextColor(cError)
                err.visibility = View.VISIBLE
                return@setOnClickListener
            }
            hideKeyboard()
            err.visibility = View.GONE
            main.isEnabled = false
            main.alpha = 0.6f
            val wasSignup = signupMode
            io.execute {
                try {
                    val s = if (wasSignup) Api.signUp(e, p) else Api.login(e, p)
                    if (s != null) {
                        Store.save(s)
                        ui { showNotes() }
                    } else {
                        ui {
                            signupMode = false
                            showAuth()
                            toast(t("confirm_mail"))
                        }
                    }
                } catch (ex: ApiException) {
                    ui {
                        main.isEnabled = true
                        main.alpha = 1f
                        err.text = errText(ex)
                        err.setTextColor(cError)
                        err.visibility = View.VISIBLE
                    }
                } catch (ex: Exception) {
                    ui {
                        main.isEnabled = true
                        main.alpha = 1f
                        err.text = errText(ApiException(ex.message ?: "", -1))
                        err.visibility = View.VISIBLE
                    }
                }
            }
        }
        col.addView(main, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(20) })

        col.addView(
            button(if (signupMode) t("to_login") else t("to_signup"), false) {
                signupMode = !signupMode
                showAuth()
            },
            LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(8) }
        )

        root.addView(scroll, FrameLayout.LayoutParams(-1, -1))
    }

    // ---------------------------------------------------------------- notes screen
    private fun showNotes() {
        root.removeAllViews()

        val column = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(8))
        }
        header.addView(tv(t("my_notes"), 28f, cText, true))
        header.addView(tv(Store.session()?.email ?: "", 13f, cMuted).apply {
            setPadding(0, dp(2), 0, dp(12))
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
        })
        val chips = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val gap = LinearLayout.LayoutParams(-2, -2).apply { rightMargin = dp(8) }
        chips.addView(chip("⟳ " + t("refresh")) { loadNotes() }, gap)
        chips.addView(chip(t("lang_btn")) {
            Store.lang = if (Store.lang == "ta") "en" else "ta"
            showNotes()
        }, gap)
        chips.addView(chip(t("logout")) { doLogout() }, gap)
        header.addView(chips)
        column.addView(header, LinearLayout.LayoutParams(-1, -2))

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(110))
        }
        val status = tv(t("loading"), 15f, cMuted).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(48), 0, 0)
        }
        box.addView(status, LinearLayout.LayoutParams(-1, -2))
        notesBox = box
        statusTv = status

        val scroll = ScrollView(this).apply { addView(box) }
        column.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(column, FrameLayout.LayoutParams(-1, -1))

        val fab = tv("+", 32f, Color.WHITE, true).apply {
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(cAccent)
            }
            elevation = dp(8).toFloat()
            setOnClickListener { openEditor(null) }
        }
        root.addView(
            fab,
            FrameLayout.LayoutParams(dp(60), dp(60), Gravity.BOTTOM or Gravity.END).apply {
                setMargins(0, 0, dp(22), dp(26))
            }
        )

        loadNotes()
    }

    private fun loadNotes() {
        statusTv?.let {
            it.text = t("loading")
            it.visibility = View.VISIBLE
        }
        call({ Api.listNotes(Store.token()) }, { render(it) })
    }

    private fun render(list: List<Note>) {
        val box = notesBox ?: return
        val status = statusTv
        box.removeAllViews()
        if (status != null) {
            status.text = t("empty")
            box.addView(status, LinearLayout.LayoutParams(-1, -2))
            status.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        list.forEachIndexed { i, n -> box.addView(card(n, i), cardParams()) }
    }

    private fun cardParams() =
        LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(10) }

    private fun card(n: Note, index: Int): View {
        val c = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(cCard, 16)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            setOnClickListener { openEditor(n) }
        }
        c.addView(tv(n.title, 17f, cText, true).apply {
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        })
        if (n.body.isNotEmpty()) {
            c.addView(tv(n.body, 14f, cMuted).apply {
                maxLines = 3
                ellipsize = TextUtils.TruncateAt.END
                setPadding(0, dp(6), 0, 0)
            })
        }
        c.addView(tv(n.updatedAt.take(10), 11f, Color.parseColor("#5F6F93")).apply {
            setPadding(0, dp(8), 0, 0)
        })
        c.alpha = 0f
        c.translationY = dp(16).toFloat()
        c.animate().alpha(1f).translationY(0f).setDuration(220)
            .setStartDelay((index * 40L).coerceAtMost(400L)).start()
        return c
    }

    // ---------------------------------------------------------------- editor
    private fun openEditor(n: Note?) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), 0)
        }
        val title = field(t("title")).apply {
            filters = arrayOf(InputFilter.LengthFilter(120))
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }
        val body = field(t("body"), true).apply {
            filters = arrayOf(InputFilter.LengthFilter(5000))
        }
        if (n != null) {
            title.setText(n.title)
            body.setText(n.body)
        }
        box.addView(title, LinearLayout.LayoutParams(-1, -2))
        box.addView(body, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(12) })
        val sv = ScrollView(this).apply { addView(box) }

        val b = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle(if (n == null) t("new_note") else t("edit_note"))
            .setView(sv)
            .setPositiveButton(t("save"), null)
            .setNegativeButton(t("cancel"), null)
        if (n != null) b.setNeutralButton(t("delete"), null)
        val d = b.create()
        d.window?.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
        d.show()

        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val ti = title.text.toString().trim()
            val bo = body.text.toString()
            if (ti.isEmpty()) {
                title.error = t("need_title")
                return@setOnClickListener
            }
            d.dismiss()
            if (n == null) {
                call({ Api.addNote(Store.token(), ti, bo) }, {
                    toast(t("saved"))
                    loadNotes()
                })
            } else {
                call({ Api.updateNote(Store.token(), n.id, ti, bo) }, {
                    toast(t("saved"))
                    loadNotes()
                })
            }
        }
        if (n != null) {
            d.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                    .setMessage(t("del_q"))
                    .setPositiveButton(t("delete")) { _, _ ->
                        d.dismiss()
                        call({ Api.deleteNote(Store.token(), n.id) }, {
                            toast(t("deleted"))
                            loadNotes()
                        })
                    }
                    .setNegativeButton(t("cancel"), null)
                    .show()
            }
        }
    }

    // ---------------------------------------------------------------- session + network
    private fun doLogout() {
        val tok = Store.session()?.accessToken
        Store.clear()
        if (tok != null) io.execute { Api.logout(tok) }
        showAuth()
    }

    /**
     * Runs [work] on a background thread. Refreshes the access token when it is about to
     * expire, retries once on 401, and sends the user back to the login screen if the
     * session cannot be recovered.
     */
    private fun <T> call(
        work: () -> T,
        ok: (T) -> Unit,
        fail: (ApiException) -> Unit = { e ->
            statusTv?.let { it.text = errText(e) }
            toast(errText(e))
        }
    ) {
        io.execute {
            try {
                val r = withAuth(work)
                ui { ok(r) }
            } catch (e: ApiException) {
                ui {
                    if (e.code == 401) {
                        Store.clear()
                        showAuth()
                    } else {
                        fail(e)
                    }
                }
            } catch (e: Exception) {
                ui { fail(ApiException(e.message ?: "", -1)) }
            }
        }
    }

    private fun <T> withAuth(work: () -> T): T {
        val s = Store.session() ?: throw ApiException("no session", 401)
        if (s.expiresAt - System.currentTimeMillis() < 60_000L) refreshSession()
        try {
            return work()
        } catch (e: ApiException) {
            if (e.code != 401) throw e
            refreshSession()
            return work()
        }
    }

    private fun refreshSession() {
        val s = Store.session() ?: throw ApiException("no session", 401)
        try {
            Store.save(Api.refresh(s.refreshToken, s.email))
        } catch (e: ApiException) {
            // Network problems are passed through; any server-side rejection means re-login.
            if (e.message == "NETWORK") throw e
            throw ApiException("session expired", 401)
        }
    }
}
