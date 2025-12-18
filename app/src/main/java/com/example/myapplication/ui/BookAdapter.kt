package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Book

class BookAdapter(
    private var items: List<Book> = emptyList(),
    private val onItemClick: (Book) -> Unit = {},
    private val onItemLongClick: (Book) -> Boolean = { false }
) : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

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
        val book = items[position]

        // Click listeners
        holder.itemView.setOnClickListener { onItemClick(book) }
        holder.itemView.setOnLongClickListener { onItemLongClick(book) }

        // Título con saga si aplica
        holder.titleText.text = if (book.sagaTitulo != null && book.sagaVolumen != null) {
            "${book.titulo} (${book.sagaTitulo} #${book.sagaVolumen})"
        } else {
            book.titulo
        }

        // Estado
        holder.statusText.text = "Estado: ${book.estado.name.replace("_", " ")}"

        // Información adicional (autor, páginas, fechas)
        val progressParts = mutableListOf<String>()
        book.autor?.let { progressParts.add("Autor: $it") }
        book.paginasTotales?.let { progressParts.add("$it páginas") }
        book.fechaInicio?.let { progressParts.add("Inicio: $it") }
        book.fechaFin?.let { progressParts.add("Fin: $it") }

        if (progressParts.isNotEmpty()) {
            holder.progressText.visibility = View.VISIBLE
            holder.progressText.text = progressParts.joinToString(" • ")
        } else {
            holder.progressText.visibility = View.GONE
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Book>) {
        items = newItems
        notifyDataSetChanged()
    }
}
