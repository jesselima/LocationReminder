package com.udacity.locationreminder.e2e

enum class UserAction {
    HOME, BACK, LEFT, RIGHT, UP, DOWN, CENTER, MENU, SEARCH, ENTER, DELETE, DEL, RECENT,
    VOLUME_UP, VOLUME_DOWN, VOLUME_MUTE, CAMERA;
    fun asKey(): String = this.name.lowercase()
}