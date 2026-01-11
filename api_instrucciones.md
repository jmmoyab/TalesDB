PARTE 1: Google Books API Key (5-7 minutos)

  Te voy a guiar paso a paso:

  1. Ir a Google Cloud Console
  URL: https://console.cloud.google.com

  2. Crear un proyecto nuevo
  - En la parte superior, click en "Select a project"
  - Click en "NEW PROJECT"
  - Nombre del proyecto: TalesDB
  - Click en "CREATE"
  - Espera unos segundos a que se cree

  3. Activar Google Books API
  - En el menú lateral izquierdo (☰), ir a: "APIs & Services" → "Library"
  - En el buscador, escribir: Google Books API
  - Click en "Google Books API"
  - Click en el botón azul "ENABLE"
  - Espera unos segundos

  4. Crear la API Key
  - En el menú lateral, ir a: "APIs & Services" → "Credentials"
  - Click en el botón "+ CREATE CREDENTIALS" (arriba)
  - Seleccionar: "API Key"
  - ¡COPIAR la key que aparece! (algo como: AIzaSyA1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p)

  5. (Opcional) Restringir la key
  - Click en "RESTRICT KEY"
  - En "API restrictions" → Seleccionar "Restrict key"
  - Marcar solo: "Google Books API"
  - Click en "SAVE"

  ✅ LISTO - Ya tienes tu Google Books API Key

  ---
  PARTE 2: TMDB API Key (3-5 minutos)

  1. Registrarse en TMDB
  URL: https://www.themoviedb.org/signup
  - Completar el formulario de registro
  - Verificar tu email

  2. Iniciar sesión
  - Login con tu cuenta recién creada

  3. Ir a configuración de API
  - Click en tu foto de perfil (arriba derecha)
  - Click en "Settings"
  - En el menú lateral, click en "API"

  4. Solicitar API Key
  - Click en "Request an API Key"
  - Seleccionar: "Developer"
  - Aceptar los términos de uso

  5. Completar el formulario
  - Application Name: TalesDB
  - Application URL: http://localhost (o deja en blanco)
  - Application Summary: Personal media tracking app
  - Click en "Submit"

  6. Copiar tu API Key
  - Se mostrará tu API Key (v3 auth)                                - ¡COPIAR esta key! (algo como: a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6)

  ✅ LISTO - Ya tienes tu TMDB API Key

  ---
  ⚙️ PARTE 3: Configurar las Keys en el Proyecto

  Ahora vamos a poner las keys en el código:

  1. Abrir el archivo ApiConfig.kt
  Ruta: app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt                                                             2. Buscar estas líneas (están cerca de la línea 25):
  const val GOOGLE_BOOKS_KEY = "TU_GOOGLE_BOOKS_API_KEY_AQUI"
  const val TMDB_KEY = "TU_TMDB_API_KEY_AQUI"

  3. Reemplazar con tus keys:
  const val GOOGLE_BOOKS_KEY = "AIzaSyA1b2c3d4e5f6g7h8i9j0"  // ← Pega tu key de Google Books aquí
  const val TMDB_KEY = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"  // ← Pega tu key de TMDB aquí

  4. Guardar el archivo

  ✅ ¡LISTO! Ya está configurado                                    ---
  🚀 Ahora sí, compilar y probar:

  ./gradlew assembleDebug

  Instala el APK y prueba:
  - Agregar libro → buscar "Harry Potter"
  - Debería aparecer lista de resultados
  - Selecciona uno y el formulario se autocompleta
