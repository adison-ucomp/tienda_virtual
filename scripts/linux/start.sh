#!/bin/bash

# Nombre personalizado para el contenedor
CONTAINER_NAME=e6262ed74d02

# Verifica si el contenedor existe (activo o inactivo)
if docker ps -a --format '{{.Names}}' | grep -wq "$CONTAINER_NAME"; then
    # Verifica si ya está en ejecución
    if docker ps --format '{{.Names}}' | grep -wq "$CONTAINER_NAME"; then
        echo "Contenedor '$CONTAINER_NAME' ya está en ejecución."
    else
        echo "Contenedor '$CONTAINER_NAME' existe pero está detenido. Iniciando..."
        docker start "$CONTAINER_NAME"
    fi
else
    echo "Contenedor no existe. Creando e iniciando..."
    docker run -d --name "$CONTAINER_NAME" -p 9411:9411 openzipkin/zipkin
fi

# Ejecutar la aplicación Spring Boot
echo "Iniciando aplicación Spring Boot..."
# java -jar target/mi-app.jar  # Descomenta y ajusta la ruta según tu jar

# Detener el contenedor de Zipkin al finalizar la app (opcional)
echo "Deteniendo imagen Zipkin..."
docker stop "$CONTAINER_NAME"
