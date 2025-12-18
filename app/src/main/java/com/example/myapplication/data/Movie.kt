package com.example.myapplication.data

data class Movie(
    val id: Long = 0,
    val titulo: String,
    val añoEstreno: Int? = null,
    val plataforma: String? = null,         // "Netflix", "Disney+", "Cine"
    val duracionMinutos: String? = null,    // "120 min" (como String)

    // Saga/Colección (igual que libros)
    val sagaTitulo: String? = null,         // "Star Wars", "007", "Misión Imposible"
    val sagaVolumen: String? = null,        // "1", "IV", "21" (flexible)

    // Fecha
    val fechaVisionado: String? = null,     // YYYY-MM-DD (una sola fecha)

    // Estado
    val estado: MovieStatus = MovieStatus.PENDIENTE,

    // Extra
    val notas: String? = null,
    val linkWeb: String? = null,

    // Metadata
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null
)

enum class MovieStatus {
    PENDIENTE,   // No vista
    EN_CURSO,    // Empezada pero no terminada
    VISTA        // Vista completa
}
