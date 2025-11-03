package com.sigefve;

import com.sigefve.config.ConfiguracionBaseDatos;
import com.sigefve.controladores.ControladorRutas;
import com.sigefve.controladores.ControladorTelemetria;
import com.sigefve.controladores.ControladorVehiculos;
import com.sigefve.simulador.SimuladorTelemetria;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ServidorHTTP {
    private static final int PUERTO = Integer.parseInt(
        System.getenv().getOrDefault("JAVA_PORT", "8585")
    );
    
    private HttpServer servidor;
    private SimuladorTelemetria simulador;

    public void iniciar() throws Exception {
        System.out.println("Iniciando SIGEFVE - Modulo Java");
        System.out.println("=======================================");
        
        // Inicializar base de datos
        System.out.println("Conectando a PostgreSQL...");
        ConfiguracionBaseDatos.obtenerInstancia();
        
        // Crear servidor HTTP
        servidor = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        servidor.setExecutor(Executors.newFixedThreadPool(10));
        
        // Registrar controladores
        servidor.createContext("/vehiculos", new ControladorVehiculos());
        servidor.createContext("/telemetria", new ControladorTelemetria());
        servidor.createContext("/rutas", new ControladorRutas());
        
        // Health check
        servidor.createContext("/health", exchange -> {
            String respuesta = "{\"status\":\"OK\",\"servicio\":\"SIGEFVE-Java\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, respuesta.length());
            exchange.getResponseBody().write(respuesta.getBytes());
            exchange.getResponseBody().close();
        });
        servidor.createContext("/", exchange -> {
            String respuesta = """
            Este es el api.<br>
            A continuacion se muestran los endpoint:<br>


            API REST Endpoints
Veh&iacute;culos
GET /vehiculos

Obtener todos los veh&iacute;culos
Response: 200 OK con array de veh&iacute;culos
GET /vehiculos/:id

Obtener un veh&iacute;culo por ID
Response: 200 OK con veh&iacute;culo o 404 Not Found
GET /vehiculos?estado=DISPONIBLE

Filtrar veh&iacute;culos por estado
Response: 200 OK con array de veh&iacute;culos
POST /vehiculos

Crear un nuevo veh&iacute;culo
Body ejemplo:
{
  \"tipo\": \"VAN\",
  \"placa\": \"VAN-001\",
  \"modelo\": \"Mercedes eSprinter\",
  \"anio\": 2023,
  \"capacidadBateria\": 90.0,
  \"autonomiaMaxima\": 150.0,
  \"capacidadCarga\": 1500.0,
  \"numeroAsientos\": 3
}
Response: 201 Created con ID del veh&iacute;culo
PUT /vehiculos/:id

Actualizar un veh&iacute;culo completo
Body: Mismo formato que POST
Response: 200 OK
PUT /vehiculos/:id/estado

Cambiar solo el estado de un veh&iacute;culo
Body:
{
  \"estado\": \"EN_RUTA\"
}
Response: 200 OK
DELETE /vehiculos/:id

Eliminar un veh&iacute;culo
Response: 200 OK o 404 Not Found
Telemetr&iacute;a
GET /telemetria/vehiculo/:id

Obtener historial de telemetr&iacute;a de un veh&iacute;culo
Query params: ?limite=100 (opcional, default: 100)
Response: 200 OK con array de telemetr&iacute;a
GET /telemetria/vehiculo/:id/ultima

Obtener la $uacute;ltima telemetr&iacute;a de un veh&iacute;culo
Response: 200 OK con telemetr&iacute;a o 404 Not Found
POST /telemetria

Registrar nueva telemetr&iacute;a (normalmente usado por el simulador)
Body:
{
  \"vehiculoId\": 1,
  \"nivelBateria\": 75.5,
  \"latitud\": 20.5288,
  \"longitud\": -100.8157,
  \"temperaturaMotor\": 45.2,
  \"velocidadActual\": 60.0,
  \"kilometrajeActual\": 1250.5
}
Response: 201 Created con ID
Rutas
GET /rutas

Obtener todas las rutas activas (no completadas)
Response: 200 OK con array de rutas
GET /rutas/:id

Obtener una ruta por ID (incluye entregas)
Response: 200 OK con ruta o 404 Not Found
GET /rutas/:id/entregas

Obtener entregas de una ruta espec&iacute;fica
Response: 200 OK con array de entregas
POST /rutas

Crear una nueva ruta
Body:
{
  \"nombre\": \"Entregas Zona Centro\",
  \"distanciaTotal\": 15.5,
  \"vehiculoId\": 1
}
Response: 201 Created con ID
POST /rutas/:id/entregas

Agregar una entrega a una ruta
Body:
{
  \"direccionDestino\": \"Av. Ju&aacute;rez 123\",
  \"latitud\": 20.5288,
  \"longitud\": -100.8157,
  \"descripcionPaquete\": \"Documentos importantes\",
  \"pesoKg\": 2.5
}
Response: 201 Created con ID
PUT /rutas/:id/asignar

Asignar un veh&iacute;culo a una ruta
Body:
{
  \"vehiculoId\": 1
}
Response: 200 OK
Nota: Cambia autom&aacute;ticamente el estado del veh&iacute;culo a EN_RUTA
PUT /rutas/:id/completar

Marcar una ruta como completada
Response: 200 OK
Nota: Cambia autom&aacute;ticamente el estado del veh&iacute;culo a DISPONIBLE
            """;
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, respuesta.length());
            exchange.getResponseBody().write(respuesta.getBytes());
            exchange.getResponseBody().close();
        });
        
        servidor.start();
        System.out.println("Servidor HTTP iniciado en puerto " + PUERTO);
        
        // Iniciar simulador de telemetria
        simulador = new SimuladorTelemetria();
        simulador.iniciar();
        
        System.out.println("=======================================");
        System.out.println("SIGEFVE - Modulo Java en ejecucion");
        System.out.println("API disponible en: http://localhost:" + PUERTO);
        System.out.println("=======================================\n");
    }

    public void detener() {
        System.out.println("\n Deteniendo servidor...");
        if (simulador != null) {
            simulador.detener();
        }
        if (servidor != null) {
            servidor.stop(0);
        }
        System.out.println("Servidor detenido correctamente");
    }

    public static void main(String[] args) {
        ServidorHTTP servidor = new ServidorHTTP();
        
        // Manejar cierre graceful
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::detener));
        
        try {
            servidor.iniciar();
            Thread.currentThread().join(); // Mantener el servidor ejecutandose
        } catch (Exception e) {
            System.err.println(" Error al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}