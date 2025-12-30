# 🤖 APIs vs IA para Autocompletado en TalesDB

**Fecha:** 30 de Diciembre 2025
**Versión del documento:** 1.0
**Para:** TalesDB v1.4.0+ (funcionalidades futuras)

---

## 📑 Índice

1. [¿Qué es Google Books API?](#qué-es-google-books-api)
2. [APIs Disponibles](#apis-disponibles)
3. [IA (LLMs) Disponibles](#ia-llms-disponibles)
4. [Comparación: APIs vs IA](#comparación-apis-vs-ia)
5. [Casos de Uso](#casos-de-uso)
6. [Costos y Límites](#costos-y-límites)
7. [Escenario Real: 2,000 Usuarios](#escenario-real-2000-usuarios)
8. [Soluciones para Mantener Gratuidad](#soluciones-para-mantener-gratuidad)
9. [Implementación Recomendada](#implementación-recomendada)
10. [Plan de Desarrollo](#plan-de-desarrollo)

---

## 🎯 ¿Qué es Google Books API?

### Definición
Google Books API es una interfaz de programación que te permite acceder a la **base de datos gigante de Google de información de libros**.

### ¿Qué resuelve en TalesDB?

**Problema actual:**
```
Usuario quiere agregar "El Quijote"
→ Tiene que escribir manualmente:
   • Título: Don Quijote de la Mancha
   • Autor: Miguel de Cervantes
   • Páginas: 863
   • Año: 1605
   • Género: Novela clásica
```

**Con Google Books API:**
```
Usuario escribe: "quijote"
→ API muestra sugerencias:
   📖 Don Quijote de la Mancha
      Miguel de Cervantes • 863 páginas • 1605

   📖 El Quijote (edición ilustrada)
      Miguel de Cervantes • 1200 páginas • 2015

→ Usuario toca una opción
→ ¡Todos los datos se rellenan automáticamente! ✨
→ Solo ajusta: estado, fechas personales, notas
```

### Información que proporciona

- ✅ Título completo
- ✅ Autor(es)
- ✅ Número de páginas
- ✅ Año de publicación
- ✅ ISBN (código único del libro)
- ✅ Editorial
- ✅ Descripción/Sinopsis
- ✅ Categorías/Géneros
- ✅ Portada (imagen URL)
- ✅ Idioma
- ✅ Calificación promedio

---

## 📚 APIs Disponibles

### Para Libros

#### 1. **Google Books API** ⭐ Mejor opción
- **Costo:** Gratis (1,000 peticiones/día)
- **Base de datos:** Gigante
- **Calidad:** Muy alta
- **Portadas:** Sí, alta calidad
- **URL:** https://developers.google.com/books
- **Registro:** Google Cloud Console
- **Ejemplo:**
  ```
  GET https://www.googleapis.com/books/v1/volumes?q=harry+potter
  ```

#### 2. **Open Library API**
- **Costo:** Totalmente gratis, sin límites
- **Base de datos:** Enorme (20M+ libros)
- **Calidad:** Buena
- **Portadas:** Menos que Google
- **URL:** https://openlibrary.org/developers/api
- **Registro:** No requiere
- **Uso:** Perfecto como fallback

#### 3. **ISBN DB API**
- **Costo:** Freemium
- **Búsqueda:** Por código ISBN
- **Precisión:** Muy alta
- **Uso:** Para libros con ISBN conocido

---

### Para Películas/Series

#### 1. **TMDB (The Movie Database)** ⭐ Mejor opción
- **Costo:** Gratis (3M peticiones/mes)
- **Base de datos:** Completa y actualizada
- **Calidad:** Excelente
- **Contenido:** Posters, trailers, reparto, sinopsis
- **URL:** https://www.themoviedb.org/settings/api
- **Registro:** Cuenta TMDB (gratis)
- **Límites:** 40 peticiones/10 segundos

#### 2. **OMDb API** (Open Movie Database)
- **Costo:** 1,000 peticiones/día gratis
- **Base de datos:** Buena
- **Calidad:** Básica
- **Uso:** Alternativa más simple

#### 3. **TVMaze API**
- **Costo:** Gratis, sin límites
- **Especialidad:** Series de TV
- **Calidad:** Muy buena para series
- **Uso:** Complemento a TMDB

---

## 🤖 IA (LLMs) Disponibles

### 1. **Gemini API (Google)** ⭐ Mejor para empezar
- **Costo:** Gratis (60 peticiones/min)
- **Tier gratuito:** ~45,000 peticiones/mes
- **Registro:** No requiere tarjeta de crédito
- **Calidad:** Muy buena
- **Velocidad:** Rápida (2-4s)
- **URL:** https://ai.google.dev/

### 2. **ChatGPT API (OpenAI)**
- **Costo:** $0.50-$2.00 por 1M tokens
- **Equivalente:** ~100-200 consultas por $1
- **Calidad:** Excelente
- **Uso típico:** $0.011 por consulta
- **Recomendado:** Solo si necesitas lo mejor

### 3. **Claude API (Anthropic)**
- **Costo:** Similar a ChatGPT
- **Calidad:** Excelente con datos estructurados
- **Ventaja:** Mejor que ChatGPT en JSON/listas

### 4. **Modelos Open Source** (LLaMA, Mistral)
- **Costo:** Gratis (ejecutas localmente)
- **Problema:** Requiere mucho procesamiento
- **Viabilidad móvil:** Difícil

---

## ⚖️ Comparación: APIs vs IA

### Tabla Comparativa Completa

| Aspecto | APIs Especializadas | IA (LLMs) |
|---------|---------------------|-----------|
| **Precisión** | ✅✅✅ 100% | ⚠️ 90-95% |
| **Velocidad** | ✅✅✅ <1 segundo | ⚠️ 2-5 segundos |
| **Costo gratis** | ✅✅✅ Hasta miles usuarios | ⚠️ Limitado |
| **Portadas/Posters** | ✅ URLs directas | ❌ No puede generar |
| **Datos actuales** | ✅ Siempre actualizados | ⚠️ Cutoff date |
| **Libros raros** | ⚠️ Si no están, no hay | ✅ Puede inferir |
| **Formato respuesta** | ✅ JSON estructurado | ⚠️ Requiere parsing |
| **Internet** | ✅ Necesario | ✅ Necesario |
| **Límites/día gratis** | ✅ 1,000+ | ⚠️ 60-1,500 |
| **Base de datos** | ✅ Millones de títulos | ⚠️ Conocimiento general |

---

## 💡 Casos de Uso

### APIs Especializadas: Cuándo usarlas

✅ **Autocompletar datos conocidos:**
- Título, autor, año, páginas
- Portadas y posters
- Géneros estándar
- ISBNs, TMDBs
- Información estructurada

**Ejemplo:**
```
Usuario: "Harry Potter"
→ Google Books API
→ Resultado preciso en 0.5s
→ Todos los datos + portada
```

---

### IA (LLMs): Cuándo usarlas

✅ **Contenido creativo y análisis:**

#### 1. Recomendaciones Personalizadas
```
Usuario: "¿Qué debería leer después de Harry Potter?"

IA analiza tu biblioteca:
- Has leído: Harry Potter (7 libros)
- Géneros favoritos: Fantasía, Aventuras
- Edad: Juvenil

Recomienda:
• Percy Jackson (Rick Riordan)
• El nombre del viento (Patrick Rothfuss)
• Eragon (Christopher Paolini)
```

#### 2. Análisis de Estadísticas
```
Usuario: "Resume mi año de lectura"

IA genera:
"En 2025 leíste 24 libros (8,432 páginas).
Géneros favoritos: Ciencia Ficción 40%, Fantasía 35%.
Mes más activo: Agosto (6 libros).
Libro más largo: Don Quijote (863 págs).
Recomendación: Prueba más novela histórica."
```

#### 3. Sinopsis Personalizadas
```
Usuario toca libro "Dune"

IA genera:
"Dune es una epopeya de ciencia ficción sobre
política, religión y ecología en un planeta desértico.
Similar en complejidad a El Señor de los Anillos
pero en el espacio. ~450 páginas."
```

#### 4. Fallback para Libros Raros
```
Usuario: "El libro secreto de mi abuelo"
→ Google Books API: ❌ No encontrado
→ IA: "Probablemente edición local/autopublicada.
       ¿Tienes ISBN? Puedo ayudarte a buscar más info."
```

---

## 💰 Costos y Límites

### APIs Especializadas - Límites Gratuitos

#### Google Books API
```
Límite oficial: 1,000 peticiones/día
              = 30,000 peticiones/mes

Usuario promedio: 10 búsquedas/mes
Soporta gratis: 3,000 usuarios activos/mes

Costo si excedes: Requiere solicitar cuota aumentada
Estimado: $0.001-$0.005 por búsqueda extra
```

#### TMDB API
```
Límite oficial: 40 peticiones/10 segundos
              = 14,400/hora
              = 345,600/día
              = 10,368,000/mes

Usuario promedio: 5 búsquedas/mes
Soporta gratis: 2,000,000+ usuarios/mes

Conclusión: Prácticamente ilimitado para apps pequeñas/medianas
```

#### Open Library API
```
Límite oficial: No publicado (muy generoso)
Uso razonable: Sin problemas con miles de usuarios

Conclusión: Perfecto como fallback sin límites estrictos
```

---

### IA - Límites Gratuitos

#### Gemini API (Google) - Tier Gratuito 2025
```
Modelo pequeño: 60 peticiones/minuto
Modelo grande: 15 peticiones/minuto

Estimado mensual: ~45,000 peticiones/mes

Usuario promedio: 8 consultas IA/mes
Soporta gratis: 5,625 usuarios activos/mes

Conclusión: Muy generoso para apps personales
```

#### ChatGPT API - Tier de Pago
```
Sin tier gratuito (solo trial)

Costo por consulta típica:
- Entrada (200 tokens): $0.002
- Salida (300 tokens): $0.009
- Total: ~$0.011 por consulta

Usuario promedio: 8 consultas/mes = $0.088/mes
100 usuarios: $8.80/mes
1,000 usuarios: $88/mes
```

---

## 📊 Escenario Real: 2,000 Usuarios

### Escenario: Usuarios inventarían bibliotecas completas

#### Primer Mes (Inventario Masivo)

**Usuario promedio agrega:**
- 200 libros (de su casa)
- 50 películas
- 30 series
- **Total: 280 items**

**Con 2,000 usuarios:**
```
Google Books: 2,000 × 200 = 400,000 búsquedas
TMDB: 2,000 × 80 = 160,000 búsquedas
Gemini IA: 2,000 × 5 = 10,000 consultas (recomendaciones)
```

---

### Límites vs Uso Real

#### Google Books API
```
Límite gratuito: 30,000/mes
Uso real: 400,000/mes
Diferencia: EXCEDE por 370,000 ❌

Problema: Se bloquea después de 1,000/día
Solo ~30 de 2,000 usuarios podrían inventariar

Costo estimado (sin optimizar):
370,000 búsquedas × $0.002 = $740/mes
```

#### TMDB API
```
Límite gratuito: 10,368,000/mes
Uso real: 160,000/mes
Diferencia: Muy dentro del límite ✅

Conclusión: Sin problemas, totalmente gratis
```

#### Gemini API
```
Límite gratuito: 45,000/mes
Uso real: 10,000/mes
Diferencia: Dentro del límite ✅

Conclusión: Totalmente gratis
```

---

### Resumen de Costos (SIN optimizar)

```
Primer mes (inventario masivo):
- Google Books: $740 (excede límite)
- TMDB: $0 (gratis)
- Gemini: $0 (gratis)
────────────────────
TOTAL: $740

Meses siguientes (uso normal):
- Búsquedas: 2,000 × 10 = 20,000/mes
- Límite Google Books: 30,000/mes
- Todo gratis ✅
────────────────────
TOTAL: $0/mes
```

---

## 🎯 Soluciones para Mantener Gratuidad

### Solución 1: Caché Local + Distribución de Carga ⭐ MEJOR

#### Concepto
Guardar búsquedas populares localmente para reutilizar entre usuarios.

#### Implementación
```kotlin
class SmartBookSearch {
    private val cache = LocalCache() // SQLite local

    suspend fun buscar(titulo: String): BookData {
        // 1. Buscar en caché primero
        cache.get(titulo)?.let { return it }

        // 2. Si no existe, llamar API
        val resultado = googleBooksAPI.buscar(titulo)

        // 3. Guardar para futuros usuarios
        cache.put(titulo, resultado)

        return resultado
    }
}
```

#### Impacto
```
"Harry Potter y la piedra filosofal"
→ Primera búsqueda: Llama API (1 petición)
→ Siguientes 100 usuarios: Usan caché (0 peticiones)
→ Ahorro: 99 llamadas API

Libros populares (80% de casos):
400,000 búsquedas → Se reducen a ~80,000
= DENTRO del límite gratuito ✅
```

#### Resultado
**Costo: $0/mes** 🎉

---

### Solución 2: Open Library API como Fallback

#### Concepto
Si Google Books se agota, usar Open Library (sin límites).

#### Implementación
```kotlin
suspend fun buscar(titulo: String): BookData {
    try {
        // Intentar Google Books primero (mejor calidad)
        return googleBooksAPI.buscar(titulo)
    } catch (QuotaExceededException) {
        // Fallback a Open Library (gratis, sin límites)
        return openLibraryAPI.buscar(titulo)
    }
}
```

#### Ventajas
- ✅ Open Library: Gratis, sin límites estrictos
- ✅ Base de datos: 20M+ libros
- ⚠️ Menos portadas que Google Books

#### Resultado
**Costo: $0/mes** 🎉

---

### Solución 3: Límite Temporal por Usuario (Beta)

#### Concepto
Durante los primeros meses, limitar búsquedas por usuario.

#### Implementación
```kotlin
Usuario nuevo:
- Primeras 50 búsquedas/mes: Gratis con API
- Búsquedas 51+: Entrada manual o esperar próximo mes

Banner en app:
"Estás en periodo beta. Límite: 50 búsquedas/mes.
Se aumentará en futuras versiones."
```

#### Ventajas
- ✅ Controlas costos totalmente
- ✅ Usuarios entienden (beta)
- ✅ Puedes aumentar después

#### Resultado
**Costo: $0/mes** 🎉

---

### Solución 4: Modelo Freemium (Monetización)

#### Versión Gratis
```
- 20 búsquedas API/mes
- Entrada manual ilimitada
- Todas las demás funcionalidades
```

#### Versión Premium ($0.99/mes)
```
- Búsquedas API ilimitadas
- Recomendaciones IA avanzadas
- Sincronización nube (futuro)
- Sin anuncios
```

#### Proyección Financiera
```
2,000 usuarios:
- 20% pagan premium = 400 usuarios
- Ingresos: 400 × $0.99 = $396/mes

Costos:
- Google Books extra: $200/mes
- Gemini premium: $50/mes
- Google Play fee (30%): -$119/mes
────────────────────
Ganancia neta: $27/mes

(O reinvertir en más features)
```

#### Resultado
**Rentable + Sostenible** 💰

---

## 🚀 Implementación Recomendada

### Solución Híbrida ÓPTIMA (Costo $0)

```kotlin
class SmartSearchService {
    private val cache = LocalBookCache()
    private val googleBooks = GoogleBooksAPI()
    private val openLibrary = OpenLibraryAPI()
    private val tmdb = TMDBAPI()

    suspend fun buscarLibro(titulo: String): BookSearchResult {
        // 1. CACHÉ LOCAL (instantáneo, 0 peticiones)
        cache.getBook(titulo)?.let {
            return BookSearchResult(
                datos = it,
                fuente = "Caché Local",
                velocidad = "Instantáneo"
            )
        }

        // 2. GOOGLE BOOKS API (mejor calidad)
        try {
            val resultado = googleBooks.search(titulo)
            cache.putBook(titulo, resultado) // Guardar para otros

            return BookSearchResult(
                datos = resultado,
                fuente = "Google Books",
                velocidad = "0.5s"
            )
        } catch (QuotaExceededException) {
            // 3. FALLBACK: OPEN LIBRARY (gratis, sin límites)
            val resultado = openLibrary.search(titulo)
            cache.putBook(titulo, resultado)

            return BookSearchResult(
                datos = resultado,
                fuente = "Open Library",
                velocidad = "1s",
                nota = "Google Books temporalmente agotado"
            )
        }
    }

    suspend fun buscarPelicula(titulo: String): MovieSearchResult {
        // TMDB tiene límites muy generosos, no necesita caché urgente
        cache.getMovie(titulo)?.let {
            return MovieSearchResult(it, "Caché")
        }

        val resultado = tmdb.search(titulo)
        cache.putMovie(titulo, resultado)

        return MovieSearchResult(resultado, "TMDB")
    }
}
```

### Flujo de Usuario

```
Usuario agrega "Harry Potter":

1. App busca en caché local
   └─ ❌ No encontrado (primera vez)

2. App llama Google Books API
   └─ ✅ Encontrado (0.5s)
   └─ Guarda en caché

3. Muestra resultados:
   📖 Harry Potter y la piedra filosofal
      J.K. Rowling • 264 págs • 1997 • Fantasía
      [Portada]

4. Usuario toca → Formulario autocompletado ✅

────────────────────

Siguiente usuario agrega "Harry Potter":

1. App busca en caché local
   └─ ✅ Encontrado (instantáneo)

2. Muestra resultados (sin llamar API)
   📖 Harry Potter y la piedra filosofal
      J.K. Rowling • 264 págs • 1997 • Fantasía
      [Portada]

3. Usuario toca → Formulario autocompletado ✅

────────────────────
Ahorro: 1 llamada API
```

---

### Ventajas de la Solución Híbrida

- ✅ **80% de búsquedas:** Caché local (0 peticiones API)
- ✅ **15% de búsquedas:** Google Books (mejor calidad)
- ✅ **5% de búsquedas:** Open Library (si Google se agota)
- ✅ **Costo total:** $0/mes
- ✅ **Escalabilidad:** Soporta miles de usuarios
- ✅ **Experiencia:** Rápida y fluida
- ✅ **Confiabilidad:** Múltiples fallbacks

---

## 📈 Proyección de Costos por Escala

### Con Solución Híbrida (Caché + Fallback)

| Usuarios | Búsquedas/mes | Google Books | Open Library | TMDB | Gemini | **TOTAL** |
|----------|---------------|--------------|--------------|------|--------|-----------|
| 100 | 10,000 | $0 | $0 | $0 | $0 | **$0** |
| 500 | 50,000 | $0 | $0 | $0 | $0 | **$0** |
| 1,000 | 100,000 | $0 | $0 | $0 | $0 | **$0** |
| 2,000 | 200,000 | $0 | $0 | $0 | $0 | **$0** |
| 5,000 | 500,000 | $0 | $0 | $0 | $0 | **$0** |
| 10,000 | 1,000,000 | $0* | $0 | $0 | $0 | **$0** |

*Gracias a caché local que reduce 80% de peticiones reales

---

### Cuándo SÍ Tendrías que Pagar

Solo pagarías en estos casos OPCIONALES:

#### 1. Sin implementar caché ni fallback
```
Solo Google Books, sin optimizaciones:
2,000 usuarios × 200 búsquedas = 400,000/mes
Límite gratuito: 30,000/mes
Costo: ~$740 primer mes
```

#### 2. Usar ChatGPT en lugar de Gemini
```
10,000 consultas IA/mes × $0.011 = $110/mes
vs
Gemini gratis: $0/mes
```

#### 3. Servidor propio para sincronización nube (futuro)
```
Hosting: $20-50/mes
Base de datos: $10-30/mes
Total: $30-80/mes
```

**PERO ninguna de estas es necesaria para v1.4.0**

---

## 📅 Plan de Desarrollo

### **Fase 1: v1.4.0 - APIs Básicas (4-5 horas)**

#### Tareas
1. Registrarse en Google Books API (30 min)
2. Registrarse en TMDB API (30 min)
3. Crear `BookSearchService` (2 horas)
4. Crear `MovieSearchService` (1 hora)
5. Implementar UI de búsqueda (1.5 horas)
6. Testing en dispositivos (30 min)

#### Resultado
- ✅ Autocompletar libros con Google Books
- ✅ Autocompletar películas/series con TMDB
- ✅ 100% gratis
- ✅ Cubre 90% de títulos populares

---

### **Fase 2: v1.4.1 - Caché Local (2 horas)**

#### Tareas
1. Crear tabla SQLite para caché (30 min)
2. Implementar `LocalBookCache` (1 hora)
3. Integrar caché en búsquedas (30 min)

#### Resultado
- ✅ Búsquedas instantáneas (libros populares)
- ✅ Reduce 80% de llamadas API
- ✅ Mejor experiencia de usuario

---

### **Fase 3: v1.4.2 - Fallback Open Library (1 hora)**

#### Tareas
1. Integrar Open Library API (45 min)
2. Implementar lógica de fallback (15 min)

#### Resultado
- ✅ Backup si Google Books se agota
- ✅ Sin límites estrictos
- ✅ Mayor confiabilidad

---

### **Fase 4: v1.5.0 - IA Básica con Gemini (6-8 horas)**

#### Tareas
1. Registrarse en Gemini API (30 min)
2. Crear `AIRecommendationService` (3 horas)
3. Implementar análisis de biblioteca (2 horas)
4. UI para recomendaciones (2 horas)
5. Testing (30 min)

#### Resultado
- ✅ Recomendaciones personalizadas
- ✅ "¿Qué debería leer después de X?"
- ✅ Análisis de estadísticas con IA
- ✅ 100% gratis (Gemini tier gratuito)

---

### **Fase 5: v1.6.0 - Sistema Híbrido Completo (2 horas)**

#### Tareas
1. Combinar APIs + IA inteligentemente (1 hora)
2. Optimizaciones finales (30 min)
3. Testing completo (30 min)

#### Resultado
- ✅ Mejor de ambos mundos
- ✅ APIs para datos
- ✅ IA para inteligencia
- ✅ Sistema robusto y escalable

---

## 🎯 Resumen Ejecutivo

### ¿APIs o IA?

**Respuesta: AMBAS, usadas correctamente**

### Para Datos Estructurados (Autocompletar)
**Usa APIs Especializadas:**
- ✅ Google Books (libros)
- ✅ TMDB (películas/series)
- ✅ Open Library (fallback)
- **Razón:** Más rápido, preciso, gratis

### Para Funcionalidades Inteligentes
**Usa IA (Gemini):**
- ✅ Recomendaciones
- ✅ Análisis de biblioteca
- ✅ Sinopsis personalizadas
- **Razón:** Flexible, creativo, gratis con límites generosos

### Costos con Implementación Inteligente

```
2,000 usuarios activos:
- Con caché + fallback: $0/mes
- Con Gemini (no ChatGPT): $0/mes
- Con TMDB: $0/mes

Total: $0/mes ✅

Límite realista GRATIS: 5,000-10,000 usuarios
```

### Recomendación Final

1. **v1.4.0:** APIs básicas (4-5 horas)
2. **v1.4.1:** Agregar caché (2 horas)
3. **v1.4.2:** Agregar fallback (1 hora)
4. **v1.5.0:** Agregar IA Gemini (6-8 horas)

**Total de desarrollo: ~15-20 horas**
**Total de costo mensual: $0**
**Valor para usuarios: Enorme** 🚀

---

## 📚 Enlaces de Referencia

### APIs

**Google Books:**
- Documentación: https://developers.google.com/books
- Console: https://console.cloud.google.com
- Ejemplo: https://developers.google.com/books/docs/v1/using

**TMDB:**
- Sitio: https://www.themoviedb.org/
- API Docs: https://developer.themoviedb.org/docs
- Registro: https://www.themoviedb.org/settings/api

**Open Library:**
- Sitio: https://openlibrary.org/
- API Docs: https://openlibrary.org/developers/api
- Búsqueda: https://openlibrary.org/dev/docs/api/search

### IA

**Gemini:**
- Sitio: https://ai.google.dev/
- Docs: https://ai.google.dev/docs
- Pricing: https://ai.google.dev/pricing

**ChatGPT:**
- Sitio: https://platform.openai.com/
- Docs: https://platform.openai.com/docs
- Pricing: https://openai.com/pricing

---

**Documento creado:** 30 de Diciembre 2025
**Próxima revisión:** Cuando se implemente v1.4.0
**Estado:** Listo para implementación

**¿Dudas? Consulta este documento antes de empezar** 📖
