package com.example.myapplication.data

import java.io.Serializable

data class Book(
    val id: Long = 0,
    val titulo: String,
    val autor: String? = null,
    val paginasTotales: Int? = null,

    // Saga/Colección
    val sagaTitulo: String? = null,
    val sagaVolumen: String? = null,  // String flexible: "1", "6/2", "?", etc.

    // Lectura
    val fechaInicio: String? = null, // YYYY-MM-DD
    val fechaFin: String? = null,    // YYYY-MM-DD
    val estado: BookStatus = BookStatus.PENDIENTE,

    // Extra
    val notas: String? = null,
    val enlaceWeb: String? = null,

    // Metadata
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null
) : Serializable

enum class BookStatus {
    LEIDO,      // Libro leído/terminado
    EN_CURSO,   // Leyendo actualmente
    PENDIENTE   // Por leer
}
