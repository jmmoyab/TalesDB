#!/bin/bash

################################################################################
# EMERGENCIA: Limpiar API Keys del historial de Git
# SOLO ejecutar después de revocar las API Keys en Google y TMDB
################################################################################

set -e

echo "🚨 LIMPIEZA DE EMERGENCIA - API Keys filtradas"
echo "================================================"
echo ""
echo "⚠️  ANTES DE CONTINUAR:"
echo "  1. ¿Ya revocaste la Google Books API Key? (s/n)"
read -r google_revoked
echo "  2. ¿Ya revocaste la TMDB API Key? (s/n)"
read -r tmdb_revoked

if [[ "$google_revoked" != "s" ]] || [[ "$tmdb_revoked" != "s" ]]; then
    echo ""
    echo "❌ STOP: Primero revoca las API Keys antes de continuar"
    echo ""
    echo "Google Cloud Console: https://console.cloud.google.com/apis/credentials"
    echo "TMDB Settings: https://www.themoviedb.org/settings/api"
    echo ""
    exit 1
fi

echo ""
echo "Iniciando limpieza del historial..."
echo ""

# Backup del branch actual
CURRENT_BRANCH=$(git branch --show-current)
echo "Branch actual: $CURRENT_BRANCH"

# Remover ApiConfig.kt del historial completo
echo "Paso 1: Removiendo ApiConfig.kt del historial..."
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt" \
  --prune-empty --tag-name-filter cat -- --all

echo ""
echo "Paso 2: Limpiando referencias..."
rm -rf .git/refs/original/
git reflog expire --expire=now --all
git gc --prune=now --aggressive

echo ""
echo "✅ Historial limpiado localmente"
echo ""
echo "================================================"
echo "SIGUIENTE PASO:"
echo "================================================"
echo ""
echo "Ejecuta estos comandos para actualizar GitHub:"
echo ""
echo "  git push origin --force --all"
echo "  git push origin --force --tags"
echo ""
echo "⚠️  Esto reescribirá el historial público de GitHub"
echo ""
