@echo off
REM Verificador local. Requiere el JDK instalado (Unidad I, Laboratorio 1).
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
echo Compilando...
javac -encoding UTF-8 -d out marco\*.java *.java
if errorlevel 1 goto fin
echo Ejecutando pruebas...
echo.
java -cp out marco.Juez PruebasParcial03 - 5000
:fin
pause
