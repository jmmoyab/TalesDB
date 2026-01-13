# 🚨 Plan de Recuperación - API Keys Filtradas

**Fecha:** 12 de Enero 2026
**Problema:** API Keys expuestas en historial de Git público

---

## ⚠️ Situación

GitHub detectó tus API Keys en el commit `db6fadfd` del repositorio público.

Aunque ahora están protegidas con `.gitignore`, el **historial de Git** conserva las keys.

**Riesgo:** Cualquiera puede:
- Ver el historial: `git log`
- Ver el commit antiguo: `git show db6fadfd`
- Extraer tus API Keys
- Usar tu cuota de APIs

---

## ✅ Plan de Acción (30 minutos)

### Fase 1: Revocar API Keys (URGENTE - 10 min)

#### Google Books API

1. Ve a: https://console.cloud.google.com/apis/credentials
2. Inicia sesión con tu cuenta de Google
3. Busca el proyecto "TalesDB" o similar
4. Click en **API Keys**
5. Busca la key: `AIzaSyBzPq8lvDjLIUb87Qk2ha1PL5uy_40TYDk`
6. Click en **🗑️** (Eliminar) o **Regenerar**
7. Confirma la eliminación

#### TMDB API

1. Ve a: https://www.themoviedb.org/settings/api
2. Inicia sesión
3. Busca tu API Key actual
4. Click en **Revoke** o **Regenerate**
5. Confirma

**✅ Checkpoint:** Keys revocadas. Ahora nadie puede usarlas.

---

### Fase 2: Limpiar Historial de Git (10 min)

**Ejecuta desde terminal:**

```bash
chmod +x EMERGENCIA_LIMPIAR_KEYS.sh
./EMERGENCIA_LIMPIAR_KEYS.sh
```

El script te preguntará si ya revocaste las keys.

**Luego ejecuta:**

```bash
git push origin --force --all
git push origin --force --tags
```

⚠️ Esto reescribirá el historial público en GitHub.

**✅ Checkpoint:** Historial limpio en GitHub.

---

### Fase 3: Generar Nuevas API Keys (10 min)

#### Nueva Google Books API Key

1. Ve a: https://console.cloud.google.com/apis/credentials
2. Click en **+ CREATE CREDENTIALS**
3. Selecciona **API Key**
4. Copia la nueva key generada
5. (Opcional) Click en **Restrict Key** → Solo Google Books API

#### Nueva TMDB API Key

1. Ve a: https://www.themoviedb.org/settings/api
2. Click en **Generate New API Key** o similar
3. Llena el formulario (uso personal/educativo)
4. Copia la nueva key

**✅ Checkpoint:** Nuevas keys generadas.

---

### Fase 4: Actualizar Código Local (5 min)

1. Abre: `app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt`

2. Reemplaza las keys antiguas por las nuevas:

```kotlin
const val GOOGLE_BOOKS_KEY = "TU_NUEVA_GOOGLE_KEY_AQUI"
const val TMDB_KEY = "TU_NUEVA_TMDB_KEY_AQUI"
```

3. Guarda el archivo

4. **NO hagas commit** (el archivo está en .gitignore)

**✅ Checkpoint:** App funcionando con nuevas keys.

---

### Fase 5: Recompilar APKs (10 min)

#### APK Privado (con nuevas keys)

```bash
# AndroidIDE: Build → Assemble Release
cp app/build/outputs/apk/release/app-release.apk TalesDB-v1.4.0.apk
```

#### APK Público (sin keys)

```bash
./compile-public-manual.sh prepare
# AndroidIDE: Build → Assemble Release
./compile-public-manual.sh package
./compile-public-manual.sh restore
```

**✅ Checkpoint:** APKs actualizados con nuevas keys.

---

### Fase 6: Publicar Release en GitHub (5 min)

Ahora SÍ puedes crear el GitHub Release:

1. Ve a: https://github.com/jmmoyab/TalesDB/releases/new
2. Usa el archivo: `RELEASE_GITHUB_v1.4.0.txt`
3. Sube: `TalesDB-v1.4.0-public.apk` (el recién compilado)
4. Publica

**✅ Checkpoint:** Release público sin tus keys reales.

---

## 🔍 Verificación Final

### Verificar que el historial está limpio:

```bash
# Buscar las keys antiguas en el historial
git log -p | grep "AIzaSyBzPq8lvDjLIUb87Qk2ha1PL5uy_40TYDk"
```

**Resultado esperado:** No debe encontrar nada

### Verificar que .gitignore funciona:

```bash
git status | grep ApiConfig.kt
```

**Resultado esperado:** No debe aparecer

---

## 📊 Resumen

| Fase | Tiempo | Estado |
|------|--------|--------|
| 1. Revocar keys antiguas | 10 min | ⏳ |
| 2. Limpiar historial Git | 10 min | ⏳ |
| 3. Generar nuevas keys | 10 min | ⏳ |
| 4. Actualizar código | 5 min | ⏳ |
| 5. Recompilar APKs | 10 min | ⏳ |
| 6. Publicar Release | 5 min | ⏳ |
| **TOTAL** | **50 min** | |

---

## ⚠️ Importante

**NO saltes ninguna fase.** Cada paso es crítico:

1. **Primero revoca** → Evita abuso inmediato
2. **Limpia historial** → Elimina evidencia pública
3. **Nuevas keys** → Funcionamiento restaurado
4. **Recompila** → APKs actualizados
5. **Publica** → Distribución segura

---

## 🛡️ Prevención Futura

Este problema **NO volverá a pasar** porque:

✅ `.gitignore` protege `ApiConfig.kt`
✅ Scripts automáticos manejan las keys
✅ Documentación clara sobre seguridad
✅ Verificación antes de commits

---

## 📞 Soporte

Si tienes problemas:
1. Lee cada fase con calma
2. Ejecuta un paso a la vez
3. Verifica cada checkpoint
4. Avísame si algo falla

---

**Estado actual:** ⏸️ PAUSA - No publiques el release hasta completar todas las fases

**Próximo paso:** Revocar las API Keys antiguas

---

**Fecha del plan:** 12 de Enero 2026
**Tiempo estimado total:** 50 minutos
**Prioridad:** 🔴 CRÍTICA
