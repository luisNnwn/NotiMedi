package com.example.notimedi.data.dao

import androidx.room.*
import com.example.notimedi.data.model.Alergia
import kotlinx.coroutines.flow.Flow

@Dao
interface AlergiaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(alergia: Alergia)

    @Query("SELECT * FROM alergias WHERE perfilId = :perfilId")
    suspend fun obtenerPorPerfil(perfilId: Int): List<Alergia>

    @Delete
    suspend fun eliminar(alergia: Alergia)

    @Query("DELETE FROM alergias WHERE perfilId = :perfilId")
    suspend fun eliminarPorPerfil(perfilId: Int)

    @Query("SELECT * FROM alergias WHERE username = :username")
    fun obtenerAlergiasPorUsuario(username: String): Flow<List<Alergia>>

    @Query("SELECT * FROM alergias WHERE perfilId = :perfilId AND username = :username")
    fun obtenerAlergiasDePerfil(perfilId: Int, username: String): Flow<List<Alergia>>
}
