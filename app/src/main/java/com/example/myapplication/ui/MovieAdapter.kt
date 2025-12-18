package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Movie

class MovieAdapter(
    private var items: List<Movie> = emptyList(),
    private val onItemClick: (Movie) -> Unit = {},
    private val onItemLongClick: (Movie) -> Boolean = { false }
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.title_text)
        val statusText: TextView = view.findViewById(R.id.status_text)
        val progressText: TextView = view.findViewById(R.id.progress_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = items[position]

        // Título
        holder.titleText.text = movie.titulo

        // Estado
        holder.statusText.text = "Estado: ${movie.estado.name.replace("_", " ")}"

        // Información adicional
        val progressParts = mutableListOf<String>()
        movie.añoEstreno?.let { progressParts.add("$it") }
        movie.plataforma?.let { progressParts.add(it) }
        movie.duracionMinutos?.let { progressParts.add(it) }
        if (movie.sagaTitulo != null && movie.sagaVolumen != null) {
            progressParts.add("${movie.sagaTitulo} #${movie.sagaVolumen}")
        }
        movie.fechaVisionado?.let { progressParts.add("Visto: $it") }

        if (progressParts.isNotEmpty()) {
            holder.progressText.visibility = View.VISIBLE
            holder.progressText.text = progressParts.joinToString(" • ")
        } else {
            holder.progressText.visibility = View.GONE
        }

        // Click listeners
        holder.itemView.setOnClickListener { onItemClick(movie) }
        holder.itemView.setOnLongClickListener { onItemLongClick(movie) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Movie>) {
        items = newItems
        notifyDataSetChanged()
    }
}
