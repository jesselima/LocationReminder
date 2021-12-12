package com.udacity.locationreminder.e2e

import android.view.KeyEvent
import androidx.test.uiautomator.UiDevice

fun UiDevice.mapKeyToAction(key: String): Boolean = when (key.lowercase()) {
    UserAction.HOME.asKey() -> pressHome()
    UserAction.BACK.asKey() -> pressBack()
    UserAction.LEFT.asKey() -> pressDPadLeft()
    UserAction.RIGHT.asKey() -> pressDPadRight()
    UserAction.UP.asKey() -> pressDPadUp()
    UserAction.DOWN.asKey() -> pressDPadDown()
    UserAction.CENTER.asKey() -> pressDPadCenter()
    UserAction.MENU.asKey() -> pressMenu()
    UserAction.SEARCH.asKey() -> pressSearch()
    UserAction.ENTER.asKey() -> pressEnter()
    UserAction.DELETE.asKey(), UserAction.DEL.asKey() -> pressDelete()
    UserAction.RECENT.asKey() -> pressRecentApps()
    UserAction.VOLUME_UP.asKey() -> pressKeyCode(KeyEvent.KEYCODE_VOLUME_UP)
    UserAction.VOLUME_DOWN.asKey() -> pressKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN)
    UserAction.VOLUME_MUTE.asKey() -> pressKeyCode(KeyEvent.KEYCODE_VOLUME_MUTE)
    UserAction.CAMERA.asKey() -> pressKeyCode(KeyEvent.KEYCODE_CAMERA)
    else ->  pressKeyCode(KeyEvent.KEYCODE_POWER)
}

fun UiDevice.searchAppName(appName: String) {
    for (letter in appName) {
        mapTextToKeyEvents(letter.toString())
    }
}

fun UiDevice.mapTextToKeyEvents(text: String) {
    when(text.uppercase()) {
        "A" -> pressKeyCode(KeyEvent.KEYCODE_A)
        "B" -> pressKeyCode(KeyEvent.KEYCODE_B)
        "C" -> pressKeyCode(KeyEvent.KEYCODE_C)
        "D" -> pressKeyCode(KeyEvent.KEYCODE_D)
        "E" -> pressKeyCode(KeyEvent.KEYCODE_E)
        "F" -> pressKeyCode(KeyEvent.KEYCODE_F)
        "G" -> pressKeyCode(KeyEvent.KEYCODE_G)
        "H" -> pressKeyCode(KeyEvent.KEYCODE_H)
        "I" -> pressKeyCode(KeyEvent.KEYCODE_I)
        "J" -> pressKeyCode(KeyEvent.KEYCODE_J)
        "K" -> pressKeyCode(KeyEvent.KEYCODE_K)
        "L" -> pressKeyCode(KeyEvent.KEYCODE_L)
        "M" -> pressKeyCode(KeyEvent.KEYCODE_M)
        "N" -> pressKeyCode(KeyEvent.KEYCODE_N)
        "O" -> pressKeyCode(KeyEvent.KEYCODE_O)
        "P" -> pressKeyCode(KeyEvent.KEYCODE_P)
        "Q" -> pressKeyCode(KeyEvent.KEYCODE_Q)
        "R" -> pressKeyCode(KeyEvent.KEYCODE_R)
        "S" -> pressKeyCode(KeyEvent.KEYCODE_S)
        "T" -> pressKeyCode(KeyEvent.KEYCODE_T)
        "U" -> pressKeyCode(KeyEvent.KEYCODE_U)
        "V" -> pressKeyCode(KeyEvent.KEYCODE_V)
        "W" -> pressKeyCode(KeyEvent.KEYCODE_W)
        "X" -> pressKeyCode(KeyEvent.KEYCODE_X)
        "Y" -> pressKeyCode(KeyEvent.KEYCODE_Y)
        "Z" -> pressKeyCode(KeyEvent.KEYCODE_Z)
    }
}