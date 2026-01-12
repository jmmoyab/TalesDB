#!/bin/bash

################################################################################
# Script: compile-public-manual.sh
# Para usar en AndroidIDE (compilación manual)
# Versión: 1.0
################################################################################

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Rutas
API_CONFIG="app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt"
API_TEMPLATE="app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt.template"
API_BACKUP="app/src/main/java/com/example/myapplication/data/api/ApiConfig.kt.backup"

# Función para mostrar uso
usage() {
    echo "Uso: ./compile-public-manual.sh [COMANDO]"
    echo ""
    echo "Comandos:"
    echo "  prepare   - Prepara el código (quita API Keys)"
    echo "  restore   - Restaura el código (pone API Keys)"
    echo "  package   - Copia y renombra el APK compilado"
    echo ""
    echo "Flujo completo:"
    echo "  1. ./compile-public-manual.sh prepare"
    echo "  2. [Compilar en AndroidIDE: Build → Assemble Release]"
    echo "  3. ./compile-public-manual.sh package"
    echo "  4. ./compile-public-manual.sh restore"
}

# Función prepare
prepare() {
    echo "================================================"
    echo "  PREPARAR: Quitar API Keys"
    echo "================================================"
    echo ""

    if [ ! -f "$API_TEMPLATE" ]; then
        echo -e "${RED}ERROR: No existe ApiConfig.kt.template${NC}"
        exit 1
    fi

    if [ ! -f "$API_CONFIG" ]; then
        echo -e "${RED}ERROR: No existe ApiConfig.kt${NC}"
        exit 1
    fi

    echo -e "${YELLOW}1.${NC} Haciendo backup de ApiConfig.kt..."
    cp "$API_CONFIG" "$API_BACKUP"
    echo -e "${GREEN}✓${NC} Backup creado"
    echo ""

    echo -e "${YELLOW}2.${NC} Reemplazando con template (sin keys)..."
    cp "$API_TEMPLATE" "$API_CONFIG"
    echo -e "${GREEN}✓${NC} ApiConfig.kt sin API Keys"
    echo ""

    echo "================================================"
    echo -e "${GREEN}  LISTO PARA COMPILAR${NC}"
    echo "================================================"
    echo ""
    echo "Ahora:"
    echo "  1. Abre AndroidIDE"
    echo "  2. Build → Assemble Release"
    echo "  3. Espera a que termine (2-5 min)"
    echo "  4. Ejecuta: ./compile-public-manual.sh package"
    echo ""
}

# Función restore
restore() {
    echo "================================================"
    echo "  RESTAURAR: Poner API Keys"
    echo "================================================"
    echo ""

    if [ ! -f "$API_BACKUP" ]; then
        echo -e "${YELLOW}⚠${NC} No hay backup. Las API Keys ya están restauradas."
        exit 0
    fi

    echo -e "${YELLOW}1.${NC} Restaurando ApiConfig.kt original..."
    cp "$API_BACKUP" "$API_CONFIG"
    rm -f "$API_BACKUP"
    echo -e "${GREEN}✓${NC} ApiConfig.kt restaurado con tus API Keys"
    echo ""

    echo "================================================"
    echo -e "${GREEN}  RESTAURADO${NC}"
    echo "================================================"
    echo ""
}

# Función package
package() {
    echo "================================================"
    echo "  EMPAQUETAR: Copiar APK"
    echo "================================================"
    echo ""

    APK_SOURCE="app/build/outputs/apk/release/app-release.apk"

    if [ ! -f "$APK_SOURCE" ]; then
        echo -e "${RED}ERROR: No se encuentra el APK compilado${NC}"
        echo "Ubicación esperada: $APK_SOURCE"
        echo ""
        echo "¿Ya compilaste en AndroidIDE?"
        exit 1
    fi

    # Obtener versión
    VERSION=$(grep "versionName" app/build.gradle | awk '{print $2}' | tr -d '"' | head -1)
    if [ -z "$VERSION" ]; then
        VERSION="1.4.0"
    fi

    OUTPUT_NAME="TalesDB-v${VERSION}-public.apk"

    echo -e "${YELLOW}1.${NC} Copiando APK..."
    cp "$APK_SOURCE" "$OUTPUT_NAME"

    APK_SIZE=$(du -h "$OUTPUT_NAME" | cut -f1)

    echo -e "${GREEN}✓${NC} APK copiado: $OUTPUT_NAME ($APK_SIZE)"
    echo ""

    echo "================================================"
    echo -e "${GREEN}  APK PÚBLICO CREADO${NC}"
    echo "================================================"
    echo ""
    echo "Archivo: $OUTPUT_NAME"
    echo "Tamaño: $APK_SIZE"
    echo ""
    echo "Este APK está listo para GitHub Release"
    echo ""
    echo "No olvides ejecutar:"
    echo "  ./compile-public-manual.sh restore"
    echo ""
}

# Main
case "${1}" in
    prepare)
        prepare
        ;;
    restore)
        restore
        ;;
    package)
        package
        ;;
    *)
        usage
        exit 1
        ;;
esac
