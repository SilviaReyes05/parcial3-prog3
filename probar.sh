#!/usr/bin/env bash
# Compila y ejecuta el main() de la plantilla (pruebas rapidas).
set -e
cd "$(dirname "$0")"
rm -rf out && mkdir -p out
javac -encoding UTF-8 -d out Parcial3ArreglosColecciones.java
java -cp out Parcial3ArreglosColecciones
