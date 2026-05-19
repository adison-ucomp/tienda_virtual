@echo off
setlocal

REM Nombre del contenedor
set CONTAINER_NAME=e6262ed74d02

REM Verificar si el contenedor existe
docker inspect %CONTAINER_NAME% >nul 2>&1

if %errorlevel%==0 (
    echo El contenedor "%CONTAINER_NAME%" ya existe. Iniciando...
    docker start %CONTAINER_NAME%
) else (
    echo El contenedor no existe. Creando e iniciando...
    docker run -d --name %CONTAINER_NAME% -p 9411:9411 openzipkin/zipkin
)

:: REM Iniciar aplicación compilada
:: echo Iniciando aplicación Spring...
:: start "" java -jar compile\base_app_spring.jar

REM Esperar tiempo establecido para detener el contenedor
REM (1 horas * 60 minutos * 60 segundos = 3600 segundos)
REM (2 horas * 60 minutos * 60 segundos = 7200 segundos)
echo Esperando el tiempo antes de detener el contenedor...
timeout /t 3600 /nobreak

REM Detener el contenedor después de esperar
echo Deteniendo contenedor "%CONTAINER_NAME%"...
docker stop %CONTAINER_NAME%
