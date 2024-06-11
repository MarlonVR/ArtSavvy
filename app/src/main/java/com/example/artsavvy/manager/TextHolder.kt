package com.example.artsavvy.manager

object TextHolder {
    var currentText: String = ""

    fun updateText(newText: String) {
        currentText = newText
    }

    fun clearText() {
        currentText = ""
    }

    fun getText(): String = currentText
}
