#!/usr/bin/env bash
# Verificador local. Requiere el JDK instalado (Unidad I, Laboratorio 1).
set -e
cd "$(dirname "$0")"
rm -rf out && mkdir -p out
echo "Compilando..."
javac -encoding UTF-8 -d out marco/*.java *.java
echo "Ejecutando pruebas..."
echo
java -cp out marco.Juez PruebasParcial03 - 5000
