package com.example.notimedi

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiHelper {
    suspend fun query(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val finalPrompt = """
                Saluda al usuario y menciona para qué sirve el medicamento "$prompt".
                Si está mal escrito pero entendible, acepta la consulta. Si hay ambigüedad, pide que se repita.
                Explica para qué sirve y cuáles son los efectos secundarios.
                No recomiendes dosis ni fomentes la automedicación.
                Incluye un emoji de pastilla al final.
            """.trimIndent()

            val model = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = BuildConfig.GEMINI_API_KEY
            )

            val response = model.generateContent(finalPrompt)
            response.text ?: "Respuesta vacía"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
