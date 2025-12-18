package com.example.myapplication.data

import android.content.Context

class ContentManager(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private val db = dbHelper.writableDatabase

    // DAOs
    val bookDao = BookDao(db)
    val serieDao = SerieDao(db)
    val movieDao = MovieDao(db)

    // Insertar datos de ejemplo (solo si la BD está vacía)
    fun insertSampleData() {
        // Verificar si ya hay datos
        if (bookDao.getAll().isNotEmpty()) {
            return // Ya hay datos, no insertar de nuevo
        }

        // Libros de ejemplo
        bookDao.insert(
            Book(
                titulo = "El Señor de los Anillos: La Comunidad del Anillo",
                autor = "J.R.R. Tolkien",
                paginasTotales = 423,
                sagaTitulo = "El Señor de los Anillos",
                sagaVolumen = "1",
                fechaInicio = "2024-01-15",
                estado = BookStatus.EN_CURSO
            )
        )

        bookDao.insert(
            Book(
                titulo = "El Señor de los Anillos: Las Dos Torres",
                autor = "J.R.R. Tolkien",
                paginasTotales = 352,
                sagaTitulo = "El Señor de los Anillos",
                sagaVolumen = "2",
                estado = BookStatus.PENDIENTE
            )
        )

        bookDao.insert(
            Book(
                titulo = "Dune",
                autor = "Frank Herbert",
                paginasTotales = 688,
                fechaInicio = "2024-03-01",
                fechaFin = "2024-04-20",
                estado = BookStatus.LEIDO
            )
        )

        bookDao.insert(
            Book(
                titulo = "1984",
                autor = "George Orwell",
                paginasTotales = 328,
                fechaInicio = "2023-11-10",
                fechaFin = "2023-12-05",
                estado = BookStatus.LEIDO,
                enlaceWeb = "https://es.wikipedia.org/wiki/1984_(novela)"
            )
        )

        // Series de ejemplo
        serieDao.insert(
            Serie(
                titulo = "Breaking Bad",
                añoEstreno = 2008,
                plataformas = "Netflix",
                temporadasTotales = 5,
                temporadasVistas = 5,
                capitulosPorTemporada = "7,13,13,13,16",
                temporadaActual = 5,
                capituloActual = 16,
                fechaInicioVisionado = "2023-06-01",
                fechaFinVisionado = "2023-08-15",
                estado = SerieStatus.TERMINADA,
                linkWeb = "https://www.netflix.com/title/70143836"
            )
        )

        serieDao.insert(
            Serie(
                titulo = "The Last of Us",
                añoEstreno = 2023,
                plataformas = "HBO Max",
                temporadasTotales = 1,
                temporadasVistas = 0,
                capitulosPorTemporada = "9",
                temporadaActual = 1,
                capituloActual = 5,
                fechaInicioVisionado = "2024-02-10",
                estado = SerieStatus.EN_CURSO
            )
        )

        serieDao.insert(
            Serie(
                titulo = "Stranger Things",
                añoEstreno = 2016,
                plataformas = "Netflix",
                temporadasTotales = 4,
                temporadasVistas = 0,
                capitulosPorTemporada = "8,9,8,9",
                estado = SerieStatus.PENDIENTE
            )
        )

        // Películas de ejemplo
        movieDao.insert(
            Movie(
                titulo = "Inception",
                añoEstreno = 2010,
                plataforma = "Netflix",
                duracionMinutos = "148 min",
                fechaVisionado = "2024-01-20",
                estado = MovieStatus.VISTA
            )
        )

        movieDao.insert(
            Movie(
                titulo = "Interestelar",
                añoEstreno = 2014,
                plataforma = "Prime Video",
                duracionMinutos = "169 min",
                estado = MovieStatus.PENDIENTE,
                linkWeb = "https://www.primevideo.com/"
            )
        )

        movieDao.insert(
            Movie(
                titulo = "The Matrix",
                añoEstreno = 1999,
                plataforma = "HBO Max",
                duracionMinutos = "136 min",
                fechaVisionado = "2024-03-15",
                estado = MovieStatus.VISTA
            )
        )

        // Ejemplo de saga de películas
        movieDao.insert(
            Movie(
                titulo = "Star Wars: Episodio IV - Una Nueva Esperanza",
                añoEstreno = 1977,
                plataforma = "Disney+",
                duracionMinutos = "121 min",
                sagaTitulo = "Star Wars",
                sagaVolumen = "IV",
                fechaVisionado = "2023-05-04",
                estado = MovieStatus.VISTA
            )
        )
    }

    // Cerrar la base de datos (llamar cuando la app se cierra)
    fun close() {
        db.close()
        dbHelper.close()
    }
}
