package com.example.notimedi.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfiles")
data class Perfil(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val sexo: String,
    val fechaNacimiento: String,
    val esPrincipal: Boolean = false,
    @ColumnInfo(name = "username")
    val username: String
)
