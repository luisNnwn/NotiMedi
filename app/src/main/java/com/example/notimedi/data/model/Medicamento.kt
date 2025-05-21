package com.example.notimedi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamentos")
data class Medicamento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val perfilId: Int,
    val nombre: String,
    val dosisMg: Int,
    val via: String,
    val hora: String,
    val unidad: String,
    val dias: String,
    val frecuenciaHoras: Int,
    val username: String
)
