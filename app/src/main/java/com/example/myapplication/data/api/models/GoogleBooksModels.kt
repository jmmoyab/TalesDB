package com.example.myapplication.data.api.models

import com.google.gson.annotations.SerializedName

/**
 * Modelos de datos para Google Books API
 * Documentación: https://developers.google.com/books/docs/v1/using
 */

/**
 * Respuesta principal de búsqueda de Google Books
 */
data class GoogleBooksResponse(
    @SerializedName("items")
    val items: List<GoogleBookItem>? = null,

    @SerializedName("totalItems")
    val totalItems: Int = 0
)

/**
 * Un libro individual en la respuesta
 */
data class GoogleBookItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfo
)

/**
 * Información detallada del libro
 */
data class VolumeInfo(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("authors")
    val authors: List<String>? = null,

    @SerializedName("publisher")
    val publisher: String? = null,

    @SerializedName("publishedDate")
    val publishedDate: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("pageCount")
    val pageCount: Int? = null,

    @SerializedName("categories")
    val categories: List<String>? = null,

    @SerializedName("imageLinks")
    val imageLinks: ImageLinks? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("industryIdentifiers")
    val industryIdentifiers: List<IndustryIdentifier>? = null
)

/**
 * URLs de las imágenes de portada
 */
data class ImageLinks(
    @SerializedName("thumbnail")
    val thumbnail: String? = null,

    @SerializedName("smallThumbnail")
    val smallThumbnail: String? = null
)

/**
 * Identificadores del libro (ISBN, etc)
 */
data class IndustryIdentifier(
    @SerializedName("type")
    val type: String,

    @SerializedName("identifier")
    val identifier: String
)

/**
 * Modelo simplificado para mostrar en la UI
 */
data class BookSearchResult(
    val id: String,
    val title: String,
    val author: String,
    val pages: Int,
    val year: String,
    val thumbnailUrl: String?,
    val description: String?,
    val categories: String?
) {
    companion object {
        /**
         * Convierte un GoogleBookItem a BookSearchResult
         */
        fun fromGoogleBookItem(item: GoogleBookItem): BookSearchResult {
            val info = item.volumeInfo

            // Extraer año de publishedDate (puede ser "2015" o "2015-01-15")
            val year = info.publishedDate?.take(4) ?: "Desconocido"

            // Unir autores en un solo string
            val author = info.authors?.joinToString(", ") ?: "Autor desconocido"

            // Unir categorías
            val categories = info.categories?.joinToString(", ")

            return BookSearchResult(
                id = item.id,
                title = info.title ?: "Sin título",
                author = author,
                pages = info.pageCount ?: 0,
                year = year,
                thumbnailUrl = info.imageLinks?.thumbnail,
                description = info.description,
                categories = categories
            )
        }
    }
}
