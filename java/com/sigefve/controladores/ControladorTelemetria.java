package com.sigefve.controladores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.GsonBuilder;
// import com.google.gson.JsonSerializer;
// import com.google.gson.JsonDeserializer;
// import com.google.gson.*;
import com.sigefve.modelos.Telemetria;
import com.sigefve.servicios.TelemetriaServicio;
import com.sigefve.adaptadores.LocalDateTimeTypeAdapter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
//import java.time.LocalDate;

// import java.lang.reflect.Type;

public class ControladorTelemetria implements HttpHandler {
    private final TelemetriaServicio telemetriaServicio;
    private final Gson gson;

    public ControladorTelemetria() {
        this.telemetriaServicio = new TelemetriaServicio();
        // this.gson = new Gson();
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        configurarCORS(exchange);
        
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            enviarRespuesta(exchange, 200, "");
            return;
        }

        try {
            String metodo = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] partes = path.split("/");

            if (metodo.equals("GET")) {
                manejarGET(exchange, partes);
            } else if (metodo.equals("POST")) {
                manejarPOST(exchange);
            } else {
                enviarError(exchange, 405, "Metodo no permitido");
            }
        } catch (Exception e) {
            enviarError(exchange, 500, "Error interno: " + e.getMessage());
        }
    }

    private void manejarGET(HttpExchange exchange, String[] partes) throws IOException {
        try {
            // GET /telemetria/vehiculo/:id/ultima
            if (partes.length >= 5 && partes[4].equals("ultima")) {
                Long vehiculoId = Long.parseLong(partes[3]);
                Telemetria telemetria = telemetriaServicio.obtenerUltimaTelemetria(vehiculoId);
                
                if (telemetria != null) {
                    enviarJSON(exchange, 200, telemetria);
                } else {
                    enviarError(exchange, 404, "No hay telemetria para este vehiculo");
                }
            }
            // GET /telemetria/vehiculo/:id?limite=100
            else if (partes.length >= 4) {
                Long vehiculoId = Long.parseLong(partes[3]);
                String query = exchange.getRequestURI().getQuery();
                int limite = 100;
                
                if (query != null) {
                    Map<String, String> params = parsearParametros(query);
                    if (params.containsKey("limite")) {
                        limite = Integer.parseInt(params.get("limite"));
                    }
                }
                
                List<Telemetria> historial = telemetriaServicio.obtenerHistorial(vehiculoId, limite);
                enviarJSON(exchange, 200, historial);
            }
            else if (partes.length==1) {
                String respuesta = """
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8" />
<title>SIGEFVE API Documentation</title>
<meta name=&quot;description&quot; content=&quot;SIGEFVE: SIstema de GEsti&oacute;n de Flota de Veh&iacute;culos El&eacute;ctricos (SIGEFVE) - Documentaci&oacute;n de API REST&quot;>
<meta property=&quot;og:title&quot; content=&quot;SIGEFVE API&quot;>
<meta property=&quot;og:description&quot; content=&quot;SIstema de GEsti&oacute;n de Flota de Veh&iacute;culos El&eacute;ctricos (SIGEFVE) - Documentaci&oacute;n de API REST&quot;>
<meta property=&quot;og:image&quot; content=&quot;/mnt/data/SIGEFVE.png&quot;>
<style>
body{font-family:Arial,Helvetica,sans-serif;background:#f6f7fb;margin:0;padding-top:60px}
.fixedtoc{position:fixed;top:0;left:0;right:0;background:#000;color:#fff;padding:10px;z-index:999;display:flex;gap:20px}
.fixedtoc a{color:#fff;text-decoration:none;font-weight:bold;font-size:14px}
h1{display:none}
.section{border-bottom:1px solid #ddd;padding:20px}
.endpoint{margin:10px 0;border-radius:6px;overflow:hidden;border-left:8px solid #888;background:#fff}
.endpoint summary{cursor:pointer;padding:12px 15px;font-weight:bold;font-size:15px}
.method{display:inline-block;padding:2px 6px;border-radius:4px;color:#fff;font-size:11px;margin-right:6px}
.GET{background:#2ecc71}.POST{background:#3498db}.PUT{background:#f39c12}.DELETE{background:#e74c3c}
.endpoint.GET{border-left-color:#2ecc71}.endpoint.POST{border-left-color:#3498db}.endpoint.PUT{border-left-color:#f39c12}.endpoint.DELETE{border-left-color:#e74c3c}
.desc{padding:0 15px 15px 15px}
pre{background:#222;color:#0f0;padding:10px;border-radius:6px;font-size:12px;overflow:auto;position:relative}
.copybtn{position:absolute;top:5px;right:5px;font-size:10px;background:#555;color:#fff;border:none;padding:3px 5px;cursor:pointer;border-radius:3px}
.baseurlbox{background:#fff;padding:10px;border-bottom:1px solid #ccc}
</style>
<script>
function cp(t){navigator.clipboard.writeText(t.innerText)}
</script>
</head>
<body>
<div class="fixedtoc">
<a href="#vehiculos">Veh&iacute;culos</a>
<a href="#telemetria">Telemetr&iacute;a</a>
<a href="#rutas">Rutas</a>
</div>
<div class="baseurlbox">Base URL: <input value="https://tajava.xipatlani.tk" style="width:250px"></div>
<div class="section" id="vehiculos">
<h2>Veh&iacute;culos</h2>
<div class="endpoint GET"><summary><span class="method GET">GET</span>/vehiculos</summary><div class="desc">Obtener todos los veh&iacute;culos</div></div>
<div class="endpoint GET"><summary><span class="method GET">GET</span>/vehiculos/:id</summary><div class="desc">Obtener un veh&iacute;culo por ID</div></div>
<div class="endpoint GET"><summary><span class="method GET">GET</span>/vehiculos?estado=DISPONIBLE</summary><div class="desc">Filtrar veh&iacute;culos por estado</div></div>
<div class="endpoint POST"><summary><span class="method POST">POST</span>/vehiculos</summary><div class="desc">Crear veh&iacute;culo<pre><button class="copybtn" onclick="cp(this.parentNode)">copiar</button>{
  &quot;tipo&quot;:&quot;VAN&quot;,
  &quot;placa&quot;:&quot;VAN-001&quot;,
  &quot;modelo&quot;:&quot;Mercedes eSprinter&quot;,
  &quot;anio&quot;:2023,
  &quot;capacidadBateria&quot;:90.0,
  &quot;autonomiaMaxima&quot;:150.0,
  &quot;capacidadCarga&quot;:1500.0,
  &quot;numeroAsientos&quot;:3
}</pre></div></div>
<div class="endpoint PUT"><summary><span class="method PUT">PUT</span>/vehiculos/:id</summary><div class="desc">Actualizar un veh&iacute;culo (mismo body que POST)</div></div>
<div class="endpoint PUT"><summary><span class="method PUT">PUT</span>/vehiculos/:id/estado</summary><div class="desc">Cambiar estado<pre><button class="copybtn" onclick="cp(this.parentNode)">copiar</button>{&quot;estado&quot;:&quot;EN_RUTA&quot;}</pre></div></div>
<div class="endpoint DELETE"><summary><span class="method DELETE">DELETE</span>/vehiculos/:id</summary><div class="desc">Eliminar un veh&iacute;culo</div></div>
</div>
<div class="section" id="telemetria">
<h2>Telemetr&iacute;a</h2>
<div class="endpoint GET"><summary><span class="method GET">GET</span>/telemetria/vehiculo/:id</summary><div class="desc">Historial (limite param opcional)</div></div>
<div class="endpoint GET"><summary><span class="method GET">GET</span>/telemetria/vehiculo/:id/ultima</summary><div class="desc">&Uacute;ltima telemetr&iacute;a</div></div>
<div class="endpoint POST"><summary><span class="method POST">POST</span>/telemetria</summary><div class="desc">Registrar telemetr&iacute;a<pre><button class="copybtn" onclick="cp(this.parentNode)">copiar</button>{
  &quot;vehiculoId&quot;:1,
  &quot;nivelBateria&quot;:75.5,
  &quot;latitud&quot;:20.5288,
  &quot;longitud&quot;:-100.8157,
  &quot;temperaturaMotor&quot;:45.2,
  &quot;velocidadActual&quot;:60.0,
  &quot;kilometrajeActual&quot;:1250.5
}</pre></div></div>
</div>
<div class="section" id="rutas">
<h2>Rutas</h2>
<div class="endpoint GET"><summary><span class="method GET">GET</span>/rutas</summary><div class="desc">Rutas activas</div></div>
<div class="endpoint GET"><summary><span class="method GET">GET</span>/rutas/:id</summary><div class="desc">Ruta por ID</div></div>
<div class="endpoint GET"><summary><span class="method GET">GET</span>/rutas/:id/entregas</summary><div class="desc">Entregas de la ruta</div></div>
<div class="endpoint POST"><summary><span class="method POST">POST</span>/rutas</summary><div class="desc">Crear ruta<pre><button class="copybtn" onclick="cp(this.parentNode)">copiar</button>{
  &quot;nombre&quot;:&quot;Entregas Zona Centro&quot;,
  &quot;distanciaTotal&quot;:15.5,
  &quot;vehiculoId&quot;:1
}</pre></div></div>
<div class="endpoint POST"><summary><span class="method POST">POST</span>/rutas/:id/entregas</summary><div class="desc">Agregar entrega<pre><button class="copybtn" onclick="cp(this.parentNode)">copiar</button>{
  &quot;direccionDestino&quot;:&quot;Av. Ju&aacute;rez 123&quot;,
  &quot;latitud&quot;:20.5288,
  &quot;longitud&quot;:-100.8157,
  &quot;descripcionPaquete&quot;:&quot;Documentos importantes&quot;,
  &quot;pesoKg&quot;:2.5
}</pre></div></div>
<div class="endpoint PUT"><summary><span class="method PUT">PUT</span>/rutas/:id/asignar</summary><div class="desc"><pre><button class="copybtn" onclick="cp(this.parentNode)">copiar</button>{&quot;vehiculoId&quot;:1}</pre></div></div>
<div class="endpoint PUT"><summary><span class="method PUT">PUT</span>/rutas/:id/completar</summary><div class="desc">Completar ruta</div></div>
</div>
</body>
</html>

            """;
                enviarHTML(exchange, 200, respuesta);
            } else {
                enviarError(exchange, 400, "ID de vehiculo requerido");
            }
        } catch (Exception e) {
            enviarError(exchange, 500, e.getClass().getName() + " :> " + e.getMessage());
        }
    }

    private void manejarPOST(HttpExchange exchange) throws IOException {
        try {
            String body = leerCuerpo(exchange);
            JsonObject json = gson.fromJson(body, JsonObject.class);
            
            Telemetria telemetria = new Telemetria(
                json.get("vehiculoId").getAsLong(),
                json.get("nivelBateria").getAsDouble(),
                json.get("latitud").getAsDouble(),
                json.get("longitud").getAsDouble(),
                json.get("temperaturaMotor").getAsDouble(),
                json.get("velocidadActual").getAsDouble(),
                json.get("kilometrajeActual").getAsDouble()
            );
            
            Long id = telemetriaServicio.registrarTelemetria(telemetria);
            
            JsonObject respuesta = new JsonObject();
            respuesta.addProperty("id", id);
            respuesta.addProperty("mensaje", "Telemetria registrada exitosamente");
            
            enviarJSON(exchange, 201, respuesta);
        } catch (Exception e) {
            enviarError(exchange, 400, e.getMessage());
        }
    }

    private void configurarCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private String leerCuerpo(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private Map<String, String> parsearParametros(String query) {
        Map<String, String> params = new java.util.HashMap<>();
        for (String param : query.split("&")) {
            String[] par = param.split("=");
            if (par.length == 2) {
                params.put(par[0], par[1]);
            }
        }
        return params;
    }

    private void enviarJSON(HttpExchange exchange, int codigo, Object obj) throws IOException {

        String json = gson.toJson(obj);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        enviarRespuesta(exchange, codigo, json);
    }

    private void enviarHTML(HttpExchange exchange, int codigo, Object obj) throws IOException {

        String json = gson.toJson(obj);
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        enviarRespuesta(exchange, codigo, json);
    }

    private void enviarError(HttpExchange exchange, int codigo, String mensaje) throws IOException {
        JsonObject error = new JsonObject();
        error.addProperty("error", mensaje);
        enviarJSON(exchange, codigo, error);
    }

    private void enviarRespuesta(HttpExchange exchange, int codigo, String respuesta) throws IOException {
        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}