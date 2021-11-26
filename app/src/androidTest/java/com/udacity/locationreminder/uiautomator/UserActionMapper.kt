package com.udacity.locationreminder.uiautomator

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
