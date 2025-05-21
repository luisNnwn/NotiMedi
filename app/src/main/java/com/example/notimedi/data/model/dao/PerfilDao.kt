package com.example.notimedi.data.dao

import androidx.room.*
import com.example.notimedi.data.model.Perfil
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(perfil: Perfil): Long

    @Query("SELECT * FROM perfiles")
    fun obtenerTodos(): Flow<List<Perfil>>

    @Query("SELECT * FROM perfiles WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Perfil?

    @Query("SELECT * FROM perfiles WHERE username = :username")
    fun obtenerPorUsuario(username: String): Flow<List<Perfil>>

    @Delete
    suspend fun eliminar(perfil: Perfil)

    @Update
    suspend fun actualizar(perfil: Perfil)
}
