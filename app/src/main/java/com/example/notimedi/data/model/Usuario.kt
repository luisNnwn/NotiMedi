package com.example.notimedi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario (
    @PrimaryKey val username: String,
    val password: String,
    val nombreCompleto: String? = null,
    val pin: String? = null
)