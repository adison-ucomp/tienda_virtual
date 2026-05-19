@echo off

cd /d "%~dp0..\.."

title Flujo Git Automatico

echo ========================================
echo INICIANDO FLUJO GIT
echo ========================================
echo.

IF NOT EXIST ".git" (
    echo ERROR: No se encontro repositorio Git
    pause
    exit /b
)

for /f %%i in ('powershell -Command "[guid]::NewGuid().ToString()"') do set UUID=%%i

echo UUID generado:
echo %UUID%
echo.

echo [1/5] Ejecutando git pull...
git pull

IF %ERRORLEVEL% NEQ 0 (
    echo ERROR en git pull
    pause
    exit /b
)

echo.
echo [2/5] Ejecutando git status...
git status

echo.
echo [3/5] Ejecutando git add -A...
git add -A

IF %ERRORLEVEL% NEQ 0 (
    echo ERROR en git add
    pause
    exit /b
)

echo.
echo [4/5] Ejecutando git commit...
git commit -m "feat: %UUID%"

IF %ERRORLEVEL% NEQ 0 (
    echo ERROR en git commit
    pause
    exit /b
)

echo.
echo [5/5] Ejecutando git push...
git push

IF %ERRORLEVEL% NEQ 0 (
    echo ERROR en git push
    pause
    exit /b
)

echo.
echo ========================================
echo PROCESO FINALIZADO CORRECTAMENTE
echo ========================================

pause
