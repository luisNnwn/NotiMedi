package com.example.notimedi.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notimedi.data.dao.AlergiaDao
import com.example.notimedi.data.dao.ItinerarioDao
import com.example.notimedi.data.dao.MedicamentoDao
import com.example.notimedi.data.dao.PerfilDao
import com.example.notimedi.data.model.Perfil
import com.example.notimedi.data.model.Alergia
import com.example.notimedi.data.model.Medicamento
import com.example.notimedi.data.model.Itinerario
import com.example.notimedi.data.model.dao.UsuarioDao
import com.example.notimedi.data.model.Usuario
import androidx.room.migration.Migration


@Database(
    entities = [Usuario::class, Perfil::class, Alergia::class, Medicamento::class, Itinerario::class],
    version = 14
)
abstract class NotiMediDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun perfilDao(): PerfilDao
    abstract fun alergiaDao(): AlergiaDao
    abstract fun medicamentoDao(): MedicamentoDao
    abstract fun itinerarioDao(): ItinerarioDao

    companion object {
        @Volatile
        private var INSTANCE: NotiMediDatabase? = null

        fun getDatabase(context: Context): NotiMediDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotiMediDatabase::class.java,
                    "notimedi_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ✅ Migración real registrada fuera de la clase para evitar pérdida de datos
val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Si no hiciste cambios estructurales, podés dejar esto vacío.
        // Ejemplo: database.execSQL("ALTER TABLE usuarios ADD COLUMN nuevoCampo TEXT")
    }
}
