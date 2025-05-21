package com.example.notimedi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itinerarios")
data class Itinerario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val perfilId: Int,
    val medicamentoId: Int,
    val hora: String,
    val dias: String, // ej: "Lun,Mié,Vie"
    val username: String
)
