package com.notesmvp.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class GuestNotesTest {
    private var saved: String? = null
    private fun notes() = GuestNotes({ saved }, { saved = it; true }, { "2026-01-01" })

    @Test fun startsEmpty() = assertTrue(notes().list().isEmpty())

    @Test fun addKeepsNewestFirstAndSurvivesReload() {
        notes().add("first", "a")
        notes().add("second", "\u0ba4\u0bae\u0bbf\u0bb4\u0bcd \"quoted\"\nline")
        val list = notes().list()
        assertEquals(listOf("second", "first"), list.map { it.title })
        assertEquals("\u0ba4\u0bae\u0bbf\u0bb4\u0bcd \"quoted\"\nline", list[0].body)
    }

    @Test fun updateAndDeleteTouchOnlyTheChosenNote() {
        notes().add("one", "")
        notes().add("two", "")
        val one = notes().list().first { it.title == "one" }
        notes().update(one.id, "one edited", "x")
        assertEquals(listOf("two", "one edited"), notes().list().map { it.title })
        notes().delete(one.id)
        assertEquals(listOf("two"), notes().list().map { it.title })
    }

    @Test fun failedSaveIsReported() {
        val broken = GuestNotes({ null }, { false })
        assertThrows(IllegalStateException::class.java) { broken.add("t", "b") }
    }
}
