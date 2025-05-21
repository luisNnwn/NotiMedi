package com.example.notimedi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alergias")
data class Alergia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val perfilId: Int,
    val nombre: String,
    val username: String
)

