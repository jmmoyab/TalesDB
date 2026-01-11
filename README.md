# TalesDB - Organizador de Libros, Películas y Series

![Version](https://img.shields.io/badge/version-1.4.0-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![License](https://img.shields.io/badge/license-MIT-orange.svg)

**TalesDB** es una aplicación Android para organizar y gestionar tu colección personal de libros, películas y series. Con búsqueda automática, estadísticas detalladas y exportación de datos.

## Características Principales

- **Gestión completa de contenido**: Libros, películas y series en una sola app
- **Autocompletado inteligente**: Búsqueda automática usando Google Books API y TMDB
- **Estados personalizados**: Pendiente, En Progreso, Completado, Abandonado
- **Estadísticas detalladas**: Visualiza tu progreso y colección
- **Exportación/Importación**: Backup completo de tus datos en formato TXT
- **Filtros avanzados**: Por tipo, estado, género, etc.
- **Sin anuncios**: 100% gratuita y sin publicidad

## Versión Actual: v1.4.0

### Novedades v1.4.0
- Autocompletado con APIs funcionando al 100%
- Integración completa con Google Books API
- Integración completa con TMDB (The Movie Database)
- Búsqueda mejorada con duración de películas y episodios de series
- Corrección de bugs menores

## Requisitos

- Android 5.0 (Lollipop) o superior
- Conexión a Internet (solo para búsqueda automática)
- **API Keys propias** (Google Books + TMDB) - Ver sección de configuración

## Instalación

### Para usuarios finales:

1. Descarga el APK desde [Releases](https://github.com/jmmoyab/TalesDB/releases)
2. Habilita "Orígenes desconocidos" en tu dispositivo
3. Instala el APK
4. Consulta `COMO_INSTALAR.md` para instrucciones detalladas

### Para desarrolladores:

```bash
# Clonar el repositorio
git clone https://github.com/jmmoyab/TalesDB.git

# Navegar al directorio
cd TalesDB

# Configurar API Keys (IMPORTANTE)
# 1. Copia el archivo template
cp app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt.template \
   app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt

# 2. Edita ApiConfig.kt y agrega tus API Keys
# Ver CONFIGURAR_API_KEYS.md para obtener las keys

# Compilar con Android Studio o Gradle
./gradlew assembleDebug
```

## Configuración de API Keys

La aplicación requiere dos API Keys para funcionar:

### 1. Google Books API Key
- **Obtener en**: [Google Cloud Console](https://console.cloud.google.com)
- **Límite gratuito**: 1,000 peticiones/día
- **Uso**: Búsqueda de libros

### 2. TMDB API Key
- **Obtener en**: [TMDB Settings](https://www.themoviedb.org/settings/api)
- **Límite gratuito**: 3,000,000 peticiones/mes
- **Uso**: Búsqueda de películas y series

**Ver `CONFIGURAR_API_KEYS.md` para instrucciones paso a paso**

## Estructura del Proyecto

```
TalesDB/
├── app/
│   ├── src/main/java/com/example/myapplication/
│   │   ├── data/           # Modelos y base de datos
│   │   │   ├── api/        # APIs y configuración
│   │   │   └── models/     # Modelos de datos
│   │   ├── ui/             # Actividades y UI
│   │   └── utils/          # Utilidades
│   └── build.gradle        # Dependencias
├── CONFIGURAR_API_KEYS.md  # Guía de configuración
├── COMO_INSTALAR.md        # Guía de instalación
└── README.md               # Este archivo
```

## Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **Base de Datos**: SQLite (Room)
- **Networking**: Retrofit 2 + OkHttp
- **APIs Externas**: Google Books API, TMDB API
- **UI**: Material Design Components
- **Arquitectura**: MVVM (Model-View-ViewModel)

## Uso

1. **Agregar contenido**:
   - Toca el botón "+" flotante
   - Usa la búsqueda automática o añade manualmente

2. **Organizar**:
   - Filtra por tipo (Libro/Película/Serie)
   - Filtra por estado (Pendiente/En Progreso/Completado/Abandonado)

3. **Estadísticas**:
   - Visualiza tu colección por tipo
   - Revisa tu progreso

4. **Backup**:
   - Exporta tus datos en formato TXT
   - Importa datos desde archivo TXT

## Roadmap

### v1.4.1 (Próxima - Opcional)
- Caché de búsquedas populares
- Mejora de mensajes de error
- Tutorial de primera vez

### v1.5.0 (Planificada)
- Backup automático de SQLite
- Modo oscuro/claro
- Sincronización en la nube

### v1.6.0 (Futura)
- Integración con Gemini AI
- Recomendaciones personalizadas
- Resúmenes de contenido

## Privacidad y Seguridad

- **Datos locales**: Toda tu información se almacena localmente en tu dispositivo
- **API Keys**: Las API Keys NO están incluidas en el repositorio por seguridad
- **Sin analytics**: No recopilamos ningún dato de uso
- **Sin permisos innecesarios**: Solo solicita los permisos estrictamente necesarios

## Distribución

Esta aplicación es de **distribución gratuita** para uso personal y familiar.

**Límites recomendados con las API Keys gratuitas:**
- Hasta 15-20 usuarios: 100% seguro
- Más de 50 usuarios: Considerar upgrade de APIs

## Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add: AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo `LICENSE` para más detalles.

## Contacto

**Desarrollador**: jmmoyab
**Repositorio**: [https://github.com/jmmoyab/TalesDB](https://github.com/jmmoyab/TalesDB)

---

**Hecho con ❤️ para organizar todas tus historias favoritas**
