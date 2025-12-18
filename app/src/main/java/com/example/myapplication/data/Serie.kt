package com.example.myapplication.data

data class Serie(
    val id: Long = 0,
    val titulo: String,
    val añoEstreno: Int? = null,
    val plataformas: String? = null, // "Netflix, HBO Max" (permite múltiples)

    // Temporadas y capítulos
    val temporadasTotales: Int? = null,       // Total de temporadas que existen
    val temporadasVistas: Int? = null,        // Temporadas que YO he visto
    val capitulosPorTemporada: String? = null, // "10,12,8" (info extra)
    val temporadaActual: Int? = 1,            // Última temporada viendo
    val capituloActual: Int? = 1,             // Último capítulo visto

    // Fechas de visionado
    val fechaInicioVisionado: String? = null,  // YYYY-MM-DD
    val fechaFinVisionado: String? = null,     // YYYY-MM-DD

    // Estado
    val estado: SerieStatus = SerieStatus.PENDIENTE,

    // Extra
    val notas: String? = null,
    val linkWeb: String? = null,

    // Metadata
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null
)

enum class SerieStatus {
    PENDIENTE,              // No empezada
    EN_CURSO,               // Viéndola actualmente
    TERMINADA,              // Vista completa (todas las temporadas existentes)
    EN_ESPERA_TEMPORADA     // Vista todo lo que hay, esperando nuevas temporadas
}
