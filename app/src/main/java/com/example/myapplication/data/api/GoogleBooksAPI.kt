package com.example.myapplication.data.api

import com.example.myapplication.data.api.models.BookSearchResult
import com.example.myapplication.data.api.models.GoogleBooksResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio de Google Books API
 * v1.4.0 - Búsqueda y autocompletado de libros
 *
 * Documentación: https://developers.google.com/books/docs/v1/using
 * Límite gratuito: 1,000 peticiones/día (30,000/mes)
 */

/**
 * Interface de Retrofit para Google Books API
 */
interface GoogleBooksService {
    /**
     * Buscar libros por query
     *
     * @param query Término de búsqueda (título, autor, ISBN, etc)
     * @param apiKey API Key de Google Books
     * @param maxResults Número máximo de resultados (default: 10)
     * @param langRestrict Restringir idioma (ej: "es", "en")
     */
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("langRestrict") langRestrict: String? = null
    ): Response<GoogleBooksResponse>
}

/**
 * Cliente de Google Books API
 */
class GoogleBooksAPI {

    private val service: GoogleBooksService by lazy {
        ApiConfig.googleBooksRetrofit.create(GoogleBooksService::class.java)
    }

    /**
     * Buscar libros por título, autor o ISBN
     *
     * @param query Término de búsqueda
     * @param maxResults Número máximo de resultados (default: 10)
     * @return Lista de resultados simplificados
     */
    suspend fun searchBooks(
        query: String,
        maxResults: Int = 10
    ): Result<List<BookSearchResult>> = withContext(Dispatchers.IO) {
        try {
            // Verificar que la API key esté configurada
            if (ApiConfig.GOOGLE_BOOKS_KEY == "TU_GOOGLE_BOOKS_API_KEY_AQUI") {
                return@withContext Result.failure(
                    Exception("API Key de Google Books no configurada. Ver ApiConfig.kt")
                )
            }

            // Limpiar query (remover caracteres especiales)
            val cleanQuery = query.trim()
            if (cleanQuery.isEmpty()) {
                return@withContext Result.failure(Exception("La búsqueda no puede estar vacía"))
            }

            // Realizar búsqueda
            val response = service.searchBooks(
                query = cleanQuery,
                apiKey = ApiConfig.GOOGLE_BOOKS_KEY,
                maxResults = maxResults
            )

            // Procesar respuesta
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.items != null) {
                    // Convertir a modelo simplificado
                    val results = body.items.map { BookSearchResult.fromGoogleBookItem(it) }
                    Result.success(results)
                } else {
                    Result.success(emptyList())
                }
            } else {
                val errorMsg = when (response.code()) {
                    400 -> "Búsqueda inválida"
                    403 -> "API Key inválida o límite excedido"
                    404 -> "No se encontraron resultados"
                    else -> "Error del servidor: ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Buscar libro por ISBN específico
     *
     * @param isbn Código ISBN del libro
     * @return Resultado del libro o null si no se encuentra
     */
    suspend fun searchByISBN(isbn: String): Result<BookSearchResult?> = withContext(Dispatchers.IO) {
        try {
            if (ApiConfig.GOOGLE_BOOKS_KEY == "TU_GOOGLE_BOOKS_API_KEY_AQUI") {
                return@withContext Result.failure(
                    Exception("API Key de Google Books no configurada")
                )
            }

            val cleanISBN = isbn.trim().replace("-", "")
            if (cleanISBN.isEmpty()) {
                return@withContext Result.failure(Exception("ISBN no puede estar vacío"))
            }

            val response = service.searchBooks(
                query = "isbn:$cleanISBN",
                apiKey = ApiConfig.GOOGLE_BOOKS_KEY,
                maxResults = 1
            )

            if (response.isSuccessful) {
                val body = response.body()
                val firstItem = body?.items?.firstOrNull()
                if (firstItem != null) {
                    Result.success(BookSearchResult.fromGoogleBookItem(firstItem))
                } else {
                    Result.success(null)
                }
            } else {
                Result.failure(Exception("Error al buscar ISBN: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Buscar libros con filtro de idioma
     *
     * @param query Término de búsqueda
     * @param language Código de idioma (ej: "es", "en")
     * @param maxResults Número máximo de resultados
     * @return Lista de resultados
     */
    suspend fun searchBooksWithLanguage(
        query: String,
        language: String,
        maxResults: Int = 10
    ): Result<List<BookSearchResult>> = withContext(Dispatchers.IO) {
        try {
            if (ApiConfig.GOOGLE_BOOKS_KEY == "TU_GOOGLE_BOOKS_API_KEY_AQUI") {
                return@withContext Result.failure(
                    Exception("API Key de Google Books no configurada")
                )
            }

            val cleanQuery = query.trim()
            if (cleanQuery.isEmpty()) {
                return@withContext Result.failure(Exception("La búsqueda no puede estar vacía"))
            }

            val response = service.searchBooks(
                query = cleanQuery,
                apiKey = ApiConfig.GOOGLE_BOOKS_KEY,
                maxResults = maxResults,
                langRestrict = language
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.items != null) {
                    val results = body.items.map { BookSearchResult.fromGoogleBookItem(it) }
                    Result.success(results)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}
