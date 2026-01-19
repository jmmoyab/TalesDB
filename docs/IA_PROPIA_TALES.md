# Implementar IA Propia para TalesDB

## Objetivo

Reemplazar las APIs de TMDB y Google Books por un sistema de IA propio que busque en internet, permitiendo **uso comercial sin restricciones**.

---

## Arquitectura Propuesta

```
┌─────────────────┐
│   TalesDB App   │
│    (Android)    │
└────────┬────────┘
         │ HTTP Request
         ▼
┌─────────────────┐      ┌─────────────────┐
│  Tu Backend     │ ──── │    Tavily       │
│  (opcional)     │      │  (búsqueda web) │
└────────┬────────┘      └─────────────────┘
         │
         ▼
┌─────────────────┐
│    Groq API     │
│ (Llama - extrae │
│  datos JSON)    │
└─────────────────┘
```

**Dos opciones:**
- **Con backend**: Más seguro (API keys en servidor)
- **Sin backend**: Llamadas directas desde la app (más simple)

---

## APIs Necesarias

| Servicio | URL | Plan Gratuito |
|----------|-----|---------------|
| **Tavily** | https://tavily.com | 1,000 búsquedas/mes |
| **Groq** | https://console.groq.com | Muy generoso (sin límite mensual claro) |

---

## Flujo de Búsqueda

### Paso 1: Buscar con Tavily

```bash
curl -X POST https://api.tavily.com/search \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer tvly-TU_API_KEY" \
  -d '{
    "query": "El Padrino 1972 película director duración reparto",
    "search_depth": "basic",
    "max_results": 3,
    "include_answer": true
  }'
```

**Respuesta:** Texto con información de varias fuentes web.

### Paso 2: Extraer datos con Groq (Llama)

```bash
curl https://api.groq.com/openai/v1/chat/completions \
  -H "Authorization: Bearer $GROQ_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama-3.3-70b-versatile",
    "messages": [
      {
        "role": "system",
        "content": "Extrae información de películas del texto proporcionado. Responde SOLO con JSON válido."
      },
      {
        "role": "user",
        "content": "Texto de Tavily: [resultado de búsqueda aquí]. Extrae: título, año, director, duración en minutos, género, sinopsis breve."
      }
    ],
    "response_format": {
      "type": "json_schema",
      "json_schema": {
        "name": "movie_data",
        "strict": true,
        "schema": {
          "type": "object",
          "properties": {
            "titulo": {"type": "string"},
            "año": {"type": "integer"},
            "director": {"type": "string"},
            "duracion_minutos": {"type": "integer"},
            "genero": {"type": "string"},
            "sinopsis": {"type": "string"}
          },
          "required": ["titulo", "año", "director", "duracion_minutos", "genero", "sinopsis"],
          "additionalProperties": false
        }
      }
    }
  }'
```

**Respuesta ejemplo:**
```json
{
  "titulo": "El Padrino",
  "año": 1972,
  "director": "Francis Ford Coppola",
  "duracion_minutos": 175,
  "genero": "Drama, Crimen",
  "sinopsis": "La historia de la familia mafiosa Corleone..."
}
```

---

## Código Kotlin para Android

### Data Classes

```kotlin
// Respuesta de Tavily
data class TavilyResponse(
    val answer: String?,
    val results: List<TavilyResult>
)

data class TavilyResult(
    val title: String,
    val content: String,
    val url: String
)

// Datos extraídos de película
data class MovieExtracted(
    val titulo: String,
    val año: Int,
    val director: String,
    val duracion_minutos: Int,
    val genero: String,
    val sinopsis: String
)

// Datos extraídos de libro
data class BookExtracted(
    val titulo: String,
    val autor: String,
    val año: Int?,
    val paginas: Int?,
    val genero: String,
    val sinopsis: String
)

// Datos extraídos de serie
data class SerieExtracted(
    val titulo: String,
    val año: Int,
    val creador: String,
    val temporadas: Int,
    val genero: String,
    val sinopsis: String
)
```

### API Interfaces

```kotlin
// TavilyAPI.kt
interface TavilyAPI {
    @POST("search")
    suspend fun search(
        @Header("Authorization") auth: String,
        @Body request: TavilyRequest
    ): TavilyResponse
}

data class TavilyRequest(
    val query: String,
    val search_depth: String = "basic",
    val max_results: Int = 3,
    val include_answer: Boolean = true
)

// GroqAPI.kt
interface GroqAPI {
    @POST("chat/completions")
    suspend fun complete(
        @Header("Authorization") auth: String,
        @Body request: GroqRequest
    ): GroqResponse
}
```

### Servicio de Búsqueda IA

```kotlin
class AISearchService(
    private val tavilyApi: TavilyAPI,
    private val groqApi: GroqAPI
) {
    private val tavilyKey = "Bearer tvly-TU_KEY"
    private val groqKey = "Bearer gsk_TU_KEY"

    suspend fun searchMovie(query: String): MovieExtracted? {
        // 1. Buscar en web con Tavily
        val searchResult = tavilyApi.search(
            tavilyKey,
            TavilyRequest(
                query = "$query película año director duración",
                max_results = 3
            )
        )

        // 2. Combinar resultados
        val context = buildString {
            searchResult.answer?.let { append("Resumen: $it\n\n") }
            searchResult.results.forEach {
                append("${it.title}: ${it.content}\n\n")
            }
        }

        // 3. Extraer con Groq/Llama
        val extraction = groqApi.complete(
            groqKey,
            GroqRequest(
                model = "llama-3.3-70b-versatile",
                messages = listOf(
                    Message("system", MOVIE_EXTRACTION_PROMPT),
                    Message("user", "Extrae datos de: $context")
                ),
                response_format = jsonSchemaMovie()
            )
        )

        // 4. Parsear JSON
        return parseMovieJson(extraction.choices[0].message.content)
    }

    companion object {
        const val MOVIE_EXTRACTION_PROMPT = """
            Eres un extractor de datos de películas.
            Del texto proporcionado, extrae:
            - titulo: nombre de la película
            - año: año de estreno (número)
            - director: nombre del director
            - duracion_minutos: duración en minutos (número)
            - genero: géneros separados por coma
            - sinopsis: resumen breve (máx 200 caracteres)

            Si no encuentras algún dato, usa null.
            Responde SOLO con JSON válido.
        """

        const val BOOK_EXTRACTION_PROMPT = """
            Eres un extractor de datos de libros.
            Del texto proporcionado, extrae:
            - titulo: nombre del libro
            - autor: nombre del autor
            - año: año de publicación (número)
            - paginas: número de páginas (número)
            - genero: géneros separados por coma
            - sinopsis: resumen breve (máx 200 caracteres)

            Si no encuentras algún dato, usa null.
            Responde SOLO con JSON válido.
        """

        const val SERIE_EXTRACTION_PROMPT = """
            Eres un extractor de datos de series de TV.
            Del texto proporcionado, extrae:
            - titulo: nombre de la serie
            - año: año de estreno (número)
            - creador: nombre del creador/showrunner
            - temporadas: número de temporadas (número)
            - genero: géneros separados por coma
            - sinopsis: resumen breve (máx 200 caracteres)

            Si no encuentras algún dato, usa null.
            Responde SOLO con JSON válido.
        """
    }
}
```

---

## Prompts de Búsqueda Optimizados

### Para Películas
```
"{título}" película ficha técnica año director duración reparto sinopsis
```

### Para Series
```
"{título}" serie TV temporadas episodios año creador reparto sinopsis
```

### Para Libros
```
"{título}" libro autor editorial año páginas ISBN sinopsis reseña
```

---

## Costos Estimados

| Uso mensual | Tavily | Groq | Total |
|-------------|--------|------|-------|
| 100 búsquedas | Gratis | Gratis | **$0** |
| 500 búsquedas | Gratis | Gratis | **$0** |
| 1,000 búsquedas | Gratis | Gratis | **$0** |
| 2,000 búsquedas | ~$8 | Gratis | **~$8** |
| 5,000 búsquedas | ~$32 | Gratis | **~$32** |

**Nota:** Groq tiene límites de rate-limit (requests/minuto), no de cantidad mensual.

---

## Plan de Implementación

| Fase | Tarea | Estimación |
|------|-------|------------|
| 1 | Crear cuentas Tavily + Groq | 15 min |
| 2 | Crear `AISearchService.kt` | 2-3 horas |
| 3 | Integrar en formularios existentes | 2-3 horas |
| 4 | Testing y ajuste de prompts | 1-2 horas |
| 5 | UI para elegir fuente (API actual vs IA) | 1 hora |

---

## Ventajas vs TMDB/Google Books

| Aspecto | APIs actuales | IA propia |
|---------|---------------|-----------|
| Uso comercial | Restringido | **Libre** |
| Cobertura | Solo su BD | **Todo internet** |
| Dependencia | Sus términos | **Tú controlas** |
| Costo comercial | $149+/mes | **~$0-30/mes** |
| Datos en español | Variable | **Busca en español** |
| Mantenimiento | Ellos actualizan | Tú ajustas prompts |

---

## Consideraciones Legales

- **Tavily/Groq**: Permiten uso comercial
- **Datos extraídos**: Son datos públicos transformados por IA
- **No scraping directo**: La IA resume/transforma, no copia
- **Atribución**: No requerida (a diferencia de TMDB)

---

## Alternativas Investigadas

| Fuente | Uso Comercial | Viabilidad |
|--------|---------------|------------|
| TMDB | $149/mes | Viable si hay ingresos |
| Google Books | Prohibido sin acuerdo | Descartado |
| Filmaffinity (scraping) | Riesgo legal | No recomendado |
| Open Library | Solo no comercial | Descartado |
| **Tavily + Groq** | **Permitido** | **Recomendado** |

---

## Referencias

- [Tavily Search API Docs](https://docs.tavily.com/documentation/api-reference/endpoint/search)
- [Groq Structured Outputs](https://console.groq.com/docs/structured-outputs)
- [Tavily Python SDK](https://github.com/tavily-ai/tavily-python)
- [Groq API Reference](https://console.groq.com/docs/api-reference)

---

**Creado:** 16 Enero 2026
**Estado:** Investigación completada, pendiente implementación
**Prioridad:** Media (para v1.5.0 o v2.0.0)
