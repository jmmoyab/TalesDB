# Instrucciones para Crear GitHub Release v1.4.0

## Paso a Paso

### 1. Ir a la página de Releases
1. Abre tu navegador
2. Ve a: https://github.com/jmmoyab/TalesDB
3. Click en **"Releases"** (lado derecho, debajo de "About")
4. Click en **"Create a new release"** (botón verde)

### 2. Configurar el Release

**Tag version:**
```
v1.4.0
```
- Escribe `v1.4.0` en el campo
- Click en "Create new tag: v1.4.0 on publish"

**Target:**
- Deja: `feature/autocompletado-v1.4.0` (branch actual)

**Release title:**
```
TalesDB v1.4.0 - Autocompletado con APIs
```

### 3. Descripción del Release

Copia y pega este texto en el campo "Describe this release":

```markdown
## Novedades principales

### ✨ Autocompletado Inteligente
- 🔍 Búsqueda automática de libros usando **Google Books API**
- 🎬 Búsqueda automática de películas y series usando **TMDB API**
- ⚡ Autocompletado de título, autor/director, año, género
- 📊 Duración de películas y número de episodios de series

### 🔧 Mejoras
- Interfaz optimizada para búsqueda
- Mensajes de error más claros
- Validación de API Keys configuradas
- Corrección del botón "Salir" en el menú

## 📥 Instalación

1. Descarga el archivo **TalesDB-v1.4.0.apk** (abajo)
2. Habilita "Orígenes desconocidos" en tu dispositivo Android
3. Instala el APK
4. Ver [COMO_INSTALAR.md](COMO_INSTALAR.md) para instrucciones detalladas

## 📋 Requisitos

- Android 5.0 (Lollipop) o superior
- Conexión a Internet (solo para búsqueda automática)

## ⚠️ Nota importante

Esta versión requiere configurar API Keys propias para usar la búsqueda automática:
- Google Books API Key (gratuita)
- TMDB API Key (gratuita)

👉 Ver [CONFIGURAR_API_KEYS.md](CONFIGURAR_API_KEYS.md) para obtener las keys.

## 📝 Cambios desde v1.3.0

- ✅ Integración completa Google Books API
- ✅ Integración completa TMDB API
- ✅ Autocompletado con duración y episodios
- ✅ Fix botón salir en menú
- ✅ Mejoras en mensajes de error
- ✅ Validación de configuración de APIs

## 🐛 Problemas conocidos

Ninguno reportado.

## 🚀 Próximas versiones

**v1.4.1** - Caché de búsquedas, tutorial de primera vez
**v1.5.0** - Backup automático SQLite, modo oscuro

---

**¿Preguntas o problemas?**
- 📢 Reporta issues en: https://github.com/jmmoyab/TalesDB/issues
```

### 4. Subir el APK

1. En la sección **"Attach binaries"** (al final)
2. Click en el área o arrastra el archivo
3. Busca y selecciona: **TalesDB-v1.4.0.apk**
   - Ubicación: `/storage/emulated/0/AndroidIDEProjects/My Application/TalesDB-v1.4.0.apk`
4. Espera a que se suba completamente (5.4 MB)

### 5. Publicar

1. ✅ Marca **"Set as the latest release"**
2. ❌ NO marques "Set as a pre-release" (déjalo desmarcado)
3. Click en **"Publish release"** (botón verde grande)

## ¡Listo!

Una vez publicado:
- El APK estará disponible en: https://github.com/jmmoyab/TalesDB/releases
- Link directo de descarga: https://github.com/jmmoyab/TalesDB/releases/download/v1.4.0/TalesDB-v1.4.0.apk

## Para compartir con familia/amigos

Puedes compartir cualquiera de estos links:

**Opción 1 - Página del release:**
```
https://github.com/jmmoyab/TalesDB/releases/latest
```

**Opción 2 - Descarga directa del APK:**
```
https://github.com/jmmoyab/TalesDB/releases/download/v1.4.0/TalesDB-v1.4.0.apk
```

**Opción 3 - Mensaje para WhatsApp:**
```
Hola! Te comparto mi app TalesDB v1.4.0 para organizar libros, películas y series.

📱 Descarga aquí:
https://github.com/jmmoyab/TalesDB/releases/latest

📖 Instrucciones de instalación:
https://github.com/jmmoyab/TalesDB/blob/feature/autocompletado-v1.4.0/COMO_INSTALAR.md

Es gratis, sin anuncios y tus datos quedan en tu teléfono.
```

---

**Si tienes problemas, avísame y te ayudo.**
