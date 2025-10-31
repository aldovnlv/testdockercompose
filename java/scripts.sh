#!/bin/bash
# scripts.sh - Scripts útiles para SIGEFVE módulo Java

# ==================== COMPILACIÓN Y EJECUCIÓN ====================

# Compilar el proyecto
compile() {
    echo "🔨 Compilando proyecto..."
    mvn clean compile
}

# Empaquetar en JAR
package() {
    echo "📦 Empaquetando JAR..."
    mvn clean package -DskipTests
}

# Ejecutar aplicación
run() {
    echo "🚀 Ejecutando aplicación..."
    java -jar target/sigefve-java-1.0.0.jar
}

# Compilar y ejecutar
build_and_run() {
    package && run
}

# ==================== DOCKER ====================

# Construir imagen Docker
docker_build() {
    echo "Construyendo imagen Docker..."
    docker build -t sigefve-java:latest .
}

# Ejecutar contenedor
docker_run() {
    echo "Ejecutando contenedor..."
    docker run -p 8585:8585 \
        -e DB_URL=jdbc:postgresql://host.docker.internal:54302/sigefve \
        -e DB_USER=postgres \
        -e DB_PASSWORD=postgres \
        sigefve-java:latest
}

# Docker Compose
docker_compose_up() {
    echo "Iniciando servicios con Docker Compose..."
    docker-compose up --build
}

docker_compose_down() {
    echo "Deteniendo servicios..."
    docker-compose down
}

# ==================== BASE DE DATOS ====================

# Crear base de datos
create_db() {
    echo "Creando base de datos..."
    psql -U postgres -c "CREATE DATABASE sigefve;"
}

# Eliminar base de datos
drop_db() {
    echo "Eliminando base de datos..."
    psql -U postgres -c "DROP DATABASE IF EXISTS sigefve;"
}

# Reiniciar base de datos
reset_db() {
    drop_db
    create_db
    echo "✓ Base de datos reiniciada"
}

# Conectar a la base de datos
connect_db() {
    echo "Conectando a PostgreSQL..."
    psql -U postgres -d sigefve
}

# Inicializar datos de prueba
init_test_data() {
    echo "Inicializando datos de prueba..."
    mvn exec:java -Dexec.mainClass="com.sigefve.utils.InicializadorDatos"
}

# ==================== PRUEBAS ====================

# Ejecutar pruebas
test() {
    echo "Ejecutando pruebas..."
    mvn test
}

# Prueba de salud del servicio
health_check() {
    echo "Verificando salud del servicio..."
    curl -s http://localhost:8585/health | jq
}

# Listar vehículos
list_vehicles() {
    echo "Listando vehículos..."
    curl -s http://localhost:8585/vehiculos | jq
}

# Obtener telemetría de vehículo
get_telemetry() {
    local vehicle_id=${1:-1}
    echo "Obteniendo telemetría del vehículo $vehicle_id..."
    curl -s "http://localhost:8585/telemetria/vehiculo/$vehicle_id/ultima" | jq
}

# Listar rutas activas
list_routes() {
    echo " Listando rutas activas..."
    curl -s http://localhost:8585/rutas | jq
}

# ==================== DESARROLLO ====================

# Limpiar archivos compilados
clean() {
    echo "Limpiando archivos compilados..."
    mvn clean
}

# Ver logs de Docker
logs() {
    echo "Mostrando logs..."
    docker-compose logs -f java-service
}

# Reiniciar servicio completo
restart() {
    echo "Reiniciando servicio..."
    docker-compose restart java-service
}

# ==================== MONITOREO ====================

# Monitorear telemetría en tiempo real
monitor_telemetry() {
    echo "Monitoreando telemetría (Ctrl+C para salir)..."
    while true; do
        clear
        date
        echo "================================"
        curl -s http://localhost:8585/vehiculos | jq '.[] | {placa, estado, kilometrajeTotal}'
        sleep 5
    done
}

# Ver estadísticas de la base de datos
db_stats() {
    echo "Estadísticas de la base de datos..."
    psql -U postgres -d sigefve -c "
        SELECT 
            'Vehículos' as tabla, COUNT(*) as total 
        FROM vehiculos
        UNION ALL
        SELECT 
            'Telemetría' as tabla, COUNT(*) as total 
        FROM telemetria
        UNION ALL
        SELECT 
            'Rutas' as tabla, COUNT(*) as total 
        FROM rutas
        UNION ALL
        SELECT 
            'Entregas' as tabla, COUNT(*) as total 
        FROM entregas;
    "
}

# ==================== EJEMPLOS DE PETICIONES ====================

# Crear vehículo de ejemplo
create_sample_vehicle() {
    echo "Creando vehículo de ejemplo..."
    curl -X POST http://localhost:8585/vehiculos \
        -H "Content-Type: application/json" \
        -d '{
            "tipo": "MOTO_ELECTRICA",
            "placa": "MOTO-999",
            "modelo": "Super Soco TC Max",
            "anio": 2024,
            "capacidadBateria": 3.0,
            "autonomiaMaxima": 80.0,
            "capacidadCarga": 50.0,
            "tieneTopCase": true
        }' | jq
}

# Cambiar estado de vehículo
change_vehicle_state() {
    local vehicle_id=${1:-1}
    local state=${2:-DISPONIBLE}
    echo "Cambiando estado del vehículo $vehicle_id a $state..."
    curl -X PUT "http://localhost:8585/vehiculos/$vehicle_id/estado" \
        -H "Content-Type: application/json" \
        -d "{\"estado\": \"$state\"}" | jq
}

# Crear ruta de ejemplo
create_sample_route() {
    echo " Creando ruta de ejemplo..."
    curl -X POST http://localhost:8585/rutas \
        -H "Content-Type: application/json" \
        -d '{
            "nombre": "Ruta de Prueba",
            "distanciaTotal": 25.5
        }' | jq
}

# Asignar vehículo a ruta
assign_vehicle_to_route() {
    local route_id=${1:-1}
    local vehicle_id=${2:-1}
    echo "Asignando vehículo $vehicle_id a ruta $route_id..."
    curl -X PUT "http://localhost:8585/rutas/$route_id/asignar" \
        -H "Content-Type: application/json" \
        -d "{\"vehiculoId\": $vehicle_id}" | jq
}

# ==================== AYUDA ====================

help() {
    cat << EOF
SIGEFVE - Scripts de Utilidad del Módulo Java

COMPILACIÓN Y EJECUCIÓN:
  compile              - Compilar el proyecto
  package              - Empaquetar en JAR
  run                  - Ejecutar la aplicación
  build_and_run        - Compilar y ejecutar

DOCKER:
  docker_build         - Construir imagen Docker
  docker_run           - Ejecutar contenedor
  docker_compose_up    - Iniciar con Docker Compose
  docker_compose_down  - Detener Docker Compose
  logs                 - Ver logs del contenedor

BASE DE DATOS:
  create_db            - Crear base de datos
  drop_db              - Eliminar base de datos
  reset_db             - Reiniciar base de datos
  connect_db           - Conectar a PostgreSQL
  init_test_data       - Cargar datos de prueba
  db_stats             - Ver estadísticas de BD

PRUEBAS Y MONITOREO:
  test                 - Ejecutar pruebas
  health_check         - Verificar salud del servicio
  list_vehicles        - Listar todos los vehículos
  get_telemetry [id]   - Obtener telemetría de vehículo
  list_routes          - Listar rutas activas
  monitor_telemetry    - Monitorear telemetría en tiempo real

DESARROLLO:
  clean                - Limpiar archivos compilados
  restart              - Reiniciar servicio Docker

EJEMPLOS:
  create_sample_vehicle              - Crear vehículo de prueba
  change_vehicle_state [id] [estado] - Cambiar estado de vehículo
  create_sample_route                - Crear ruta de prueba
  assign_vehicle_to_route [rid] [vid] - Asignar vehículo a ruta

USO:
  source scripts.sh
  <nombre_funcion> [argumentos]

EJEMPLOS:
  source scripts.sh
  build_and_run
  health_check
  get_telemetry 1
  change_vehicle_state 1 CARGANDO
EOF
}

# Mostrar ayuda si se ejecuta directamente
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    help
fi

# ==================== .gitignore ====================
# Crear archivo .gitignore
create_gitignore() {
    cat > .gitignore << 'EOF'
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# Java
*.class
*.log
*.jar
*.war
*.ear
*.zip
*.tar.gz
*.rar
hs_err_pid*

# IDE
.idea/
*.iml
.vscode/
*.swp
*.swo
*~
.DS_Store

# Eclipse
.classpath
.project
.settings/

# NetBeans
nbproject/
build/
nbbuild/
dist/
nbdist/

# Logs
logs/
*.log

# Configuración local
application-local.properties
.env

# Base de datos local
*.db
*.sqlite

# Docker
.dockerignore

# Sistema operativo
Thumbs.db
.DS_Store

# Temporal
tmp/
temp/
*.tmp
EOF
    echo "✓ Archivo .gitignore creado"
}

# ==================== .dockerignore ====================
create_dockerignore() {
    cat > .dockerignore << 'EOF'
# Maven
target/

# Git
.git/
.gitignore

# IDE
.idea/
.vscode/
*.iml

# Documentación
README.md
*.md

# Docker
Dockerfile
docker-compose.yml
.dockerignore

# Scripts
scripts.sh

# Logs
*.log
logs/

# Temporal
tmp/
*.tmp
EOF
    echo "✓ Archivo .dockerignore creado"
}

# Crear todos los archivos de configuración
setup() {
    echo "Configurando proyecto..."
    create_gitignore
    create_dockerignore
    echo "Configuración completada"
}

# ==================== EXPORTAR FUNCIONES ====================
export -f compile package run build_and_run
export -f docker_build docker_run docker_compose_up docker_compose_down
export -f create_db drop_db reset_db connect_db init_test_data
export -f test health_check list_vehicles get_telemetry list_routes
export -f clean logs restart monitor_telemetry db_stats
export -f create_sample_vehicle change_vehicle_state create_sample_route assign_vehicle_to_route
export -f help setup

echo "Scripts cargados. Ejecuta 'help' para ver opciones disponibles."