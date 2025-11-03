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
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>SIGEFVE - Demo UI API (Single page)</title>
  <style>
    :root{--bg:#0f1724;--card:#0b1220;--muted:#9aa4b2;--accent:#06b6d4;--success:#16a34a}
    html,body{height:100%;margin:0;font-family:Inter,Segoe UI,Helvetica,Arial;background:linear-gradient(180deg,#071226 0%, #07182b 100%);color:#e6eef6}
    .wrap{max-width:1100px;margin:28px auto;padding:20px}
    header{display:flex;align-items:center;gap:16px}
    h1{margin:0;font-size:20px}
    .card{background:linear-gradient(180deg, rgba(255,255,255,0.02), rgba(255,255,255,0.01));border:1px solid rgba(255,255,255,0.03);padding:16px;border-radius:12px;margin-top:14px}
    .controls{display:flex;gap:8px;flex-wrap:wrap}
    input[type=text], input[type=number], select, textarea{background:#071226;border:1px solid rgba(255,255,255,0.03);color:var(--muted);padding:8px;border-radius:8px;min-width:160px}
    button{background:var(--accent);border:none;color:#042026;padding:8px 12px;border-radius:10px;cursor:pointer}
    button.secondary{background:#1f2937;color:var(--muted)}
    nav{display:flex;gap:6px;margin-top:12px}
    nav button{background:transparent;border:1px solid rgba(255,255,255,0.03);color:var(--muted);padding:8px 10px;border-radius:8px}
    nav button.active{border-color:var(--accent);color:var(--accent)}
    .grid{display:grid;grid-template-columns:1fr 420px;gap:12px;margin-top:12px}
    table{width:100%;border-collapse:collapse}
    th,td{padding:8px;border-bottom:1px solid rgba(255,255,255,0.03);text-align:left;color:var(--muted);font-size:13px}
    pre{white-space:pre-wrap;background:#06121b;padding:10px;border-radius:8px;color:var(--muted);font-size:13px}
    label{display:block;font-size:12px;color:var(--muted);margin-bottom:6px}
    .form-row{display:flex;gap:8px}
    .muted{color:var(--muted);font-size:13px}
    .log{max-height:220px;overflow:auto;padding:8px;background:#06121b;border-radius:8px;margin-top:8px}
    .small{font-size:12px}
    .pill{display:inline-block;padding:6px 8px;border-radius:999px;background:rgba(255,255,255,0.02);color:var(--muted);border:1px solid rgba(255,255,255,0.02)}
    .status-EN_RUTA{color:#f59e0b}
    .status-DISPONIBLE{color:var(--success)}
  </style>
</head>
<body>
  <div class="wrap">
    <header>
      <div style="width:56px;height:56px;border-radius:10px;background:linear-gradient(90deg,#042f3a,#063141);display:flex;align-items:center;justify-content:center;font-weight:700">SIG</div>
      <div>
        <h1>SIGEFVE - Demo cliente API (single page)</h1>
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