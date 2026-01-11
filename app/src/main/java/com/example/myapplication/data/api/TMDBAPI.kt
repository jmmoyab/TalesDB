package com.example.myapplication.data.api

import com.example.myapplication.data.api.models.MovieSearchResult
import com.example.myapplication.data.api.models.SeriesSearchResult
import com.example.myapplication.data.api.models.TMDBMovie
import com.example.myapplication.data.api.models.TMDBMovieResponse
import com.example.myapplication.data.api.models.TMDBSeriesDetails
import com.example.myapplication.data.api.models.TMDBSeriesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio de TMDB (The Movie Database) API
 * v1.4.0 - Búsqueda y autocompletado de películas y series
 *
 * Documentación: https://developer.themoviedb.org/docs
 * Límite gratuito: 3,000,000 peticiones/mes (40 peticiones/10 segundos)
 */

/**
 * Interface de Retrofit para TMDB API
 */
interface TMDBService {

    /**
     * Buscar películas
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1
    ): Response<TMDBMovieResponse>

    /**
     * Buscar series de TV
     */
    @GET("search/tv")
    suspend fun searchSeries(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1
    ): Response<TMDBSeriesResponse>

    /**
     * Obtener detalles completos de una película (incluye duración)
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): Response<TMDBMovie>

    /**
     * Obtener detalles completos de una serie (incluye temporadas/episodios)
     */
    @GET("tv/{tv_id}")
    suspend fun getSeriesDetails(
        @Path("tv_id") seriesId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): Response<TMDBSeriesDetails>
}

/**
 * Cliente de TMDB API
 */
class TMDBAPI {

    private val service: TMDBService by lazy {
        ApiConfig.tmdbRetrofit.create(TMDBService::class.java)
    }

    // ========== PELÍCULAS ==========

    /**
     * Buscar películas por título
     *
     * @param query Título de la película
     * @param language Idioma de los resultados (default: es-ES)
     * @return Lista de películas encontradas
     */
    suspend fun searchMovies(
        query: String,
        language: String = "es-ES"
    ): Result<List<MovieSearchResult>> = withContext(Dispatchers.IO) {
        try {
            // Verificar API key
            if (ApiConfig.TMDB_KEY == "TU_TMDB_API_KEY_AQUI") {
                return@withContext Result.failure(
                    Exception("API Key de TMDB no configurada. Ver ApiConfig.kt")
                )
            }

            // Validar query
            val cleanQuery = query.trim()
            if (cleanQuery.isEmpty()) {
                return@withContext Result.failure(Exception("La búsqueda no puede estar vacía"))
            }

            // Realizar búsqueda
            val response = service.searchMovies(
                apiKey = ApiConfig.TMDB_KEY,
                query = cleanQuery,
                language = language
            )

            // Procesar respuesta
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.results != null) {
                    val results = body.results.map { MovieSearchResult.fromTMDBMovie(it) }
                    Result.success(results)
                } else {
                    Result.success(emptyList())
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "API Key inválida"
                    404 -> "No se encontraron resultados"
                    429 -> "Demasiadas peticiones. Espera unos segundos"
                    else -> "Error del servidor: ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Buscar película y obtener sus detalles completos (incluye duración)
     * Combina búsqueda + detalles en una sola llamada
     *
     * @param query Título de la película
     * @param language Idioma
     * @return Lista de películas con detalles completos (duración)
     */
    suspend fun searchMoviesWithDetails(
        query: String,
        language: String = "es-ES"
    ): Result<List<MovieSearchResult>> = withContext(Dispatchers.IO) {
        try {
            // Primero buscar películas
            val searchResult = searchMovies(query, language)

            if (searchResult.isFailure) {
                return@withContext searchResult
            }

            val movies = searchResult.getOrNull() ?: emptyList()

            // Obtener detalles de las primeras 5 películas
            val moviesWithDetails = movies.take(5).mapNotNull { movieResult ->
                try {
                    val detailsResponse = service.getMovieDetails(
                        movieId = movieResult.id,
                        apiKey = ApiConfig.TMDB_KEY,
                        language = language
                    )

                    if (detailsResponse.isSuccessful) {
                        val details = detailsResponse.body()
                        if (details != null) {
                            // Crear nuevo resultado con runtime
                            movieResult.copy(runtime = details.runtime)
                        } else {
                            movieResult
                        }
                    } else {
                        movieResult
                    }
                } catch (e: Exception) {
                    movieResult  // Si falla, devolver sin runtime
                }
            }

            Result.success(moviesWithDetails)
        } catch (e: Exception) {
            Result.failure(Exception("Error al buscar películas con detalles: ${e.message}"))
        }
    }

    // ========== SERIES ==========

    /**
     * Buscar series por nombre
     *
     * @param query Nombre de la serie
     * @param language Idioma de los resultados (default: es-ES)
     * @return Lista de series encontradas
     */
    suspend fun searchSeries(
        query: String,
        language: String = "es-ES"
    ): Result<List<SeriesSearchResult>> = withContext(Dispatchers.IO) {
        try {
            // Verificar API key
            if (ApiConfig.TMDB_KEY == "TU_TMDB_API_KEY_AQUI") {
                return@withContext Result.failure(
                    Exception("API Key de TMDB no configurada")
                )
            }

            // Validar query
            val cleanQuery = query.trim()
            if (cleanQuery.isEmpty()) {
                return@withContext Result.failure(Exception("La búsqueda no puede estar vacía"))
            }

            // Realizar búsqueda
            val response = service.searchSeries(
                apiKey = ApiConfig.TMDB_KEY,
                query = cleanQuery,
                language = language
            )

            // Procesar respuesta
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.results != null) {
                    val results = body.results.map { SeriesSearchResult.fromTMDBSeries(it) }
                    Result.success(results)
                } else {
                    Result.success(emptyList())
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "API Key inválida"
                    404 -> "No se encontraron resultados"
                    429 -> "Demasiadas peticiones. Espera unos segundos"
                    else -> "Error del servidor: ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener detalles completos de una serie
     * Incluye número de temporadas y episodios
     *
     * @param seriesId ID de la serie en TMDB
     * @param language Idioma de los resultados
     * @return Detalles de la serie con temporadas/episodios
     */
    suspend fun getSeriesDetails(
        seriesId: Int,
        language: String = "es-ES"
    ): Result<SeriesSearchResult> = withContext(Dispatchers.IO) {
        try {
            if (ApiConfig.TMDB_KEY == "TU_TMDB_API_KEY_AQUI") {
                return@withContext Result.failure(
                    Exception("API Key de TMDB no configurada")
                )
            }

            val response = service.getSeriesDetails(
                seriesId = seriesId,
                apiKey = ApiConfig.TMDB_KEY,
                language = language
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(SeriesSearchResult.fromTMDBSeriesDetails(body))
                } else {
                    Result.failure(Exception("No se encontraron detalles de la serie"))
                }
            } else {
                Result.failure(Exception("Error al obtener detalles: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Buscar serie y obtener sus detalles completos
     * Combina búsqueda + detalles en una sola llamada
     *
     * @param query Nombre de la serie
     * @param language Idioma
     * @return Lista de series con detalles completos (temporadas/episodios)
     */
    suspend fun searchSeriesWithDetails(
        query: String,
        language: String = "es-ES"
    ): Result<List<SeriesSearchResult>> = withContext(Dispatchers.IO) {
        try {
            // Primero buscar series
            val searchResult = searchSeries(query, language)

            if (searchResult.isFailure) {
                return@withContext searchResult
            }

            val series = searchResult.getOrNull() ?: emptyList()

            // Obtener detalles de las primeras 5 series
            val seriesWithDetails = series.take(5).mapNotNull { seriesResult ->
                val detailsResult = getSeriesDetails(seriesResult.id, language)
                detailsResult.getOrNull()
            }

            Result.success(seriesWithDetails)
        } catch (e: Exception) {
            Result.failure(Exception("Error al buscar series con detalles: ${e.message}"))
        }
    }
}
