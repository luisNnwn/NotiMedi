package com.example.notimedi.util

import android.util.Base64

fun encriptarPin(pin: String): String {
    return Base64.encodeToString(pin.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
}

fun desencriptarPin(encriptado: String): String {
    return String(Base64.decode(encriptado, Base64.DEFAULT), Charsets.UTF_8)
}
