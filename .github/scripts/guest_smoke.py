"""Taps through guest mode on a running emulator. Any missing screen fails the build."""
import glob, os, re, subprocess, sys, time
import xml.etree.ElementTree as ET

PKG = "com.notesmvp.app"
os.makedirs("screenshots", exist_ok=True)


def adb(*args, check=True):
    return subprocess.run(["adb", *args], check=check, capture_output=True).stdout


def nodes():
    adb("shell", "uiautomator", "dump", "/sdcard/ui.xml", check=False)
    xml = adb("shell", "cat", "/sdcard/ui.xml", check=False).decode("utf-8", "replace")
    try:
        return list(ET.fromstring(xml).iter("node"))
    except ET.ParseError:
        return []


def find(text, last=False):
    hits = [n for n in nodes() if n.get("text", "").lower() == text.lower()]
    return (hits[-1] if last else hits[0]) if hits else None


def shot(name):
    with open(f"screenshots/{name}.png", "wb") as f:
        f.write(adb("exec-out", "screencap", "-p"))


def wait(text, seconds=40):
    end = time.time() + seconds
    while time.time() < end:
        n = find(text)
        if n is not None:
            return n
        time.sleep(1)
    shot("failure")
    print("On screen:", [n.get("text") for n in nodes() if n.get("text")])
    sys.exit(f"FAIL: '{text}' not found")


def gone(text, seconds=20):
    end = time.time() + seconds
    while time.time() < end:
        current = nodes()
        if current and not any(n.get("text", "") == text for n in current):
            return
        time.sleep(1)
    shot("failure")
    sys.exit(f"FAIL: '{text}' still on screen")


def tap(text, last=False):
    wait(text)
    n = find(text, last)
    x1, y1, x2, y2 = map(int, re.findall(r"\d+", n.get("bounds")))
    adb("shell", "input", "tap", str((x1 + x2) // 2), str((y1 + y2) // 2))
    time.sleep(1.5)


def launch():
    adb("shell", "am", "start", "-W", "-n", f"{PKG}/.MainActivity")


def restart():
    adb("shell", "am", "force-stop", PKG)
    launch()


def step(msg):
    print("PASS:", msg, flush=True)


adb("install", glob.glob("apk/*.apk")[0])
launch()
wait("Continue as guest"); shot("1-auth"); step("login screen shows the guest button")
tap("New here? Create an account")
wait("Already have an account? Log in"); wait("Continue as guest"); step("sign-up screen shows the guest button")
tap("Continue as guest")
wait("Guest mode: notes are saved only on this phone"); shot("2-guest-empty"); step("guest mode opens without an email")
tap("+"); tap("Title"); adb("shell", "input", "text", "GuestNote1"); time.sleep(1); tap("Save")
wait("GuestNote1"); shot("3-added"); step("add note")
restart(); wait("GuestNote1"); wait("Guest mode: notes are saved only on this phone"); step("guest mode and note survive an app restart")
tap("GuestNote1"); tap("GuestNote1", last=True); adb("shell", "input", "keyevent", "123")
adb("shell", "input", "text", "Edited"); time.sleep(1); tap("Save")
wait("GuestNote1Edited"); shot("4-edited"); step("edit note")
tap("Log in / Sign up"); wait("Continue as guest"); tap("Continue as guest"); wait("GuestNote1Edited"); step("guest notes kept after visiting the login screen")
tap("GuestNote1Edited"); tap("Delete"); wait("Delete this note permanently?"); tap("Delete", last=True)
gone("GuestNote1Edited"); restart(); wait("Guest mode: notes are saved only on this phone"); gone("GuestNote1Edited"); shot("5-deleted"); step("delete note")
crashes = adb("logcat", "-d", "-b", "crash").decode("utf-8", "replace")
if PKG in crashes:
    print(crashes); sys.exit("FAIL: crash log mentions the app")
step("no app crash in the crash log")
