package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.api.models.BookSearchResult

/**
 * Adapter para mostrar resultados de búsqueda de libros desde Google Books API
 */
class BookSearchAdapter(
    private var results: List<BookSearchResult> = emptyList(),
    private val onResultClick: (BookSearchResult) -> Unit
) : RecyclerView.Adapter<BookSearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]

        holder.tvTitle.text = result.title
        holder.tvAuthor.text = result.author

        // Mostrar páginas y año
        val details = buildString {
            if (result.pages > 0) {
                append("${result.pages} págs")
            }
            if (result.year != "Desconocido") {
                if (isNotEmpty()) append(" • ")
                append(result.year)
            }
        }
        holder.tvDetails.text = details.ifEmpty { "Sin datos adicionales" }

        // Click para seleccionar este resultado
        holder.itemView.setOnClickListener {
            onResultClick(result)
        }
    }

    override fun getItemCount(): Int = results.size

    /**
     * Actualizar lista de resultados
     */
    fun updateResults(newResults: List<BookSearchResult>) {
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
