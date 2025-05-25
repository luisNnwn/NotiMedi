package com.example.notimedi.repositorio

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// MODELO DE DATOS

data class DrugResult(
    val indications_and_usage: List<String>?,
    val warnings: List<String>?,
    val adverse_reactions: List<String>?
)

data class DrugResponse(val results: List<DrugResult>)

// API INTERFACE

interface OpenFdaApi {
    @GET("drug/label.json")
    suspend fun searchDrug(@Query("search") search: String): DrugResponse
}

// CLIENTE
object RetrofitClient {
    private const val BASE_URL = "https://api.fda.gov/"

    val openFdaApi: OpenFdaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFdaApi::class.java)
    }
}

// REPOSITORIO FINAL

object FdaRepository {

    private val traduccionesEntrada = mapOf(
        "paracetamol" to "acetaminophen",
        "ibuprofeno" to "ibuprofen",
        "amoxicilina" to "amoxicillin",
        "omeprazol" to "omeprazole",
        "naproxeno" to "naproxen",
        "azitromicina" to "azithromycin",
        "diclofenaco" to "diclofenac",
        "metformina" to "metformin",
        "loratadina" to "loratadine",
        "ranitidina" to "ranitidine",
        "simvastatina" to "simvastatin",
        "atorvastatina" to "atorvastatin",
        "clorfenamina" to "chlorpheniramine",
        "enalapril" to "enalapril",
        "losartan" to "losartan",
        "salbutamol" to "albuterol",
        "insulina" to "insulin",
        "furosemida" to "furosemide",
        "cetirizina" to "cetirizine",
        "omeprazol" to "omeprazole",
        "morfina" to "morphine",
        "codeina" to "codeine",
        "clonazepam" to "clonazepam",
        "alprazolam" to "alprazolam",
        "diazepam" to "diazepam",
        "prednisona" to "prednisone",
        "dexametasona" to "dexamethasone",
        "fluoxetina" to "fluoxetine",
        "sertralina" to "sertraline",
        "amoxicilina-clavulanico" to "amoxicillin clavulanate",
        "aciclovir" to "acyclovir",
        "ketoconazol" to "ketoconazole",
        "nimesulida" to "nimesulide",
        "butilhioscina" to "hyoscine butylbromide",
        "losartán" to "losartan",
        "omeprazol" to "omeprazole",
        "clindamicina" to "clindamycin",
        "ceftriaxona" to "ceftriaxone",
        "cefalexina" to "cephalexin",
        "captopril" to "captopril",
        "metoclopramida" to "metoclopramide",
        "domperidona" to "domperidone",
        "prazosina" to "prazosin",
        "valproato" to "valproic acid",
        "lamotrigina" to "lamotrigine",
        "carbamazepina" to "carbamazepine",
        "levetiracetam" to "levetiracetam",
        "biperideno" to "biperiden",
        "haloperidol" to "haloperidol",
        "risperidona" to "risperidone",
        "quetiapina" to "quetiapine",
        "olanzapina" to "olanzapine",
        "clozapina" to "clozapine",
        "ziprasidona" to "ziprasidone"
        // ... Podés seguir agregando
    )

    private val diccionarioMedico = mapOf(
        "headache" to "dolor de cabeza",
        "nausea" to "náuseas",
        "vomiting" to "vómitos",
        "dizziness" to "mareos",
        "rash" to "erupción cutánea",
        "fever" to "fiebre",
        "fatigue" to "fatiga",
        "pain" to "dolor",
        "itching" to "picazón",
        "diarrhea" to "diarrea",
        "constipation" to "estreñimiento",
        "insomnia" to "insomnio",
        "anxiety" to "ansiedad",
        "depression" to "depresión"
    )

    suspend fun consultarMedicamento(nombre: String): String = withContext(Dispatchers.IO) {
        try {
            val clave = nombre.lowercase().split(" ").firstOrNull()?.trim() ?: nombre
            val nombreTraducido = traduccionesEntrada[clave] ?: clave

            val response = RetrofitClient.openFdaApi
                .searchDrug("openfda.generic_name:\"$nombreTraducido\"")

            val result = response.results.firstOrNull()
                ?: return@withContext "No se encontraron resultados para \"$nombre\"."

            val texto = buildString {
                result.indications_and_usage?.firstOrNull()?.let {
                    append("🩺 ¿Para qué se usa?:\n${traducirTexto(it)}\n\n")
                }
                result.warnings?.firstOrNull()?.let {
                    append("⚠️ Advertencias:\n${traducirTexto(it)}\n\n")
                }
                result.adverse_reactions?.firstOrNull()?.let {
                    append("💥 Efectos secundarios:\n${traducirTexto(it)}")
                }
            }.ifBlank { "No se encontró información detallada para \"$nombre\"." }

            texto
        } catch (e: Exception) {
            "❌ Error al consultar el medicamento: ${e.message}"
        }
    }

    private fun traducirTexto(texto: String): String {
        var traducido = texto
        diccionarioMedico.forEach { (en, es) ->
            traducido = traducido.replace(en, es, ignoreCase = true)
        }
        return traducido
    }
}
