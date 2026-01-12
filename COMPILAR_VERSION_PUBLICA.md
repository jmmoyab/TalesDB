# Compilar Versión Pública (sin API Keys)

## Objetivo

Crear un APK público para GitHub Release que NO contenga tus API Keys reales.

---

## Pasos

### 1. Abrir ApiConfig.kt

1. Abre **AndroidIDE**
2. Navega a: `app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt`
3. Abre el archivo

### 2. Reemplazar las API Keys con placeholders

**Busca estas líneas:**

```kotlin
const val GOOGLE_BOOKS_KEY = "AIzaSyBzPq8lvDjLIUb87Qk2ha1PL5uy_40TYDk"
```

```kotlin
const val TMDB_KEY = "d72101a7a4d3f8437f491aea892d6457"
```

**Reemplázalas con:**

```kotlin
const val GOOGLE_BOOKS_KEY = "TU_GOOGLE_BOOKS_API_KEY_AQUI"
```

```kotlin
const val TMDB_KEY = "TU_TMDB_API_KEY_AQUI"
```

### 3. Guardar el archivo

**IMPORTANTE**: Guarda los cambios (Ctrl+S o menú → Save)

### 4. Compilar APK Release

1. En AndroidIDE, ve al menú (3 líneas arriba izquierda)
2. **Build** → **Assemble Release**
3. Espera a que compile (puede tardar 2-5 minutos)
4. Cuando termine, verás: "BUILD SUCCESSFUL"

### 5. Localizar el APK compilado

El APK estará en:
```
app/build/outputs/apk/release/app-release.apk
```

### 6. Renombrar el APK público

Desde terminal o gestor de archivos:

```bash
cp app/build/outputs/apk/release/app-release.apk TalesDB-v1.4.0-public.apk
```

O manualmente:
- Copia `app-release.apk`
- Pégalo en la raíz del proyecto
- Renómbralo a: `TalesDB-v1.4.0-public.apk`

### 7. RESTAURAR tus API Keys

**MUY IMPORTANTE**: Después de compilar, restaura tus API Keys reales:

1. Abre de nuevo `ApiConfig.kt`
2. Reemplaza los placeholders con tus keys originales:
   - `GOOGLE_BOOKS_KEY = "AIzaSyBzPq8lvDjLIUb87Qk2ha1PL5uy_40TYDk"`
   - `TMDB_KEY = "d72101a7a4d3f8437f491aea892d6457"`
3. Guarda el archivo

**O usa el backup:**

```bash
cp app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt.template app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt
# Edita y pon tus keys reales
```

---

## Resultado Final

Tendrás dos APKs:

**1. TalesDB-v1.4.0-public.apk** (SIN tus API Keys)
- ✅ Para GitHub Release
- ✅ Usuarios configuran sus propias keys
- ✅ Tamaño: ~5.4 MB

**2. TalesDB-v1.4.0.apk** (CON tus API Keys) - El que ya tienes
- ✅ Para Google Drive (privado)
- ✅ Funciona directamente
- ✅ Solo para familia/amigos

---

## Distribución

### GitHub Release (APK público)
- Sube: `TalesDB-v1.4.0-public.apk`
- Instrucciones: Los usuarios deben leer CONFIGURAR_API_KEYS.md

### Google Drive (APK privado)
- Sube: `TalesDB-v1.4.0.apk` (el actual que ya tienes)
- Link solo por WhatsApp a conocidos
- Funciona directamente sin configuración

---

## Verificación

Para verificar que el APK público NO tiene tus keys:

1. Descomprime el APK (es un archivo ZIP):
   ```bash
   unzip -q TalesDB-v1.4.0-public.apk -d apk-extracted
   ```

2. Busca las clases:
   ```bash
   grep -r "AIzaSyBzPq8lvDjLIUb87Qk2ha1PL5uy_40TYDk" apk-extracted/
   ```

3. Si NO encuentra nada: ✅ Seguro
4. Si encuentra la key: ❌ Necesitas recompilar

---

**Fecha**: 12 de Enero 2026
**Versión**: 1.4.0
