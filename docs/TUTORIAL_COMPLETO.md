# 📚 Tutorial Completo: Gestor de Contenido Android

**Fecha:** 21 de Noviembre de 2025
**Versión:** 1.0
**Estado:** App funcional con SQLite

---

## 📖 Tabla de Contenidos

1. [Introducción](#1-introducción)
2. [¿Qué es AndroidIDE?](#2-qué-es-androidide)
3. [Estructura del Proyecto](#3-estructura-del-proyecto)
4. [Arquitectura de la Aplicación](#4-arquitectura-de-la-aplicación)
5. [Capa de Datos (Database)](#5-capa-de-datos-database)
6. [Capa de Presentación (UI)](#6-capa-de-presentación-ui)
7. [Navegación y Flujo](#7-navegación-y-flujo)
8. [Cómo Usar AndroidIDE](#8-cómo-usar-androidide)
9. [Compilar y Ejecutar](#9-compilar-y-ejecutar)
10. [Próximos Pasos](#10-próximos-pasos)

---

## 1. Introducción

### ¿Qué hace esta aplicación?

Esta aplicación Android permite gestionar tu consumo de contenido multimedia:
- **📚 Libros**: Registra libros, sagas, autor, páginas, fechas de lectura
- **📺 Series**: Registra series, temporadas, capítulos, plataformas de streaming
- **🎬 Películas**: Registra películas, duración, plataforma, fechas de visualización

### Características principales

✅ **Navegación por pestañas** - 3 secciones separadas
✅ **Base de datos SQLite** - Persistencia local de datos
✅ **Estados personalizados** - Diferentes estados para cada tipo de contenido
✅ **Datos de ejemplo** - 10 items precargados para probar
✅ **Interfaz Material Design** - Cards modernas y atractivas

---

## 2. ¿Qué es AndroidIDE?

### Descripción

**AndroidIDE** es un IDE (Entorno de Desarrollo Integrado) completo para Android que se ejecuta directamente en tu dispositivo Android. No necesitas PC.

🌐 **Sitio oficial**: https://m.androidide.com/
📥 **Descargar**: [Google Play Store](https://play.google.com/store/apps/details?id=com.itsaky.androidide) o GitHub Releases

### Características de AndroidIDE

- ✅ Editor de código con resaltado de sintaxis
- ✅ Autocompletado inteligente
- ✅ Gradle build system
- ✅ Terminal integrada
- ✅ Git integrado
- ✅ Soporte para Kotlin y Java
- ✅ Compilación y ejecución de APKs
- ⚠️ **Limitación**: No soporta todas las librerías nativas (JNI)

### Interfaz de AndroidIDE

```
┌─────────────────────────────────────┐
│  AndroidIDE                    [≡]  │ ← Menú principal
├─────────────────────────────────────┤
│  📁 Project                         │ ← Árbol de archivos
│  ├─ app/                            │
│  │  ├─ src/                         │
│  │  │  ├─ main/                     │
│  │  │  │  ├─ java/                  │
│  │  │  │  └─ res/                   │
│  │  └─ build.gradle.kts             │
│  └─ gradle/                         │
├─────────────────────────────────────┤
│  [Editor de Código]                 │ ← Pestaña de archivos abiertos
│                                     │
│  package com.example...             │
│  import androidx...                 │
│  ...                                │
├─────────────────────────────────────┤
│  Terminal                       [_] │ ← Terminal bash
└─────────────────────────────────────┘
```

### Ventajas y Desventajas

**✅ Ventajas:**
- No requiere PC
- Portátil (desarrolla desde cualquier lugar)
- Gratis y open source
- Gradle oficial

**❌ Desventajas:**
- No soporta Room Persistence Library (librerías nativas)
- KSP/KAPT pueden fallar
- Más lento que Android Studio en PC
- Pantalla pequeña (usar tablet recomendado)

---

## 3. Estructura del Proyecto

### Vista General

```
My Application/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/myapplication/
│   │   │   │   ├── MainActivity.kt                    ← Actividad principal
│   │   │   │   ├── data/                              ← Capa de datos
│   │   │   │   │   ├── Book.kt                        ← Modelo de libro
│   │   │   │   │   ├── Serie.kt                       ← Modelo de serie
│   │   │   │   │   ├── Movie.kt                       ← Modelo de película
│   │   │   │   │   ├── DatabaseHelper.kt              ← Gestión SQLite
│   │   │   │   │   ├── BookDao.kt                     ← DAO de libros
│   │   │   │   │   ├── SerieDao.kt                    ← DAO de series
│   │   │   │   │   ├── MovieDao.kt                    ← DAO de películas
│   │   │   │   │   └── ContentManager.kt              ← Gestor unificado
│   │   │   │   └── ui/                                ← Capa de presentación
│   │   │   │       ├── BooksFragment.kt               ← Fragmento de libros
│   │   │   │       ├── SeriesFragment.kt              ← Fragmento de series
│   │   │   │       ├── MoviesFragment.kt              ← Fragmento de películas
│   │   │   │       ├── BookAdapter.kt                 ← Adaptador de libros
│   │   │   │       ├── SerieAdapter.kt                ← Adaptador de series
│   │   │   │       └── MovieAdapter.kt                ← Adaptador de películas
│   │   │   └── res/                                   ← Recursos
│   │   │       ├── layout/
│   │   │       │   ├── activity_main.xml              ← Layout principal
│   │   │       │   ├── fragment_books.xml             ← Layout fragmento libros
│   │   │       │   ├── fragment_series.xml            ← Layout fragmento series
│   │   │       │   ├── fragment_movies.xml            ← Layout fragmento películas
│   │   │       │   └── item_content.xml               ← Layout card de item
│   │   │       ├── menu/
│   │   │       │   └── bottom_nav_menu.xml            ← Menú de navegación
│   │   │       ├── drawable/
│   │   │       │   ├── ic_book.xml                    ← Icono de libro
│   │   │       │   ├── ic_tv.xml                      ← Icono de TV
│   │   │       │   └── ic_movie.xml                   ← Icono de película
│   │   │       └── values/
│   │   │           ├── strings.xml
│   │   │           ├── colors.xml
│   │   │           └── themes.xml
│   └── build.gradle.kts                               ← Dependencias y config
├── gradle/
├── gradlew                                            ← Gradle wrapper (Linux)
├── settings.gradle.kts
├── estado_proyecto.md                                 ← Documentación
├── database_schema.sql                                ← Esquema de BD
├── ultima_claude.md                                   ← Última sesión
├── PROXIMA_SESION.md                                  ← Próximos pasos
└── TUTORIAL_COMPLETO.md                               ← Este archivo
```

### Convenciones de Nombres

**Archivos Kotlin:**
- `PascalCase` para clases: `MainActivity`, `BookAdapter`
- `camelCase` para funciones: `loadFragment()`, `getAllBooks()`
- `UPPER_SNAKE_CASE` para constantes: `DATABASE_VERSION`

**Archivos XML:**
- `snake_case` para todos los archivos: `activity_main.xml`, `item_content.xml`
- `@+id/` con snake_case: `@+id/recycler_view`, `@+id/text_title`

**Packages:**
- `lowercase`: `com.example.myapplication`, `data`, `ui`

---

## 4. Arquitectura de la Aplicación

### Patrón de Diseño: Capas

```
┌─────────────────────────────────────┐
│         UI Layer (Presentación)     │
│  - Fragments                        │
│  - Adapters                         │
│  - ViewBinding                      │
└─────────────────┬───────────────────┘
                  │
                  │ Usa
                  ▼
┌─────────────────────────────────────┐
│       Data Layer (Datos)            │
│  - Models (Book, Serie, Movie)      │
│  - DAOs (BookDao, SerieDao...)      │
│  - DatabaseHelper                   │
│  - ContentManager                   │
└─────────────────┬───────────────────┘
                  │
                  │ Accede
                  ▼
┌─────────────────────────────────────┐
│         SQLite Database             │
│  - books table                      │
│  - series table                     │
│  - movies table                     │
└─────────────────────────────────────┘
```

### Flujo de Datos

**Lectura (Database → UI):**
```
1. Fragment se crea
2. Fragment llama a ContentManager.getAllBooks()
3. ContentManager llama a BookDao.getAll()
4. BookDao ejecuta query SQL
5. SQLite devuelve Cursor
6. DAO convierte Cursor a List<Book>
7. Fragment recibe List<Book>
8. Fragment pasa datos a BookAdapter
9. Adapter muestra items en RecyclerView
```

**Escritura (UI → Database):**
```
1. Usuario hace click en botón "Agregar"
2. Fragment muestra diálogo de formulario
3. Usuario llena campos y confirma
4. Fragment crea objeto Book
5. Fragment llama a ContentManager.insertBook(book)
6. ContentManager llama a BookDao.insert(book)
7. DAO convierte Book a ContentValues
8. DAO ejecuta INSERT SQL
9. SQLite guarda datos
10. Fragment refresca RecyclerView
```

### Componentes Principales

#### MainActivity
- **Rol**: Punto de entrada de la app
- **Responsabilidad**: Gestionar navegación entre fragmentos
- **Tecnología**: ViewBinding, FragmentManager

#### Fragments (BooksFragment, SeriesFragment, MoviesFragment)
- **Rol**: Mostrar lista de items
- **Responsabilidad**: Cargar datos y mostrarlos en RecyclerView
- **Tecnología**: ViewBinding, RecyclerView

#### Adapters (BookAdapter, SerieAdapter, MovieAdapter)
- **Rol**: Adaptar datos a vistas
- **Responsabilidad**: Inflar layouts y vincular datos a vistas
- **Tecnología**: RecyclerView.Adapter

#### DAOs (BookDao, SerieDao, MovieDao)
- **Rol**: Acceso a base de datos
- **Responsabilidad**: CRUD operations (Create, Read, Update, Delete)
- **Tecnología**: SQLiteDatabase, Cursor

#### ContentManager
- **Rol**: Gestor unificado de datos
- **Responsabilidad**: Coordinar acceso a todos los DAOs
- **Tecnología**: SQLiteOpenHelper

---

## 5. Capa de Datos (Database)

### 5.1. Modelo de Datos

#### Book.kt - Modelo de Libro

```kotlin
package com.example.myapplication.data

data class Book(
    val id: Long = 0,                          // ID autoincremental (0 = nuevo)
    val titulo: String,                        // Título del libro (OBLIGATORIO)
    val autor: String? = null,                 // Autor (opcional)
    val paginasTotales: Int? = null,           // Número de páginas (opcional)
    val sagaTitulo: String? = null,            // Nombre de la saga (opcional)
    val sagaVolumen: Int? = null,              // Número de volumen en saga (opcional)
    val fechaInicio: String? = null,           // Fecha inicio lectura YYYY-MM-DD (opcional)
    val fechaFin: String? = null,              // Fecha fin lectura YYYY-MM-DD (opcional)
    val estado: BookStatus = BookStatus.REGISTRADO,  // Estado actual (default: REGISTRADO)
    val enlaceWeb: String? = null,             // URL relacionada (opcional)
    val fechaCreacion: String? = null,         // Timestamp creación (auto)
    val fechaActualizacion: String? = null     // Timestamp actualización (auto)
)

// Enum de estados válidos para libros
enum class BookStatus {
    REGISTRADO,    // Libro agregado pero no empezado
    EN_CURSO,      // Leyendo actualmente
    PENDIENTE      // Leído/terminado
}
```

**Explicación:**
- `data class`: Clase especial de Kotlin que genera automáticamente `equals()`, `hashCode()`, `toString()`, `copy()`
- `val`: Propiedades inmutables (solo lectura)
- `String?`: El `?` indica que puede ser `null` (opcional)
- `= valor`: Valor por defecto

#### Serie.kt - Modelo de Serie

```kotlin
package com.example.myapplication.data

data class Serie(
    val id: Long = 0,
    val titulo: String,                              // OBLIGATORIO
    val cadena: String? = null,                      // Netflix, HBO Max, etc.
    val temporadasTotales: Int? = null,              // Total de temporadas
    val capitulosPorTemporada: String? = null,       // Ej: "10,12,8" = T1:10 caps, T2:12 caps, T3:8 caps
    val temporadaActual: Int? = 1,                   // Temporada viendo actualmente
    val capituloActual: Int? = 1,                    // Capítulo viendo actualmente
    val fechaInicio: String? = null,                 // Fecha inicio YYYY-MM-DD
    val fechaFin: String? = null,                    // Fecha fin YYYY-MM-DD
    val estado: SerieStatus = SerieStatus.REGISTRADO,
    val enlaceWeb: String? = null,
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null
)

enum class SerieStatus {
    EN_CURSO,                  // Viendo actualmente
    PENDIENTE,                 // Por ver
    VISTA,                     // Vista completa
    MAS_TEMPORADAS_A_LA_VISTA  // Vista pero esperando nuevas temporadas
}
```

**Características especiales:**
- `capitulosPorTemporada`: String con formato CSV, ejemplo: `"8,10,12"` significa:
  - Temporada 1: 8 capítulos
  - Temporada 2: 10 capítulos
  - Temporada 3: 12 capítulos

#### Movie.kt - Modelo de Película

```kotlin
package com.example.myapplication.data

data class Movie(
    val id: Long = 0,
    val titulo: String,                              // OBLIGATORIO
    val cadena: String? = null,                      // Netflix, Cine, etc.
    val duracionMinutos: Int? = null,                // Duración en minutos
    val fechaInicio: String? = null,                 // Fecha visualización
    val fechaFin: String? = null,                    // Fecha fin
    val estado: MovieStatus = MovieStatus.REGISTRADO,
    val enlaceWeb: String? = null,
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null
)

enum class MovieStatus {
    EN_CURSO,      // Empezada pero no terminada
    PENDIENTE,     // Por ver
    VISTA          // Vista completa
}
```

### 5.2. Base de Datos SQLite

#### DatabaseHelper.kt - Gestión de la BD

```kotlin
package com.example.myapplication.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,    // Nombre del archivo: content_manager.db
    null,
    DATABASE_VERSION  // Versión: 1
) {
    companion object {
        const val DATABASE_NAME = "content_manager.db"
        const val DATABASE_VERSION = 1

        // Nombres de tablas
        const val TABLE_BOOKS = "books"
        const val TABLE_SERIES = "series"
        const val TABLE_MOVIES = "movies"

        // Columnas comunes
        const val COLUMN_ID = "id"
        const val COLUMN_TITULO = "titulo"
        const val COLUMN_ESTADO = "estado"
        const val COLUMN_ENLACE_WEB = "enlace_web"
        const val COLUMN_FECHA_INICIO = "fecha_inicio"
        const val COLUMN_FECHA_FIN = "fecha_fin"
        const val COLUMN_FECHA_CREACION = "fecha_creacion"
        const val COLUMN_FECHA_ACTUALIZACION = "fecha_actualizacion"

        // Columnas específicas de BOOKS
        const val COLUMN_AUTOR = "autor"
        const val COLUMN_PAGINAS_TOTALES = "paginas_totales"
        const val COLUMN_SAGA_TITULO = "saga_titulo"
        const val COLUMN_SAGA_VOLUMEN = "saga_volumen"

        // Columnas específicas de SERIES
        const val COLUMN_CADENA = "cadena"
        const val COLUMN_TEMPORADAS_TOTALES = "temporadas_totales"
        const val COLUMN_CAPITULOS_POR_TEMPORADA = "capitulos_por_temporada"
        const val COLUMN_TEMPORADA_ACTUAL = "temporada_actual"
        const val COLUMN_CAPITULO_ACTUAL = "capitulo_actual"

        // Columnas específicas de MOVIES
        const val COLUMN_DURACION_MINUTOS = "duracion_minutos"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla BOOKS
        db.execSQL("""
            CREATE TABLE $TABLE_BOOKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITULO TEXT NOT NULL,
                $COLUMN_AUTOR TEXT,
                $COLUMN_PAGINAS_TOTALES INTEGER,
                $COLUMN_SAGA_TITULO TEXT,
                $COLUMN_SAGA_VOLUMEN INTEGER,
                $COLUMN_FECHA_INICIO TEXT,
                $COLUMN_FECHA_FIN TEXT,
                $COLUMN_ESTADO TEXT NOT NULL DEFAULT 'REGISTRADO',
                $COLUMN_ENLACE_WEB TEXT,
                $COLUMN_FECHA_CREACION TEXT DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_FECHA_ACTUALIZACION TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """)

        // Crear índices para optimización
        db.execSQL("CREATE INDEX idx_books_estado ON $TABLE_BOOKS($COLUMN_ESTADO)")
        db.execSQL("CREATE INDEX idx_books_autor ON $TABLE_BOOKS($COLUMN_AUTOR)")
        db.execSQL("CREATE INDEX idx_books_saga ON $TABLE_BOOKS($COLUMN_SAGA_TITULO)")

        // ... Similar para SERIES y MOVIES ...
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // En caso de cambio de versión (futuro)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SERIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        onCreate(db)
    }
}
```

**Explicación:**
- `SQLiteOpenHelper`: Clase de Android que gestiona creación y actualización de BD
- `onCreate()`: Se ejecuta la primera vez que se crea la BD
- `onUpgrade()`: Se ejecuta cuando cambias `DATABASE_VERSION`
- **Índices**: Aceleran búsquedas por columnas específicas (estado, autor, saga)

**Ubicación de la BD en el dispositivo:**
```
/data/data/com.example.myapplication/databases/content_manager.db
```

### 5.3. DAOs (Data Access Objects)

#### BookDao.kt - Operaciones de Libros

```kotlin
package com.example.myapplication.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class BookDao(private val db: SQLiteDatabase) {

    // --- OPERACIONES CRUD ---

    // CREATE: Insertar nuevo libro
    fun insert(book: Book): Long {
        val values = ContentValues().apply {
            put("titulo", book.titulo)
            put("autor", book.autor)
            put("paginas_totales", book.paginasTotales)
            put("saga_titulo", book.sagaTitulo)
            put("saga_volumen", book.sagaVolumen)
            put("fecha_inicio", book.fechaInicio)
            put("fecha_fin", book.fechaFin)
            put("estado", book.estado.name)  // Enum → String
            put("enlace_web", book.enlaceWeb)
        }
        return db.insert("books", null, values)
    }

    // READ: Obtener todos los libros
    fun getAll(): List<Book> {
        val books = mutableListOf<Book>()
        val cursor = db.query(
            "books",           // Tabla
            null,              // Columnas (null = todas)
            null,              // WHERE (null = sin filtro)
            null,              // WHERE args
            null,              // GROUP BY
            null,              // HAVING
            "fecha_creacion DESC"  // ORDER BY (más recientes primero)
        )

        cursor.use {
            while (it.moveToNext()) {
                books.add(cursorToBook(it))
            }
        }
        return books
    }

    // READ: Obtener libro por ID
    fun getById(id: Long): Book? {
        val cursor = db.query(
            "books",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return cursorToBook(it)
            }
        }
        return null
    }

    // UPDATE: Actualizar libro existente
    fun update(book: Book): Int {
        val values = ContentValues().apply {
            put("titulo", book.titulo)
            put("autor", book.autor)
            put("paginas_totales", book.paginasTotales)
            put("saga_titulo", book.sagaTitulo)
            put("saga_volumen", book.sagaVolumen)
            put("fecha_inicio", book.fechaInicio)
            put("fecha_fin", book.fechaFin)
            put("estado", book.estado.name)
            put("enlace_web", book.enlaceWeb)
            put("fecha_actualizacion", "CURRENT_TIMESTAMP")
        }
        return db.update("books", values, "id = ?", arrayOf(book.id.toString()))
    }

    // DELETE: Eliminar libro
    fun delete(id: Long): Int {
        return db.delete("books", "id = ?", arrayOf(id.toString()))
    }

    // --- CONSULTAS ESPECIALIZADAS ---

    // Obtener libros por estado
    fun getByEstado(estado: BookStatus): List<Book> {
        val books = mutableListOf<Book>()
        val cursor = db.query(
            "books",
            null,
            "estado = ?",
            arrayOf(estado.name),
            null, null,
            "fecha_creacion DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                books.add(cursorToBook(it))
            }
        }
        return books
    }

    // Obtener libros por autor
    fun getByAutor(autor: String): List<Book> {
        val books = mutableListOf<Book>()
        val cursor = db.query(
            "books",
            null,
            "autor LIKE ?",
            arrayOf("%$autor%"),  // Búsqueda parcial
            null, null,
            "fecha_creacion DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                books.add(cursorToBook(it))
            }
        }
        return books
    }

    // Obtener libros de una saga
    fun getBySaga(sagaTitulo: String): List<Book> {
        val books = mutableListOf<Book>()
        val cursor = db.query(
            "books",
            null,
            "saga_titulo = ?",
            arrayOf(sagaTitulo),
            null, null,
            "saga_volumen ASC"  // Ordenar por volumen
        )

        cursor.use {
            while (it.moveToNext()) {
                books.add(cursorToBook(it))
            }
        }
        return books
    }

    // --- ESTADÍSTICAS ---

    // Contar libros por año
    fun getCountByYear(): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        val cursor = db.rawQuery("""
            SELECT strftime('%Y', fecha_inicio) as year, COUNT(*) as count
            FROM books
            WHERE fecha_inicio IS NOT NULL
            GROUP BY year
            ORDER BY year DESC
        """, null)

        cursor.use {
            while (it.moveToNext()) {
                val year = it.getString(0)
                val count = it.getInt(1)
                counts[year] = count
            }
        }
        return counts
    }

    // Contar libros por estado
    fun getCountByEstado(): Map<BookStatus, Int> {
        val counts = mutableMapOf<BookStatus, Int>()
        val cursor = db.rawQuery("""
            SELECT estado, COUNT(*) as count
            FROM books
            GROUP BY estado
        """, null)

        cursor.use {
            while (it.moveToNext()) {
                val estadoStr = it.getString(0)
                val count = it.getInt(1)
                try {
                    val estado = BookStatus.valueOf(estadoStr)
                    counts[estado] = count
                } catch (e: IllegalArgumentException) {
                    // Estado inválido, ignorar
                }
            }
        }
        return counts
    }

    // --- HELPER PRIVADO ---

    // Convertir Cursor (fila de BD) a objeto Book
    private fun cursorToBook(cursor: Cursor): Book {
        return Book(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
            autor = cursor.getString(cursor.getColumnIndexOrThrow("autor")),
            paginasTotales = cursor.getIntOrNull(cursor.getColumnIndexOrThrow("paginas_totales")),
            sagaTitulo = cursor.getString(cursor.getColumnIndexOrThrow("saga_titulo")),
            sagaVolumen = cursor.getIntOrNull(cursor.getColumnIndexOrThrow("saga_volumen")),
            fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inicio")),
            fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fecha_fin")),
            estado = BookStatus.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            ),
            enlaceWeb = cursor.getString(cursor.getColumnIndexOrThrow("enlace_web")),
            fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion")),
            fechaActualizacion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion"))
        )
    }

    // Extension function para obtener Int nullable
    private fun Cursor.getIntOrNull(columnIndex: Int): Int? {
        return if (isNull(columnIndex)) null else getInt(columnIndex)
    }
}
```

**Explicación técnica:**

1. **ContentValues**: Contenedor clave-valor para INSERT/UPDATE
2. **Cursor**: Apunta a resultados de query (como un iterador)
3. **cursor.use {}**: Kotlin extension que cierra automáticamente el cursor
4. **getColumnIndexOrThrow()**: Obtiene índice de columna o lanza excepción si no existe
5. **rawQuery()**: Query SQL directo para consultas complejas

**Mismo patrón para SerieDao y MovieDao** con sus campos específicos.

### 5.4. ContentManager - Gestor Unificado

```kotlin
package com.example.myapplication.data

import android.content.Context

class ContentManager(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private val db = dbHelper.writableDatabase

    // DAOs
    val bookDao = BookDao(db)
    val serieDao = SerieDao(db)
    val movieDao = MovieDao(db)

    // Función de inicialización con datos de ejemplo
    fun initializeSampleData() {
        // Solo insertar si la BD está vacía
        if (bookDao.getAll().isEmpty()) {
            insertSampleBooks()
        }
        if (serieDao.getAll().isEmpty()) {
            insertSampleSeries()
        }
        if (movieDao.getAll().isEmpty()) {
            insertSampleMovies()
        }
    }

    private fun insertSampleBooks() {
        // LOTR - Saga
        bookDao.insert(Book(
            titulo = "El Señor de los Anillos: La Comunidad del Anillo",
            autor = "J.R.R. Tolkien",
            paginasTotales = 423,
            sagaTitulo = "El Señor de los Anillos",
            sagaVolumen = 1,
            fechaInicio = "2024-01-15",
            estado = BookStatus.EN_CURSO
        ))

        bookDao.insert(Book(
            titulo = "El Señor de los Anillos: Las Dos Torres",
            autor = "J.R.R. Tolkien",
            paginasTotales = 352,
            sagaTitulo = "El Señor de los Anillos",
            sagaVolumen = 2,
            estado = BookStatus.REGISTRADO
        ))

        // Libro individual
        bookDao.insert(Book(
            titulo = "Dune",
            autor = "Frank Herbert",
            paginasTotales = 688,
            estado = BookStatus.PENDIENTE
        ))

        bookDao.insert(Book(
            titulo = "1984",
            autor = "George Orwell",
            paginasTotales = 328,
            fechaInicio = "2023-11-10",
            fechaFin = "2023-12-05",
            estado = BookStatus.PENDIENTE
        ))
    }

    private fun insertSampleSeries() {
        serieDao.insert(Serie(
            titulo = "Breaking Bad",
            cadena = "Netflix",
            temporadasTotales = 5,
            capitulosPorTemporada = "7,13,13,13,16",
            temporadaActual = 5,
            capituloActual = 16,
            fechaInicio = "2023-06-01",
            fechaFin = "2023-08-15",
            estado = SerieStatus.VISTA
        ))

        serieDao.insert(Serie(
            titulo = "The Last of Us",
            cadena = "HBO Max",
            temporadasTotales = 1,
            capitulosPorTemporada = "9",
            temporadaActual = 1,
            capituloActual = 5,
            fechaInicio = "2024-02-10",
            estado = SerieStatus.EN_CURSO
        ))

        serieDao.insert(Serie(
            titulo = "Stranger Things",
            cadena = "Netflix",
            temporadasTotales = 4,
            capitulosPorTemporada = "8,9,8,9",
            temporadaActual = 1,
            capituloActual = 1,
            estado = SerieStatus.PENDIENTE
        ))
    }

    private fun insertSampleMovies() {
        movieDao.insert(Movie(
            titulo = "Inception",
            cadena = "Netflix",
            duracionMinutos = 148,
            fechaInicio = "2024-01-20",
            fechaFin = "2024-01-20",
            estado = MovieStatus.VISTA
        ))

        movieDao.insert(Movie(
            titulo = "Interestelar",
            cadena = "Prime Video",
            duracionMinutos = 169,
            estado = MovieStatus.PENDIENTE
        ))

        movieDao.insert(Movie(
            titulo = "The Matrix",
            cadena = "HBO Max",
            duracionMinutos = 136,
            fechaInicio = "2024-03-10",
            estado = MovieStatus.EN_CURSO
        ))
    }

    fun close() {
        db.close()
        dbHelper.close()
    }
}
```

**Uso típico:**
```kotlin
val contentManager = ContentManager(context)
contentManager.initializeSampleData()  // Primera vez

// Obtener datos
val books = contentManager.bookDao.getAll()
val series = contentManager.serieDao.getAll()

// Insertar
val newBook = Book(titulo = "Harry Potter", autor = "J.K. Rowling", ...)
contentManager.bookDao.insert(newBook)
```

---

## 6. Capa de Presentación (UI)

### 6.1. MainActivity - Navegación Principal

```kotlin
package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.BooksFragment
import com.example.myapplication.ui.SeriesFragment
import com.example.myapplication.ui.MoviesFragment

class MainActivity : AppCompatActivity() {

    // ViewBinding - Acceso seguro a vistas del XML
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = checkNotNull(_binding) { "Activity has been destroyed" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar layout con ViewBinding
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar fragmento inicial (Libros)
        if (savedInstanceState == null) {
            loadFragment(BooksFragment())
        }

        // Configurar navegación del menú inferior
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_books -> {
                    loadFragment(BooksFragment())
                    true
                }
                R.id.nav_series -> {
                    loadFragment(SeriesFragment())
                    true
                }
                R.id.nav_movies -> {
                    loadFragment(MoviesFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Reemplazar fragmento actual con nuevo
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Limpiar ViewBinding al destruir
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
```

**Layout activity_main.xml:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Contenedor de fragmentos -->
    <FrameLayout
        android:id="@+id/fragment_container"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottom_navigation"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Menú de navegación inferior -->
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_navigation"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:menu="@menu/bottom_nav_menu" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Menú bottom_nav_menu.xml:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_books"
        android:icon="@drawable/ic_book"
        android:title="Libros" />
    <item
        android:id="@+id/nav_series"
        android:icon="@drawable/ic_tv"
        android:title="Series" />
    <item
        android:id="@+id/nav_movies"
        android:icon="@drawable/ic_movie"
        android:title="Películas" />
</menu>
```

### 6.2. BooksFragment - Lista de Libros

```kotlin
package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.ContentManager
import com.example.myapplication.databinding.FragmentBooksBinding

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    private lateinit var contentManager: ContentManager
    private lateinit var adapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ContentManager
        contentManager = ContentManager(requireContext())
        contentManager.initializeSampleData()

        // Configurar RecyclerView
        adapter = BookAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Cargar datos
        loadBooks()
    }

    private fun loadBooks() {
        val books = contentManager.bookDao.getAll()
        adapter.submitList(books)

        // Mostrar mensaje si lista vacía
        if (books.isEmpty()) {
            binding.emptyTextView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyTextView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

**Layout fragment_books.xml:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <!-- Lista de libros -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Mensaje cuando está vacío -->
    <TextView
        android:id="@+id/empty_text_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="No hay libros registrados"
        android:textSize="16sp"
        android:visibility="gone"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 6.3. BookAdapter - Adaptador de Libros

```kotlin
package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Book
import com.example.myapplication.databinding.ItemContentBinding

class BookAdapter : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private var books = listOf<Book>()

    // Actualizar lista de libros
    fun submitList(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()  // Notificar cambios
    }

    // Crear nueva vista (ViewHolder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemContentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    // Vincular datos a vista existente
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    // Cantidad de items
    override fun getItemCount(): Int = books.size

    // ViewHolder - Contenedor de vistas
    class BookViewHolder(
        private val binding: ItemContentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.textTitle.text = book.titulo

            // Construir subtítulo con información
            val details = buildString {
                book.autor?.let { append("$it • ") }
                book.paginasTotales?.let { append("$it pág") }

                // Agregar info de saga si existe
                if (book.sagaTitulo != null && book.sagaVolumen != null) {
                    if (isNotEmpty()) append("\n")
                    append("${book.sagaTitulo} #${book.sagaVolumen}")
                }

                // Agregar fechas si existen
                if (book.fechaInicio != null) {
                    if (isNotEmpty()) append("\n")
                    append("Inicio: ${book.fechaInicio}")
                    book.fechaFin?.let { append(" • Fin: $it") }
                }
            }

            binding.textDetails.text = details
            binding.textEstado.text = formatEstado(book.estado.name)
        }

        private fun formatEstado(estado: String): String {
            return estado.replace("_", " ").lowercase().capitalize()
        }
    }
}
```

**Layout item_content.xml (Card de item):**

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardElevation="4dp"
    app:cardCornerRadius="8dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="16dp">

        <!-- Título -->
        <TextView
            android:id="@+id/text_title"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:text="Título del contenido"
            android:textSize="18sp"
            android:textStyle="bold"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toStartOf="@id/text_estado" />

        <!-- Estado -->
        <TextView
            android:id="@+id/text_estado"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Estado"
            android:textSize="12sp"
            android:textColor="@color/colorPrimary"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintEnd_toEndOf="parent" />

        <!-- Detalles -->
        <TextView
            android:id="@+id/text_details"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:text="Detalles adicionales"
            android:textSize="14sp"
            android:layout_marginTop="8dp"
            app:layout_constraintTop_toBottomOf="@id/text_title"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>

</com.google.android.material.card.MaterialCardView>
```

**Mismo patrón para SeriesFragment/SerieAdapter y MoviesFragment/MovieAdapter** con sus campos específicos.

---

## 7. Navegación y Flujo

### Flujo de la App

```
[App Launch]
     │
     ▼
[MainActivity onCreate]
     │
     ├─ Inicializa ViewBinding
     ├─ Carga BooksFragment (inicial)
     └─ Configura BottomNavigationView
     │
     ▼
[Usuario hace click en pestaña]
     │
     ▼
[BottomNavigationView.setOnItemSelectedListener]
     │
     ├─ nav_books  → loadFragment(BooksFragment())
     ├─ nav_series → loadFragment(SeriesFragment())
     └─ nav_movies → loadFragment(MoviesFragment())
     │
     ▼
[Fragment onCreateView]
     │
     ├─ Infla layout (fragment_*.xml)
     └─ Retorna vista
     │
     ▼
[Fragment onViewCreated]
     │
     ├─ Inicializa ContentManager
     ├─ Llama initializeSampleData() (si es primera vez)
     ├─ Configura RecyclerView con Adapter
     └─ Carga datos: contentManager.bookDao.getAll()
     │
     ▼
[Adapter recibe datos]
     │
     ├─ submitList(books)
     ├─ notifyDataSetChanged()
     └─ RecyclerView renderiza items
     │
     ▼
[Usuario ve lista de items en pantalla]
```

---

## 8. Cómo Usar AndroidIDE

### 8.1. Abrir el Proyecto

1. **Lanzar AndroidIDE**
2. **Open → Buscar carpeta del proyecto**
   - Ubicación: `/storage/emulated/0/AndroidIDEProjects/My Application`
3. **Esperar a que cargue el proyecto**
   - Verás árbol de archivos en panel izquierdo

### 8.2. Navegar por el Código

**Panel izquierdo - Árbol de archivos:**
```
📁 My Application
  📁 app
    📁 src
      📁 main
        📁 java/com/example/myapplication
          📄 MainActivity.kt          ← Click para abrir
          📁 data
            📄 Book.kt
            📄 BookDao.kt
            ...
          📁 ui
            📄 BooksFragment.kt
            ...
        📁 res
          📁 layout
            📄 activity_main.xml
            ...
    📄 build.gradle.kts               ← Dependencias
```

**Atajos de teclado:**
- `Ctrl + F`: Buscar en archivo actual
- `Ctrl + Shift + F`: Buscar en todo el proyecto
- `Ctrl + Click`: Ir a definición
- `Ctrl + Space`: Autocompletado

### 8.3. Editar Código

1. **Click en archivo** en árbol
2. **Editar en panel central**
3. **Guardar**: `Ctrl + S` o botón Save

**Ejemplo - Cambiar título de la app:**

1. Abrir `res/values/strings.xml`
2. Buscar línea:
   ```xml
   <string name="app_name">My Application</string>
   ```
3. Cambiar a:
   ```xml
   <string name="app_name">Content Manager</string>
   ```
4. Guardar

### 8.4. Sincronizar Gradle

**Después de cambiar `build.gradle.kts`:**

1. Click en menú **≡** (arriba derecha)
2. **Build → Sync project with Gradle files**
3. Esperar a que termine (ver progreso en terminal)

### 8.5. Ver Terminal

**Panel inferior - Terminal:**

```bash
$ cd "/storage/emulated/0/AndroidIDEProjects/My Application"
$ ./gradlew tasks          # Ver tareas disponibles
$ ./gradlew clean          # Limpiar build
$ ./gradlew build          # Compilar proyecto
```

---

## 9. Compilar y Ejecutar

### 9.1. Compilar APK

**Opción 1: Desde menú AndroidIDE**

1. Click **≡** → **Build**
2. **Build → Build APK (debug)**
3. Esperar compilación (1-5 minutos)
4. APK generado en: `app/build/outputs/apk/debug/app-debug.apk`

**Opción 2: Desde terminal**

```bash
# Limpiar build anterior
./gradlew clean

# Compilar APK debug
./gradlew assembleDebug

# APK generado en:
# app/build/outputs/apk/debug/app-debug.apk
```

### 9.2. Instalar y Probar

**Método 1: Instalar desde archivo**

1. Abrir archivo `app-debug.apk` con File Manager
2. Permitir instalación de fuentes desconocidas (si es necesario)
3. Click "Instalar"
4. Abrir app desde cajón de aplicaciones

**Método 2: Instalar desde terminal**

```bash
# Instalar APK con adb
adb install app/build/outputs/apk/debug/app-debug.apk

# O reinstalar (sobrescribir)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 9.3. Ver Logs (Debugging)

**Terminal - Logcat:**

```bash
# Ver todos los logs
adb logcat

# Filtrar por tu app
adb logcat | grep com.example.myapplication

# Ver solo errores
adb logcat *:E

# Limpiar buffer
adb logcat -c
```

**Agregar logs en código Kotlin:**

```kotlin
import android.util.Log

class BooksFragment : Fragment() {
    private val TAG = "BooksFragment"

    private fun loadBooks() {
        Log.d(TAG, "Cargando libros...")
        val books = contentManager.bookDao.getAll()
        Log.d(TAG, "Libros cargados: ${books.size}")
        adapter.submitList(books)
    }
}
```

### 9.4. Errores Comunes y Soluciones

#### Error: "Permission denied" al ejecutar gradlew

```bash
# Solución: Dar permisos de ejecución
chmod +x gradlew
./gradlew --version
```

#### Error: Compilación falla con "Daemon"

```bash
# Solución: Limpiar cache de Gradle
rm -rf ~/.gradle/caches/
./gradlew clean --no-daemon
```

#### Error: APK no instala

```bash
# Solución 1: Desinstalar versión anterior
adb uninstall com.example.myapplication

# Solución 2: Reinstalar con -r
adb install -r app-debug.apk
```

#### Error: App crashea al abrir

```bash
# Ver stacktrace del error
adb logcat | grep AndroidRuntime

# Buscar línea "FATAL EXCEPTION"
# Copiar error completo para debugging
```

---

## 10. Próximos Pasos

### 10.1. Implementar CRUD Completo

**Objetivo:** Permitir agregar, editar y eliminar items desde la app.

#### Paso 1: Agregar FloatingActionButton (FAB)

**Editar `fragment_books.xml`:**

```xml
<androidx.constraintlayout.widget.ConstraintLayout>

    <androidx.recyclerview.widget.RecyclerView ... />
    <TextView android:id="@+id/empty_text_view" ... />

    <!-- NUEVO: Botón flotante para agregar -->
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab_add"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        android:src="@android:drawable/ic_input_add"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### Paso 2: Crear diálogo para agregar libro

**Crear `AddBookDialogFragment.kt`:**

```kotlin
package com.example.myapplication.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.app.AlertDialog
import android.widget.EditText
import com.example.myapplication.data.Book
import com.example.myapplication.data.BookStatus

class AddBookDialogFragment : DialogFragment() {

    private var listener: ((Book) -> Unit)? = null

    fun setOnBookAddedListener(l: (Book) -> Unit) {
        listener = l
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        // Inflar layout personalizado
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_book, null)

        // Referencias a campos
        val editTitulo = view.findViewById<EditText>(R.id.edit_titulo)
        val editAutor = view.findViewById<EditText>(R.id.edit_autor)
        val editPaginas = view.findViewById<EditText>(R.id.edit_paginas)

        builder.setView(view)
            .setTitle("Agregar Libro")
            .setPositiveButton("Agregar") { _, _ ->
                val titulo = editTitulo.text.toString()
                val autor = editAutor.text.toString().takeIf { it.isNotBlank() }
                val paginas = editPaginas.text.toString().toIntOrNull()

                if (titulo.isNotBlank()) {
                    val book = Book(
                        titulo = titulo,
                        autor = autor,
                        paginasTotales = paginas,
                        estado = BookStatus.REGISTRADO
                    )
                    listener?.invoke(book)
                }
            }
            .setNegativeButton("Cancelar", null)

        return builder.create()
    }
}
```

**Crear `res/layout/dialog_add_book.xml`:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <EditText
            android:id="@+id/edit_titulo"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Título *"
            android:inputType="text" />
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp">
        <EditText
            android:id="@+id/edit_autor"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Autor"
            android:inputType="text" />
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp">
        <EditText
            android:id="@+id/edit_paginas"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Páginas"
            android:inputType="number" />
    </com.google.android.material.textfield.TextInputLayout>

</LinearLayout>
```

#### Paso 3: Conectar diálogo en BooksFragment

```kotlin
class BooksFragment : Fragment() {
    // ... código existente ...

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ... código existente ...

        // NUEVO: Configurar FAB
        binding.fabAdd.setOnClickListener {
            showAddBookDialog()
        }
    }

    private fun showAddBookDialog() {
        val dialog = AddBookDialogFragment()
        dialog.setOnBookAddedListener { book ->
            // Insertar en BD
            contentManager.bookDao.insert(book)

            // Recargar lista
            loadBooks()

            // Mostrar mensaje
            Toast.makeText(
                requireContext(),
                "Libro agregado: ${book.titulo}",
                Toast.LENGTH_SHORT
            ).show()
        }
        dialog.show(childFragmentManager, "AddBookDialog")
    }
}
```

### 10.2. Importar Datos JSON

**Ver plantillas:**
- `books_template.json`
- `series_template.json`
- `movies_template.json`

**Crear `ImportHelper.kt`:**

```kotlin
package com.example.myapplication.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class ImportHelper(private val contentManager: ContentManager) {
    private val gson = Gson()

    fun importBooks(jsonFile: File): Int {
        val json = jsonFile.readText()
        val type = object : TypeToken<List<Book>>() {}.type
        val books: List<Book> = gson.fromJson(json, type)

        var count = 0
        books.forEach { book ->
            if (book.titulo.isNotBlank()) {  // Validar
                contentManager.bookDao.insert(book)
                count++
            }
        }
        return count
    }

    fun importSeries(jsonFile: File): Int {
        val json = jsonFile.readText()
        val type = object : TypeToken<List<Serie>>() {}.type
        val series: List<Serie> = gson.fromJson(json, type)

        var count = 0
        series.forEach { serie ->
            if (serie.titulo.isNotBlank()) {
                contentManager.serieDao.insert(serie)
                count++
            }
        }
        return count
    }

    fun importMovies(jsonFile: File): Int {
        val json = jsonFile.readText()
        val type = object : TypeToken<List<Movie>>() {}.type
        val movies: List<Movie> = gson.fromJson(json, type)

        var count = 0
        movies.forEach { movie ->
            if (movie.titulo.isNotBlank()) {
                contentManager.movieDao.insert(movie)
                count++
            }
        }
        return count
    }
}
```

### 10.3. Configurar Git y GitHub

**Ver sección "Control de Versiones con Git" en `estado_proyecto.md`**

**Comandos rápidos:**

```bash
cd "/storage/emulated/0/AndroidIDEProjects/My Application"

# Inicializar Git
git init
git add .
git commit -m "Initial commit"

# Subir a GitHub
git remote add origin https://github.com/TU_USUARIO/content-manager.git
git branch -M main
git push -u origin main
```

---

## 📚 Recursos Adicionales

### Documentación Oficial

- **Android Developers**: https://developer.android.com/
- **Kotlin**: https://kotlinlang.org/docs/home.html
- **Material Design**: https://material.io/develop/android
- **SQLite**: https://www.sqlite.org/docs.html

### Tutoriales Recomendados

- **RecyclerView**: https://developer.android.com/guide/topics/ui/layout/recyclerview
- **Fragments**: https://developer.android.com/guide/fragments
- **ViewBinding**: https://developer.android.com/topic/libraries/view-binding
- **SQLite Android**: https://developer.android.com/training/data-storage/sqlite

### Comunidad y Ayuda

- **Stack Overflow**: https://stackoverflow.com/questions/tagged/android
- **Reddit r/androiddev**: https://reddit.com/r/androiddev
- **AndroidIDE Discord**: https://discord.gg/androidide

---

## 🎓 Glosario de Términos

**Activity**: Pantalla individual de una app Android
**Fragment**: Porción reutilizable de UI dentro de una Activity
**ViewBinding**: Acceso seguro a vistas del XML desde código
**RecyclerView**: Lista eficiente de items scrollable
**Adapter**: Convierte datos a vistas para RecyclerView
**DAO**: Data Access Object, patrón para acceso a BD
**SQLite**: Motor de base de datos relacional embebido
**Cursor**: Apuntador a resultados de query SQL
**ContentValues**: Mapa de valores para INSERT/UPDATE
**Gradle**: Sistema de build de Android
**APK**: Android Package, archivo instalable
**ViewHolder**: Patrón para reciclar vistas en RecyclerView
**Enum**: Tipo enumerado con valores fijos
**Nullable**: Tipo que puede ser null (String?)
**Extension Function**: Función que extiende una clase
**Lambda**: Función anónima `{ param -> código }`

---

## ✅ Checklist de Comprensión

Después de leer este tutorial, deberías poder:

- [ ] Explicar qué hace cada capa (UI, Data, Database)
- [ ] Identificar el flujo de datos desde la BD hasta la UI
- [ ] Abrir y navegar el proyecto en AndroidIDE
- [ ] Compilar la app y generar APK
- [ ] Modificar el código de un fragmento o adapter
- [ ] Agregar un nuevo campo a Book y mostrarlo en UI
- [ ] Crear una nueva función en BookDao
- [ ] Insertar datos de ejemplo manualmente
- [ ] Leer logs de la app con adb logcat
- [ ] Explicar la diferencia entre Book, BookDao y BookAdapter

---

**Fin del Tutorial**

**Fecha de creación:** 21 de Noviembre de 2025
**Versión:** 1.0
**Autor:** Claude Code + Usuario

**Próxima actualización:** Después de implementar CRUD completo
