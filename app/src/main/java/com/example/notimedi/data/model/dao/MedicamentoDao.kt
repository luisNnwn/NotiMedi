package com.example.notimedi.data.dao

import androidx.room.*
import com.example.notimedi.data.model.Medicamento
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicamentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(medicamento: Medicamento): Long

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Medicamento?

    @Query("SELECT * FROM medicamentos")
    suspend fun obtenerTodos(): List<Medicamento>

    @Query("SELECT * FROM medicamentos WHERE perfilId = :perfilId")
    suspend fun obtenerPorPerfil(perfilId: Int): List<Medicamento>

    @Delete
    suspend fun eliminar(medicamento: Medicamento)

    @Update
    suspend fun actualizar(medicamento: Medicamento)

    @Query("SELECT * FROM medicamentos WHERE username = :username")
    fun obtenerMedicamentosPorUsuario(username: String): Flow<List<Medicamento>>

    @Query("SELECT * FROM medicamentos WHERE perfilId = :perfilId AND username = :username")
    fun obtenerMedicamentosDePerfil(perfilId: Int, username: String): Flow<List<Medicamento>>

    @Query("SELECT * FROM medicamentos WHERE perfilId = :perfilId AND username = :username")
    fun obtenerPorPerfilYUsuario(perfilId: Int, username: String): Flow<List<Medicamento>>
}
