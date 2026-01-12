# Plan de Distribución Mixta - TalesDB v1.4.0

## Estrategia: Dos versiones para dos audiencias

---

## Versión 1: Pública (GitHub Release) 🌐

### APK: TalesDB-v1.4.0-public.apk
- ❌ **SIN** tus API Keys
- ✅ Código abierto y profesional
- ✅ Sin riesgo de abuso de límites

### Audiencia:
- Desarrolladores
- Comunidad open source
- Desconocidos que encuentren el proyecto
- Portfolio profesional

### Cómo funciona:
1. Usuario descarga el APK desde GitHub
2. Usuario instala la app
3. Usuario sigue CONFIGURAR_API_KEYS.md
4. Usuario configura **sus propias** API Keys gratuitas
5. App funciona con las keys del usuario

### Ventajas:
- ✅ Sin límite de descargas
- ✅ Cada usuario usa su propia cuota de API
- ✅ Profesional para tu portfolio
- ✅ Comunidad puede contribuir al código

### Desventajas:
- ❌ Más técnico (no para todos)
- ❌ Usuario debe obtener API Keys (15-20 min)

---

## Versión 2: Privada (Google Drive) 👨‍👩‍👧‍👦

### APK: TalesDB-v1.4.0.apk (el actual)
- ✅ **CON** tus API Keys
- ✅ Funciona inmediatamente
- ✅ Fácil de instalar

### Audiencia:
- Familia
- Amigos cercanos
- 10-15 personas de confianza

### Cómo funciona:
1. Subes APK a tu Google Drive
2. Configuras link privado
3. Compartes link SOLO por WhatsApp
4. Usuario descarga e instala
5. App funciona inmediatamente (usa tus API Keys)

### Ventajas:
- ✅ Súper fácil para usuarios no técnicos
- ✅ Funciona inmediatamente sin configuración
- ✅ Control total de quién lo descarga

### Desventajas:
- ⚠️ Todos usan TUS API Keys
- ⚠️ Límite de ~15-20 usuarios (por cuota de API)
- ⚠️ Debes controlar la distribución

---

## Pasos para Implementar

### A. Compilar APK Público (sin keys)

1. Sigue las instrucciones en: **COMPILAR_VERSION_PUBLICA.md**
2. Tendrás: `TalesDB-v1.4.0-public.apk`

### B. Preparar APK Privado (con keys)

Ya lo tienes listo:
- ✅ `TalesDB-v1.4.0.apk` (5.4 MB)
- Ubicación: Raíz del proyecto

### C. Subir a GitHub Release (APK público)

1. Ve a: https://github.com/jmmoyab/TalesDB/releases/new
2. Tag: `v1.4.0`
3. Título: `TalesDB v1.4.0 - Autocompletado con APIs`
4. Descripción: Ver `RELEASE_NOTES_GITHUB.md` (abajo)
5. Sube: `TalesDB-v1.4.0-public.apk`
6. Publica

### D. Subir a Google Drive (APK privado)

1. Abre Google Drive en tu móvil
2. Toca el **+** (Nuevo)
3. Selecciona "Subir archivo"
4. Busca: `TalesDB-v1.4.0.apk` (el que ya tienes)
5. Espera a que se suba
6. Toca los 3 puntos → "Compartir"
7. Cambia a: "Cualquiera con el enlace puede ver"
8. Copia el link

---

## Notas de Release para GitHub

Usa este texto en el GitHub Release (para el APK público):

```markdown
## ⚠️ IMPORTANTE: Configuración de API Keys

Este APK **NO incluye API Keys**. Para usar la búsqueda automática, necesitas:

1. Instalar el APK
2. Obtener tus propias API Keys gratuitas:
   - Google Books API (gratuita, 1000 req/día)
   - TMDB API (gratuita, 3M req/mes)
3. Seguir las instrucciones en [CONFIGURAR_API_KEYS.md](CONFIGURAR_API_KEYS.md)

**¿Por qué?** Para evitar abuso de límites de API. Cada usuario usa su propia cuota.

---

## Novedades v1.4.0

### ✨ Autocompletado Inteligente
- 🔍 Búsqueda automática con Google Books API
- 🎬 Búsqueda automática con TMDB API
- ⚡ Autocompletado de título, autor/director, año, género
- 📊 Duración y episodios

### 🔧 Mejoras
- Interfaz optimizada
- Mensajes de error claros
- Validación de API Keys
- Fix botón "Salir"

---

## 📥 Instalación

1. Descarga **TalesDB-v1.4.0-public.apk**
2. Habilita "Orígenes desconocidos"
3. Instala el APK
4. Configura tus API Keys ([instrucciones](CONFIGURAR_API_KEYS.md))

---

## 📋 Requisitos

- Android 5.0+
- Conexión a Internet (para búsqueda)
- API Keys propias (gratuitas)

---

## 🐛 Reportar problemas

[Issues](https://github.com/jmmoyab/TalesDB/issues)
```

---

## Mensaje para WhatsApp (APK privado)

Cuando tengas el link de Google Drive, envía esto por WhatsApp:

```
Hola! 👋

Te comparto mi app TalesDB v1.4.0 para organizar libros, películas y series.

📱 Descarga aquí:
[TU_LINK_DE_GOOGLE_DRIVE]

📖 Instrucciones de instalación:
https://github.com/jmmoyab/TalesDB/blob/feature/autocompletado-v1.4.0/COMO_INSTALAR.md

✨ Características:
- Búsqueda automática de libros, películas y series
- Estadísticas de tu colección
- Exportar/importar datos
- Sin anuncios

⚡ Esta versión funciona inmediatamente, no necesitas configurar nada. Solo descarga, instala y úsala.

🔒 Es gratis, sin anuncios y tus datos quedan en tu teléfono.

Cualquier duda, avísame!
```

---

## Control de Distribución

### GitHub Release (público):
- ✅ Distribución ilimitada
- ✅ Cada usuario configura sus keys
- ✅ Sin riesgo para ti

### Google Drive (privado):
- ⚠️ Solo compartir con ~15 personas
- ⚠️ NO publicar en redes sociales
- ⚠️ Monitorear uso de APIs si hay problemas

### Monitorear límites de API:

**Google Books**:
- Ve a: https://console.cloud.google.com
- APIs & Services → Dashboard
- Revisa uso diario

**TMDB**:
- Ve a: https://www.themoviedb.org/settings/api
- Revisa estadísticas de uso

Si ves picos extraños → El APK privado se filtró → Cambiar API Keys

---

## Resumen

| Aspecto | GitHub (Público) | Drive (Privado) |
|---------|------------------|-----------------|
| APK | TalesDB-v1.4.0-public.apk | TalesDB-v1.4.0.apk |
| API Keys | No incluidas | Incluidas (tuyas) |
| Audiencia | Público general | Familia/amigos |
| Límite | Ilimitado | ~15 personas |
| Configuración | Usuario configura keys | Funciona directo |
| Riesgo | Ninguno | Abuso de tus APIs |
| Control | Ninguno | Total |

---

**Fecha**: 12 de Enero 2026
**Versión**: 1.4.0
