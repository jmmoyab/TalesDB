# Documentación de Desarrollo - TalesDB

## Índice

1. [Configuración del Entorno](#configuración-del-entorno)
2. [Gestión de API Keys](#gestión-de-api-keys)
3. [Compilar APKs](#compilar-apks)
4. [Distribución](#distribución)
5. [Estructura del Proyecto](#estructura-del-proyecto)
6. [Workflow de Desarrollo](#workflow-de-desarrollo)

---

## Configuración del Entorno

### Requisitos

- **AndroidIDE** (versión 2.x o superior)
- Android SDK 21+
- Java 11+
- Git configurado

### Clonar el Proyecto

```bash
git clone https://github.com/jmmoyab/TalesDB.git
cd TalesDB
```

### Configurar API Keys (Primera vez)

1. Copia el template:
   ```bash
   cp app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt.template \
      app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt
   ```

2. Edita `ApiConfig.kt` y agrega tus API Keys:
   ```kotlin
   const val GOOGLE_BOOKS_KEY = "TU_KEY_AQUI"
   const val TMDB_KEY = "TU_KEY_AQUI"
   ```

3. Ver [CONFIGURAR_API_KEYS.md](CONFIGURAR_API_KEYS.md) para obtener las keys

---

## Gestión de API Keys

### Archivos Importantes

```
app/src/main/java/com/example/myapplication/data/api/
├── ApiConfig.kt           ← TUS API Keys (NUNCA se sube a Git)
├── ApiConfig.kt.template  ← Template sin keys (SÍ se sube a Git)
└── ApiConfig.kt.backup    ← Backup temporal (generado por scripts)
```

### Protección Git

El archivo `.gitignore` contiene:

```gitignore
# ⚠️ CRÍTICO: API Keys - NO SUBIR NUNCA
app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt
```

**NUNCA elimines esta línea del .gitignore**

### Verificar Protección

```bash
# Verificar que ApiConfig.kt está ignorado
git check-ignore -v app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt

# Debería mostrar: .gitignore:2:app/src/.../ApiConfig.kt
```

---

## Compilar APKs

### Dos Tipos de APKs

| Tipo | Uso | API Keys | Distribución |
|------|-----|----------|--------------|
| **Privado** | Familia/amigos | ✅ Incluidas (tuyas) | Google Drive |
| **Público** | GitHub Release | ❌ Sin keys | GitHub público |

---

### Compilar APK Privado (con tus API Keys)

**Desde AndroidIDE:**

1. Asegúrate que `ApiConfig.kt` tiene tus keys
2. **Build** → **Assemble Release**
3. APK en: `app/build/outputs/apk/release/app-release.apk`

**Renombrar:**

```bash
cp app/build/outputs/apk/release/app-release.apk TalesDB-v1.4.0.apk
```

**Distribución:**
- ✅ Google Drive (link privado)
- ✅ WhatsApp a conocidos
- ❌ NO subir a GitHub público

---

### Compilar APK Público (sin API Keys)

**Opción A: Script Manual (Recomendado para AndroidIDE)**

```bash
# Paso 1: Preparar código (quita API Keys)
./compile-public-manual.sh prepare

# Paso 2: Compilar en AndroidIDE
# Build → Assemble Release

# Paso 3: Empaquetar APK
./compile-public-manual.sh package

# Paso 4: Restaurar API Keys
./compile-public-manual.sh restore
```

**Resultado:** `TalesDB-v1.4.0-public.apk`

**Opción B: Script Automático (si gradlew funciona)**

```bash
./compile-public.sh
```

Hace todo automáticamente.

**Distribución:**
- ✅ GitHub Release
- ✅ Distribución pública ilimitada
- ✅ Usuarios configuran sus propias keys

---

### Dar Permisos a los Scripts

```bash
chmod +x compile-public.sh
chmod +x compile-public-manual.sh
```

---

## Distribución

### Estrategia Mixta (Recomendada)

#### APK Privado → Google Drive
1. Compila APK privado (con keys)
2. Sube a Google Drive
3. Configura: "Cualquiera con el enlace"
4. Comparte link SOLO por WhatsApp
5. **Máximo 15-20 personas**

#### APK Público → GitHub Release
1. Compila APK público (sin keys)
2. Ve a: https://github.com/jmmoyab/TalesDB/releases/new
3. Crea release con tag `v1.4.0`
4. Sube el APK público
5. Ver [INSTRUCCIONES_CREAR_RELEASE.md](INSTRUCCIONES_CREAR_RELEASE.md)

### Límites de APIs

**Con APK Privado (tus keys):**
- Google Books: 1,000 búsquedas/día
- TMDB: 3,000,000 peticiones/mes
- **Para 15 usuarios:** ✅ Seguro
- **Para 50+ usuarios:** ⚠️ Riesgo de exceder límites

**Con APK Público (sin keys):**
- ✅ Sin límite de distribución
- ✅ Cada usuario usa su propia cuota
- ✅ Sin riesgo para ti

---

## Estructura del Proyecto

```
TalesDB/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/myapplication/
│   │   │   ├── data/
│   │   │   │   ├── api/
│   │   │   │   │   ├── ApiConfig.kt          ← ⚠️ TUS API Keys (git-ignored)
│   │   │   │   │   ├── ApiConfig.kt.template ← Template público
│   │   │   │   │   ├── GoogleBooksApi.kt
│   │   │   │   │   └── TmdbApi.kt
│   │   │   │   ├── models/                   ← Modelos de datos
│   │   │   │   └── ContentDatabase.kt        ← Room Database
│   │   │   ├── ui/                           ← Activities/Fragments
│   │   │   └── utils/                        ← Utilidades
│   │   └── res/                              ← Recursos (layouts, strings)
│   └── build.gradle                          ← Dependencias y versión
│
├── .gitignore                                ← ⚠️ Protege API Keys
├── README.md                                 ← Documentación pública
├── README_DESARROLLO.md                      ← Este archivo
├── CONFIGURAR_API_KEYS.md                    ← Guía para obtener keys
├── COMO_INSTALAR.md                          ← Guía para usuarios finales
│
├── compile-public.sh                         ← Script automático
└── compile-public-manual.sh                  ← Script manual (AndroidIDE)
```

---

## Workflow de Desarrollo

### Desarrollo Normal

```bash
# 1. Crear nueva rama
git checkout -b feature/nueva-funcionalidad

# 2. Desarrollar
# (ApiConfig.kt tiene tus keys locales, NO se sube)

# 3. Compilar y probar
# Build → Assemble Debug

# 4. Commit y push
git add .
git commit -m "Feature: nueva funcionalidad"
git push origin feature/nueva-funcionalidad

# ApiConfig.kt NUNCA se sube (está en .gitignore)
```

---

### Crear Nueva Versión

**Ejemplo: v1.5.0**

#### 1. Actualizar versión en `app/build.gradle`

```gradle
versionCode 5
versionName "1.5.0"
```

#### 2. Compilar APK Privado

```bash
# AndroidIDE: Build → Assemble Release
cp app/build/outputs/apk/release/app-release.apk TalesDB-v1.5.0.apk
```

#### 3. Compilar APK Público

```bash
./compile-public-manual.sh prepare
# AndroidIDE: Build → Assemble Release
./compile-public-manual.sh package
./compile-public-manual.sh restore
```

Resultado: `TalesDB-v1.5.0-public.apk`

#### 4. Commit de la versión

```bash
git add app/build.gradle
git commit -m "Release: v1.5.0"
git push
```

#### 5. Crear GitHub Release

- Tag: `v1.5.0`
- Subir: `TalesDB-v1.5.0-public.apk`
- Release notes

#### 6. Distribuir APK Privado

- Subir `TalesDB-v1.5.0.apk` a Drive
- Compartir por WhatsApp

---

## Seguridad

### Checklist de Seguridad

Antes de hacer `git push`:

```bash
# ✅ Verificar que ApiConfig.kt NO se subirá
git status | grep ApiConfig.kt
# NO debe aparecer

# ✅ Verificar .gitignore
cat .gitignore | grep ApiConfig.kt
# Debe aparecer la línea

# ✅ Verificar archivos staged
git diff --cached --name-only | grep ApiConfig.kt
# NO debe aparecer
```

### Si Accidentalmente Subes las Keys

**¡EMERGENCIA!**

1. **Inmediatamente** revoca las API Keys:
   - Google Cloud Console → API Keys → Delete
   - TMDB → Settings → API → Revoke

2. Genera nuevas API Keys

3. Limpia el historial de Git:
   ```bash
   git filter-branch --force --index-filter \
   "git rm --cached --ignore-unmatch app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt" \
   --prune-empty --tag-name-filter cat -- --all

   git push origin --force --all
   ```

4. Actualiza tus keys locales

---

## Monitoreo de APIs

### Google Books API

1. Ve a: https://console.cloud.google.com
2. APIs & Services → Dashboard
3. Revisa métricas diarias

**Señal de alarma:** Más de 800 búsquedas/día (indica leak del APK privado)

### TMDB API

1. Ve a: https://www.themoviedb.org/settings/api
2. Revisa estadísticas

**Señal de alarma:** Picos inusuales de uso

### Acción si hay Leak

1. Cambiar API Keys inmediatamente
2. Recompilar APK privado con nuevas keys
3. Re-distribuir solo a personas de confianza
4. Considerar sistema de autenticación

---

## Recursos

### Enlaces Útiles

- **Repositorio:** https://github.com/jmmoyab/TalesDB
- **Google Cloud Console:** https://console.cloud.google.com
- **TMDB API:** https://www.themoviedb.org/settings/api
- **AndroidIDE:** https://m.androidide.com/

### Documentación Relacionada

- [README.md](README.md) - Documentación pública
- [CONFIGURAR_API_KEYS.md](CONFIGURAR_API_KEYS.md) - Obtener API Keys
- [COMO_INSTALAR.md](COMO_INSTALAR.md) - Para usuarios finales
- [PLAN_DISTRIBUCION_MIXTA.md](PLAN_DISTRIBUCION_MIXTA.md) - Estrategia de distribución

---

## Soporte

**Desarrollador:** jmmoyab
**GitHub Issues:** https://github.com/jmmoyab/TalesDB/issues

---

**Última actualización:** 12 de Enero 2026
**Versión del documento:** 1.0
