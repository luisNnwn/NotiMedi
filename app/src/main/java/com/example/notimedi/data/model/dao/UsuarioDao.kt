package com.example.notimedi.data.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notimedi.data.model.Usuario

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun getUsuarioPorNombre(username: String): Usuario?

    @Query("UPDATE usuarios SET pin = :nuevoPin WHERE username = :username")
    suspend fun actualizarPin(username: String, nuevoPin: String)

    @Query("UPDATE usuarios SET password = :nuevaPassword WHERE username = :username")
    suspend fun actualizarPassword(username: String, nuevaPassword: String)
}