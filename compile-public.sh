#!/bin/bash

################################################################################
# Script: compile-public.sh
# Descripción: Compila APK público sin API Keys para distribución en GitHub
# Versión: 1.0
# Fecha: 12 de Enero 2026
################################################################################

set -e  # Salir si hay error

echo "================================================"
echo "  TalesDB - Compilar Versión Pública"
echo "================================================"
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Rutas
API_CONFIG="app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt"
API_TEMPLATE="app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt.template"
API_BACKUP="app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt.backup"

# Verificar que existe el template
if [ ! -f "$API_TEMPLATE" ]; then
    echo -e "${RED}ERROR: No se encuentra ApiConfig.kt.template${NC}"
    exit 1
fi

# Verificar que existe ApiConfig.kt
if [ ! -f "$API_CONFIG" ]; then
    echo -e "${RED}ERROR: No se encuentra ApiConfig.kt${NC}"
    exit 1
fi

echo -e "${YELLOW}Paso 1:${NC} Haciendo backup de ApiConfig.kt (con tus API Keys)"
cp "$API_CONFIG" "$API_BACKUP"
echo -e "${GREEN}✓${NC} Backup creado: ApiConfig.kt.backup"
echo ""

echo -e "${YELLOW}Paso 2:${NC} Reemplazando con template (sin API Keys)"
cp "$API_TEMPLATE" "$API_CONFIG"
echo -e "${GREEN}✓${NC} ApiConfig.kt ahora tiene placeholders"
echo ""

echo -e "${YELLOW}Paso 3:${NC} Limpiando build anterior"
rm -rf app/build/outputs/apk/release/
echo -e "${GREEN}✓${NC} Build limpiado"
echo ""

echo -e "${YELLOW}Paso 4:${NC} Compilando APK Release (versión pública)"
echo -e "${YELLOW}Nota:${NC} Esto puede tardar 2-5 minutos en AndroidIDE..."
echo ""

# Intentar compilar con gradlew (si tiene permisos)
if [ -x ./gradlew ]; then
    ./gradlew assembleRelease
else
    echo -e "${YELLOW}⚠${NC} No se puede ejecutar gradlew desde terminal"
    echo -e "${YELLOW}⚠${NC} Debes compilar manualmente desde AndroidIDE:"
    echo ""
    echo "   1. Build → Assemble Release"
    echo "   2. Espera a que termine"
    echo "   3. Vuelve a ejecutar este script con el parámetro 'restore':"
    echo ""
    echo "      ./compile-public.sh restore"
    echo ""
    exit 0
fi

echo ""
echo -e "${GREEN}✓${NC} Compilación completada"
echo ""

# Verificar que se generó el APK
if [ ! -f "app/build/outputs/apk/release/app-release.apk" ]; then
    echo -e "${RED}ERROR: No se generó el APK${NC}"
    echo "Restaurando ApiConfig.kt original..."
    cp "$API_BACKUP" "$API_CONFIG"
    rm -f "$API_BACKUP"
    exit 1
fi

echo -e "${YELLOW}Paso 5:${NC} Copiando y renombrando APK"

# Obtener versión del build.gradle
VERSION=$(grep "versionName" app/build.gradle | awk '{print $2}' | tr -d '"')
if [ -z "$VERSION" ]; then
    VERSION="1.4.0"
fi

OUTPUT_NAME="TalesDB-v${VERSION}-public.apk"

cp app/build/outputs/apk/release/app-release.apk "$OUTPUT_NAME"
echo -e "${GREEN}✓${NC} APK creado: $OUTPUT_NAME"
echo ""

echo -e "${YELLOW}Paso 6:${NC} Restaurando ApiConfig.kt original (con tus API Keys)"
cp "$API_BACKUP" "$API_CONFIG"
rm -f "$API_BACKUP"
echo -e "${GREEN}✓${NC} ApiConfig.kt restaurado"
echo ""

# Mostrar información del APK
APK_SIZE=$(du -h "$OUTPUT_NAME" | cut -f1)
echo "================================================"
echo -e "${GREEN}  ¡ÉXITO!${NC}"
echo "================================================"
echo ""
echo "APK público generado:"
echo "  Nombre: $OUTPUT_NAME"
echo "  Tamaño: $APK_SIZE"
echo "  Ubicación: $(pwd)/$OUTPUT_NAME"
echo ""
echo "Este APK:"
echo "  ✓ NO contiene tus API Keys"
echo "  ✓ Está listo para subir a GitHub Release"
echo "  ✓ Usuarios deben configurar sus propias keys"
echo ""
echo "Próximo paso:"
echo "  1. Ve a: https://github.com/jmmoyab/TalesDB/releases/new"
echo "  2. Sube: $OUTPUT_NAME"
echo "  3. Ver: INSTRUCCIONES_CREAR_RELEASE.md"
echo ""
echo "================================================"
