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
            Este es el api.<br><br>
            A continuacion se muestran los endpoint:<br><br>
<br>
<br>
            API REST Endpoints<br>
Veh&iacute;culos<br>
GET /vehiculos<br>
<br>
Obtener todos los veh&iacute;culos<br>
Response: 200 OK con array de veh&iacute;culos<br>
GET /vehiculos/:id<br>
<br>
Obtener un veh&iacute;culo por ID<br>
Response: 200 OK con veh&iacute;culo o 404 Not Found<br>
GET /vehiculos?estado=DISPONIBLE<br>
<br>
Filtrar veh&iacute;culos por estado<br>
Response: 200 OK con array de veh&iacute;culos<br>
POST /vehiculos<br>
<br>
Crear un nuevo veh&iacute;culo<br>
Body ejemplo:<br>
{<br>
  \"tipo\": \"VAN\",<br>
  \"placa\": \"VAN-001\",<br>
  \"modelo\": \"Mercedes eSprinter\",<br>
  \"anio\": 2023,<br>
  \"capacidadBateria\": 90.0,<br>
  \"autonomiaMaxima\": 150.0,<br>
  \"capacidadCarga\": 1500.0,<br>
  \"numeroAsientos\": 3<br>
}<br>
Response: 201 Created con ID del veh&iacute;culo<br>
PUT /vehiculos/:id<br>
<br>
Actualizar un veh&iacute;culo completo<br>
Body: Mismo formato que POST<br>
Response: 200 OK<br>
PUT /vehiculos/:id/estado<br>
<br>
Cambiar solo el estado de un veh&iacute;culo<br>
Body:<br>
{<br>
  \"estado\": \"EN_RUTA\"<br>
}<br>
Response: 200 OK<br>
DELETE /vehiculos/:id<br>
<br>
Eliminar un veh&iacute;culo<br>
Response: 200 OK o 404 Not Found<br>
Telemetr&iacute;a<br>
GET /telemetria/vehiculo/:id<br>
<br>
Obtener historial de telemetr&iacute;a de un veh&iacute;culo<br>
Query params: ?limite=100 (opcional, default: 100)<br>
Response: 200 OK con array de telemetr&iacute;a<br>
GET /telemetria/vehiculo/:id/ultima<br>
<br>
Obtener la $uacute;ltima telemetr&iacute;a de un veh&iacute;culo<br>
Response: 200 OK con telemetr&iacute;a o 404 Not Found<br>
POST /telemetria<br>
<br>
Registrar nueva telemetr&iacute;a (normalmente usado por el simulador)<br>
Body:<br>
{<br>
  \"vehiculoId\": 1,<br>
  \"nivelBateria\": 75.5,<br>
  \"latitud\": 20.5288,<br>
  \"longitud\": -100.8157,<br>
  \"temperaturaMotor\": 45.2,<br>
  \"velocidadActual\": 60.0,<br>
  \"kilometrajeActual\": 1250.5<br>
}<br>
Response: 201 Created con ID<br>
Rutas<br>
GET /rutas<br>
<br>
Obtener todas las rutas activas (no completadas)<br>
Response: 200 OK con array de rutas<br>
GET /rutas/:id<br>
<br>
Obtener una ruta por ID (incluye entregas)<br>
Response: 200 OK con ruta o 404 Not Found<br>
GET /rutas/:id/entregas<br>
<br>
Obtener entregas de una ruta espec&iacute;fica<br>
Response: 200 OK con array de entregas<br>
POST /rutas<br>
<br>
Crear una nueva ruta<br>
Body:<br>
{<br>
  \"nombre\": \"Entregas Zona Centro\",<br>
  \"distanciaTotal\": 15.5,<br>
  \"vehiculoId\": 1<br>
}<br>
Response: 201 Created con ID<br>
POST /rutas/:id/entregas<br>
<br>
Agregar una entrega a una ruta<br>
Body:<br>
{<br>
  \"direccionDestino\": \"Av. Ju&aacute;rez 123\",<br>
  \"latitud\": 20.5288,<br>
  \"longitud\": -100.8157,<br>
  \"descripcionPaquete\": \"Documentos importantes\",<br>
  \"pesoKg\": 2.5<br>
}<br>
Response: 201 Created con ID<br>
PUT /rutas/:id/asignar<br>
<br>
Asignar un veh&iacute;culo a una ruta<br>
Body:<br>
{<br>
  \"vehiculoId\": 1<br>
}<br>
Response: 200 OK<br>
Nota: Cambia autom&aacute;ticamente el estado del veh&iacute;culo a EN_RUTA<br>
PUT /rutas/:id/completar<br>
<br>
Marcar una ruta como completada<br>
Response: 200 OK<br>
Nota: Cambia autom&aacute;ticamente el estado del veh&iacute;culo a DISPONIBLE<br>
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