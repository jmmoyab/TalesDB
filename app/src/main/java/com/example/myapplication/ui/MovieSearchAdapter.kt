package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.api.models.MovieSearchResult
import com.example.myapplication.data.api.models.SeriesSearchResult

/**
 * Adapter para mostrar resultados de búsqueda de películas desde TMDB API
 */
class MovieSearchAdapter(
    private var results: List<MovieSearchResult> = emptyList(),
    private val onResultClick: (MovieSearchResult) -> Unit
) : RecyclerView.Adapter<MovieSearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvYear: TextView = view.findViewById(R.id.tvYear)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]

        holder.tvTitle.text = result.title
        holder.tvYear.text = result.year
        holder.tvDetails.visibility = View.GONE

        // Click para seleccionar este resultado
        holder.itemView.setOnClickListener {
            onResultClick(result)
        }
    }

    override fun getItemCount(): Int = results.size

    /**
     * Actualizar lista de resultados
     */
    fun updateResults(newResults: List<MovieSearchResult>) {
        results = newResults
        notifyDataSetChanged()
    }

    /**
     * Limpiar resultados
     */
    fun clear() {
        results = emptyList()
        notifyDataSetChanged()
    }
}

/**
 * Adapter para mostrar resultados de búsqueda de series desde TMDB API
 */
class SeriesSearchAdapter(
    private var results: List<SeriesSearchResult> = emptyList(),
    private val onResultClick: (SeriesSearchResult) -> Unit
) : RecyclerView.Adapter<SeriesSearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvYear: TextView = view.findViewById(R.id.tvYear)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]

        holder.tvTitle.text = result.name
        holder.tvYear.text = result.year

        // Mostrar temporadas y episodios si están disponibles
        if (result.numberOfSeasons != null && result.numberOfEpisodes != null) {
            holder.tvDetails.visibility = View.VISIBLE
            holder.tvDetails.text = "${result.numberOfSeasons} temporadas • ${result.numberOfEpisodes} episodios"
        } else {
            holder.tvDetails.visibility = View.GONE
        }

        // Click para seleccionar este resultado
        holder.itemView.setOnClickListener {
            onResultClick(result)
        }
    }

    override fun getItemCount(): Int = results.size

    /**
     * Actualizar lista de resultados
     */
    fun updateResults(newResults: List<SeriesSearchResult>) {
        results = newResults
        notifyDataSetChanged()
    }

    /**
     * Limpiar resultados
     */
    fun clear() {
        results = emptyList()
        notifyDataSetChanged()
    }
}
