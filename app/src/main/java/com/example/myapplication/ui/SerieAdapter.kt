package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Serie

class SerieAdapter(
    private var items: List<Serie> = emptyList(),
    private val onItemClick: (Serie) -> Unit = {},
    private val onItemLongClick: (Serie) -> Boolean = { false }
) : RecyclerView.Adapter<SerieAdapter.ViewHolder>() {

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
        val serie = items[position]

        // Click listeners
        holder.itemView.setOnClickListener { onItemClick(serie) }
        holder.itemView.setOnLongClickListener { onItemLongClick(serie) }

        // Título
        holder.titleText.text = serie.titulo

        // Estado
        holder.statusText.text = "Estado: ${serie.estado.name.replace("_", " ")}"

        // Información adicional
        val progressParts = mutableListOf<String>()
        serie.plataformas?.let { progressParts.add(it) }

        if (serie.temporadasTotales != null) {
            progressParts.add("T${serie.temporadaActual}E${serie.capituloActual} de ${serie.temporadasTotales} temporadas")
        } else {
            progressParts.add("T${serie.temporadaActual}E${serie.capituloActual}")
        }

        serie.fechaInicioVisionado?.let { progressParts.add("Inicio: $it") }
        serie.fechaFinVisionado?.let { progressParts.add("Fin: $it") }

        holder.progressText.visibility = View.VISIBLE
        holder.progressText.text = progressParts.joinToString(" • ")
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Serie>) {
        items = newItems
        notifyDataSetChanged()
    }
}
