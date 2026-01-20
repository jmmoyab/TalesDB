# 🔑 Configurar API Keys para TalesDB v1.4.0

## ¿Qué son las API Keys?

Las API Keys son como "contraseñas" que permiten a TalesDB conectarse con Google Books y TMDB para buscar información de libros, películas y series automáticamente.

**IMPORTANTE:** Sin configurar las API Keys, la funcionalidad de búsqueda NO funcionará y mostrará un mensaje de error.

---

## 📚 Paso 1: Obtener Google Books API Key

### Tiempo estimado: 5-10 minutos

1. **Ir a Google Cloud Console**
   - Abrir: https://console.cloud.google.com

2. **Crear un proyecto nuevo**
   - Click en "Select a project" (arriba)
   - Click en "NEW PROJECT"
   - Nombre del proyecto: `TalesDB`
   - Click en "CREATE"

3. **Activar Google Books API**
   - En el menú lateral, ir a: "APIs & Services" → "Library"
   - Buscar: `Google Books API`
   - Click en "Google Books API"
   - Click en "ENABLE"

4. **Crear API Key**
   - Ir a: "APIs & Services" → "Credentials"
   - Click en "CREATE CREDENTIALS"
   - Seleccionar: "API Key"
   - Se generará tu API Key → **COPIARLA** (ej: `AIzaSyA1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p`)

5. **Restricciones (Opcional pero recomendado)**
   - Click en "RESTRICT KEY"
   - En "API restrictions":
     - Seleccionar "Restrict key"
     - Elegir solo "Google Books API"
   - Click en "SAVE"

**Límite gratuito:** 1,000 búsquedas/día (30,000/mes) - ¡Más que suficiente!

---

## 🎬 Paso 2: Obtener TMDB API Key

### Tiempo estimado: 5 minutos

1. **Registrarse en TMDB**
   - Ir a: https://www.themoviedb.org/signup
   - Completar formulario de registro
   - Verificar email

2. **Acceder a configuración de API**
   - Login en TMDB
   - Ir a tu perfil (arriba derecha)
   - Click en "Settings"
   - Click en "API" (en el menú lateral)

3. **Solicitar API Key**
   - Click en "Request an API Key"
   - Seleccionar: "Developer"
   - Aceptar términos de uso

4. **Completar formulario de aplicación**
   - Application Name: `TalesDB`
   - Application URL: `http://localhost` (o tu sitio si tienes)
   - Application Summary: `Personal media tracking app`
   - Click en "Submit"

5. **Copiar API Key**
   - Se mostrará tu API Key (v3 auth) → **COPIARLA** (ej: `a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6`)

**Límite gratuito:** 3,000,000 peticiones/mes - ¡Prácticamente ilimitado!

---

## ⚙️ Paso 3: Configurar las API Keys en TalesDB

### Ubicación del archivo:
```
app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt
```

### Instrucciones:

1. **Abrir el archivo `ApiConfig.kt`**

2. **Buscar las líneas:**
```kotlin
const val GOOGLE_BOOKS_KEY = "TU_GOOGLE_BOOKS_API_KEY_AQUI"
const val TMDB_KEY = "TU_TMDB_API_KEY_AQUI"
```

3. **Reemplazar con tus API Keys:**
```kotlin
const val GOOGLE_BOOKS_KEY = "AIzaSyA1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p"  // Tu key de Google Books
const val TMDB_KEY = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"  // Tu key de TMDB
```

4. **Guardar el archivo**

5. **Compilar la aplicación**
```bash
./gradlew assembleDebug
```

---

## ✅ Verificar que funciona

1. Abrir TalesDB
2. Click en + para agregar un libro/película/serie
3. Escribir "Harry Potter" en el campo de búsqueda
4. Click en "Buscar"

### ✅ Si funciona correctamente:
- Verás una lista de resultados
- Puedes seleccionar uno y el formulario se autocompletará

### ❌ Si hay error:
- Verificar que las API Keys estén correctamente copiadas
- Verificar que no haya espacios extra al inicio/final
- Verificar que las comillas (`"`) estén presentes
- Recompilar la aplicación

---

## 🔒 Seguridad de las API Keys

### ⚠️ IMPORTANTE:

1. **NO COMPARTIR** tus API Keys públicamente
2. **NO SUBIR** ApiConfig.kt a repositorios públicos de GitHub
3. Si subes a GitHub, agregar `ApiConfig.kt` al `.gitignore`

### Crear .gitignore:
```
# API Keys - NO SUBIR
app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt
```

---

## 💡 Solución de Problemas

### Error: "API Key de Google Books no configurada"
- Verificar que reemplazaste `TU_GOOGLE_BOOKS_API_KEY_AQUI` con tu key real
- Verificar que la key esté entre comillas: `"AIza..."`

### Error: "API Key de TMDB no configurada"
- Verificar que reemplazaste `TU_TMDB_API_KEY_AQUI` con tu key real
- Verificar que la key esté entre comillas: `"a1b2..."`

### Error: "403 - API Key inválida"
- La key puede estar mal copiada (falta/sobra algún carácter)
- Verificar en Google Cloud Console / TMDB que la key esté activa

### Error: "Límite excedido"
- Google Books: Esperaste a mañana (límite: 1,000/día)
- TMDB: Poco probable (límite: 3,000,000/mes)

---

## 📊 Resumen

### Costo Total: **$0 USD/mes**

### Límites Gratuitos:
- **Google Books:** 1,000 búsquedas/día
- **TMDB:** 3,000,000 peticiones/mes

### Tiempo de Configuración: **10-15 minutos** (una sola vez)

### ¿Vale la pena?
✅ **100% SÍ** - Ahorra 90% del tiempo al agregar contenido

---

## 🚀 Próximos Pasos

Una vez configuradas las API Keys:

1. Compilar la app
2. Probar búsqueda de libros
3. Probar búsqueda de películas
4. Probar búsqueda de series
5. ¡Disfrutar del autocompletado! 🎉

---

**Documentación creada:** 10 de Enero 2026
**Versión:** 1.4.0
**Estado:** Lista para usar

**¿Dudas?** Consulta este documento o revisa los comentarios en `ApiConfig.kt`
