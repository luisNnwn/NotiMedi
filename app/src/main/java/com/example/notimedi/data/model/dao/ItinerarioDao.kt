package com.example.notimedi.data.dao

import androidx.room.*
import com.example.notimedi.data.model.Itinerario
import kotlinx.coroutines.flow.Flow

@Dao
interface ItinerarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(itinerario: Itinerario): Long

    @Query("SELECT * FROM itinerarios WHERE perfilId = :perfilId")
    suspend fun obtenerPorPerfil(perfilId: Int): List<Itinerario>

    @Delete
    suspend fun eliminar(itinerario: Itinerario)

    @Query("SELECT * FROM itinerarios WHERE username = :username")
    fun obtenerItinerariosPorUsuario(username: String): Flow<List<Itinerario>>

}
