package com.example.myapplication.data.api.models

import com.google.gson.annotations.SerializedName

/**
 * Modelos de datos para TMDB (The Movie Database) API
 * Documentación: https://developer.themoviedb.org/docs
 */

// ========== PELÍCULAS ==========

/**
 * Respuesta de búsqueda de películas
 */
data class TMDBMovieResponse(
    @SerializedName("results")
    val results: List<TMDBMovie>? = null,

    @SerializedName("total_results")
    val totalResults: Int = 0
)

/**
 * Datos de una película
 */
data class TMDBMovie(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("original_title")
    val originalTitle: String? = null,

    @SerializedName("overview")
    val overview: String? = null,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("poster_path")
    val posterPath: String? = null,

    @SerializedName("vote_average")
    val voteAverage: Double? = null,

    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null,

    @SerializedName("runtime")
    val runtime: Int? = null  // Duración en minutos (solo en detalles)
)

// ========== SERIES ==========

/**
 * Respuesta de búsqueda de series
 */
data class TMDBSeriesResponse(
    @SerializedName("results")
    val results: List<TMDBSeries>? = null,

    @SerializedName("total_results")
    val totalResults: Int = 0
)

/**
 * Datos de una serie
 */
data class TMDBSeries(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("original_name")
    val originalName: String? = null,

    @SerializedName("overview")
    val overview: String? = null,

    @SerializedName("first_air_date")
    val firstAirDate: String? = null,

    @SerializedName("poster_path")
    val posterPath: String? = null,

    @SerializedName("vote_average")
    val voteAverage: Double? = null,

    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null
)

/**
 * Detalles completos de una serie (incluye temporadas)
 */
data class TMDBSeriesDetails(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("overview")
    val overview: String? = null,

    @SerializedName("first_air_date")
    val firstAirDate: String? = null,

    @SerializedName("poster_path")
    val posterPath: String? = null,

    @SerializedName("number_of_seasons")
    val numberOfSeasons: Int? = null,

    @SerializedName("number_of_episodes")
    val numberOfEpisodes: Int? = null,

    @SerializedName("episode_run_time")
    val episodeRunTime: List<Int>? = null
)

// ========== MODELOS SIMPLIFICADOS PARA UI ==========

/**
 * Resultado simplificado de película para mostrar en UI
 */
data class MovieSearchResult(
    val id: Int,
    val title: String,
    val year: String,
    val posterUrl: String?,
    val overview: String?,
    val rating: Double,
    val runtime: Int? = null  // Duración en minutos
) {
    companion object {
        private const val TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/w500"

        fun fromTMDBMovie(movie: TMDBMovie): MovieSearchResult {
            // Extraer año de release_date (formato: "2015-01-15")
            val year = movie.releaseDate?.take(4) ?: "Desconocido"

            // URL completa del poster
            val posterUrl = movie.posterPath?.let { TMDB_IMAGE_BASE + it }

            return MovieSearchResult(
                id = movie.id,
                title = movie.title ?: "Sin título",
                year = year,
                posterUrl = posterUrl,
                overview = movie.overview,
                rating = movie.voteAverage ?: 0.0
            )
        }
    }
}

/**
 * Resultado simplificado de serie para mostrar en UI
 */
data class SeriesSearchResult(
    val id: Int,
    val name: String,
    val year: String,
    val posterUrl: String?,
    val overview: String?,
    val rating: Double,
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null
) {
    companion object {
        private const val TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/w500"

        fun fromTMDBSeries(series: TMDBSeries): SeriesSearchResult {
            // Extraer año de first_air_date (formato: "2015-01-15")
            val year = series.firstAirDate?.take(4) ?: "Desconocido"

            // URL completa del poster
            val posterUrl = series.posterPath?.let { TMDB_IMAGE_BASE + it }

            return SeriesSearchResult(
                id = series.id,
                name = series.name ?: "Sin nombre",
                year = year,
                posterUrl = posterUrl,
                overview = series.overview,
                rating = series.voteAverage ?: 0.0
            )
        }

        fun fromTMDBSeriesDetails(details: TMDBSeriesDetails): SeriesSearchResult {
            // Extraer año
            val year = details.firstAirDate?.take(4) ?: "Desconocido"

            // URL completa del poster
            val posterUrl = details.posterPath?.let { TMDB_IMAGE_BASE + it }

            return SeriesSearchResult(
                id = details.id,
                name = details.name ?: "Sin nombre",
                year = year,
                posterUrl = posterUrl,
                overview = details.overview,
                rating = 0.0,
                numberOfSeasons = details.numberOfSeasons,
                numberOfEpisodes = details.numberOfEpisodes
            )
        }
    }
}
