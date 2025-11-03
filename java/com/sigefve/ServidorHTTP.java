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
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, respuesta.length());
            exchange.getResponseBody().write(respuesta.getBytes());
            exchange.getResponseBody().close();
        });
        servidor.createContext("/api-cliente", exchange -> {
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
                <div class="muted small">Interfaz de pruebas para los endpoints: Veh&iacute;culos, Telemetr&iacute;a y Rutas</div>
      </div>
    </header>

    <div class="card">
      <div style="display:flex;justify-content:space-between;align-items:center;gap:12px;flex-wrap:wrap">
        <div class="controls">
          <label class="small">Base URL (ej. https://tajava.xipatlani.tk)</label>
          <input id="baseUrl" type="text" placeholder="https://tajava.xipatlani.tk" value="https://tajava.xipatlani.tk">
          <button id="btnPing" class="secondary">Probar API</button>
          <span id="pingResult" class="muted small" style="align-self:center;margin-left:6px"></span>
        </div>
        <div>
          <span class="pill">CORS: la API debe permitir or&iacute;genes</span>
        </div>
      </div>

      <nav id="mainTabs" style="margin-top:12px">
        <button data-tab="vehiculos" class="active">Veh&iacute;culos</button>
        <button data-tab="telemetria">Telemetr&iacute;a</button>
        <button data-tab="rutas">Rutas</button>
        <button data-tab="raw">Raw / Consola</button>
      </nav>

      <div class="grid">
        <main class="card" id="contentMain" style="overflow:auto">
          <!-- dynamic content here -->
        </main>

        <aside class="card">
          <div>
            <strong>Registro / Consola</strong>
            <div id="log" class="log"></div>
          </div>
        </aside>
      </div>
    </div>

    <footer style="margin-top:12px;text-align:center;color:var(--muted);font-size:13px">Demo UI - endpoints seg&uacute;n especificaci&oacute;n proporcionada. Aseg&uacute;rate de que la API soporte JSON y CORS.</footer>
  </div>

  <script>
    // ---------- helpers
    const $ = sel => document.querySelector(sel)
    const baseUrlInput = $('#baseUrl')
    const logEl = $('#log')

    function log(msg, type='info'){
      const time = new Date().toLocaleTimeString()
      const el = document.createElement('div')
      el.innerHTML = `<span style="color:#7f8fa4;font-size:12px">[${time}]</span> ${msg}`
      logEl.prepend(el)
    }

    async function apiFetch(path, opts={}){
      const url = (baseUrlInput.value.replace(/\\/+$/,'')||'https://tajava.xipatlani.tk') + path
      log(`<strong>FETCH</strong> ${opts.method || 'GET'} ${url}`)
      try{
        const res = await fetch(url, {...opts, headers:{'Content-Type':'application/json', ...(opts.headers||{})}})
        const text = await res.text()
        let data
        try{ data = text ? JSON.parse(text) : null }catch(e){ data = text }
        log(`<em>RESPONSE</em> ${res.status} ${res.statusText} - ${typeof data==='string'?data:JSON.stringify(data)}`)
        if(!res.ok) throw {status: res.status, data}
        return {status:res.status, data}
      }catch(err){
        log(`<span style="color:#ff7b7b">ERROR</span> ${err.status||''} ${err.message||JSON.stringify(err.data||err)} `)
        throw err
      }
    }

    // ---------- Tabs
    document.getElementById('mainTabs').addEventListener('click', e=>{
      if(e.target.matches('button')){
        document.querySelectorAll('#mainTabs button').forEach(b=>b.classList.remove('active'))
        e.target.classList.add('active')
        showTab(e.target.dataset.tab)
      }
    })

    function showTab(tab){
      const main = $('#contentMain')
      if(tab==='vehiculos') return renderVehiculos(main)
      if(tab==='telemetria') return renderTelemetria(main)
      if(tab==='rutas') return renderRutas(main)
      if(tab==='raw') return renderRaw(main)
    }

    // ---------- VEH&iacute;CULOS UI
    async function renderVehiculos(container){
      container.innerHTML = `
        <h3>Veh&iacute;culos</h3>
        <div style="display:flex;gap:8px;align-items:end;flex-wrap:wrap">
          <div style="min-width:220px">
            <label>ID (para GET/PUT/DELETE)</label>
            <input id="vehId" type="text" placeholder="ej. 1">
          </div>
          <div>
            <button id="btnList">Listar todos</button>
            <button id="btnGet">Obtener</button>
            <button id="btnDelete" class="secondary">Eliminar</button>
          </div>
        </div>

        <hr>

        <div style="display:flex;gap:8px;align-items:flex-start">
          <section style="flex:1">
            <h4>Crear / Actualizar veh&iacute;culo</h4>
            <div id="formVeh">
              <label>tipo</label><input id="f_tipo" type="text" value="VAN">
              <label>placa</label><input id="f_placa" type="text" value="VAN-001">
              <label>modelo</label><input id="f_modelo" type="text" value="Mercedes eSprinter">
              <div class="form-row">
                <div>
                  <label>anio</label><input id="f_anio" type="number" value="2023">
                </div>
                <div>
                  <label>capacidadBateria (kWh)</label><input id="f_capacidadBateria" type="number" value="90">
                </div>
              </div>
              <div class="form-row">
                <div>
                  <label>autonomiaMaxima (km)</label><input id="f_autonomiaMaxima" type="number" value="150">
                </div>
                <div>
                  <label>capacidadCarga (kg)</label><input id="f_capacidadCarga" type="number" value="1500">
                </div>
              </div>
              <label>numeroAsientos</label><input id="f_numeroAsientos" type="number" value="3">
              <div style="margin-top:8px">
                <button id="btnCreate">Crear (POST)</button>
                <button id="btnPut" class="secondary">Actualizar completo (PUT /vehiculos/:id)</button>
                <button id="btnChangeEstado" class="secondary">Cambiar estado (PUT /vehiculos/:id/estado)</button>
              </div>
            </div>
          </section>

          <section style="width:320px">
            <h4>Resultado / Lista</h4>
            <div id="vehList" style="max-height:420px;overflow:auto"></div>
          </section>
        </div>
      `

      document.getElementById('btnList').onclick = async()=>{
        try{ const r = await apiFetch('/vehiculos'); showVehList(r.data) }catch(e){}
      }
      document.getElementById('btnGet').onclick = async()=>{
        const id = $('#vehId').value.trim(); if(!id){alert('ID requerido');return}
        try{ const r = await apiFetch(`/vehiculos/${id}`); showVehDetail(r.data) }catch(e){if(e.status===404) alert('No encontrado')}
      }
      document.getElementById('btnDelete').onclick = async()=>{
        const id = $('#vehId').value.trim(); if(!id){alert('ID requerido');return}
        if(!confirm('Eliminar veh&iacute;culo '+id+'?')) return
        try{ await apiFetch(`/vehiculos/${id}`,{method:'DELETE'}); alert('Eliminado') }catch(e){if(e.status===404) alert('No encontrado')}
      }

      document.getElementById('btnCreate').onclick = async()=>{
        const body = readVehForm()
        try{ const r = await apiFetch('/vehiculos', {method:'POST', body: JSON.stringify(body)}); alert('Creado ID: '+r.data) }catch(e){}
      }

      document.getElementById('btnPut').onclick = async()=>{
        const id = $('#vehId').value.trim(); if(!id){alert('ID requerido');return}
        const body = readVehForm()
        try{ await apiFetch(`/vehiculos/${id}`,{method:'PUT', body: JSON.stringify(body)}); alert('Actualizado') }catch(e){if(e.status===404) alert('No encontrado')}
      }

      document.getElementById('btnChangeEstado').onclick = async()=>{
        const id = $('#vehId').value.trim(); if(!id){alert('ID requerido');return}
        const nuevo = prompt('Nuevo estado (ej. EN_RUTA o DISPONIBLE)')
        if(!nuevo) return
        try{ await apiFetch(`/vehiculos/${id}/estado`,{method:'PUT', body: JSON.stringify({estado:nuevo})}); alert('Estado cambiado') }catch(e){if(e.status===404) alert('No encontrado')}
      }

      // auto-list
      try{ const r = await apiFetch('/vehiculos'); showVehList(r.data) }catch(e){}
    }

    function readVehForm(){
      return {
        tipo: $('#f_tipo').value,
        placa: $('#f_placa').value,
        modelo: $('#f_modelo').value,
        anio: Number($('#f_anio').value)||0,
        capacidadBateria: Number($('#f_capacidadBateria').value)||0,
        autonomiaMaxima: Number($('#f_autonomiaMaxima').value)||0,
        capacidadCarga: Number($('#f_capacidadCarga').value)||0,
        numeroAsientos: Number($('#f_numeroAsientos').value)||0
      }
    }

    function showVehList(list){
      const el = document.getElementById('vehList')
      if(!list || !list.length){ el.innerHTML = '<div class="muted">No hay veh&iacute;culos</div>'; return }
      const rows = list.map(v=>{
        const s = v.estado ? `<span class="pill status-${v.estado}">${v.estado}</span>` : ''
        return `<div style="padding:8px;border-bottom:1px solid rgba(255,255,255,0.02);">
          <strong>${v.id ? v.id+' - ' : ''}${v.placa || ''}</strong> ${s}<div class="muted">${v.tipo || ''} * ${v.modelo || ''} * ${v.anio || ''}</div>
          <div style="margin-top:6px"><button onclick="fetchVeh(${v.id})">Ver</button> <button onclick="prefillVeh(${encodeURIComponent(JSON.stringify(v))})" class="secondary">Editar</button></div>
        </div>`
      }).join('')
      el.innerHTML = rows
    }

    window.fetchVeh = async function(id){
      try{ const r = await apiFetch('/vehiculos/'+id); showVehDetail(r.data) }catch(e){if(e.status===404) alert('No encontrado')}
    }

    window.prefillVeh = function(encoded){
      const v = JSON.parse(decodeURIComponent(encoded))
      $('#vehId').value = v.id || ''
      $('#f_tipo').value = v.tipo || ''
      $('#f_placa').value = v.placa || ''
      $('#f_modelo').value = v.modelo || ''
      $('#f_anio').value = v.anio || ''
      $('#f_capacidadBateria').value = v.capacidadBateria || ''
      $('#f_autonomiaMaxima').value = v.autonomiaMaxima || ''
      $('#f_capacidadCarga').value = v.capacidadCarga || ''
      $('#f_numeroAsientos').value = v.numeroAsientos || ''
    }

    function showVehDetail(v){
      const el = document.getElementById('vehList')
      el.innerHTML = `<pre>${JSON.stringify(v, null, 2)}</pre>`
    }

    // ---------- TELEMETR&iacute;A UI
    async function renderTelemetria(container){
      container.innerHTML = `
        <h3>Telemetr&iacute;a</h3>
        <div style="display:flex;gap:8px;align-items:end;margin-bottom:8px">
          <div>
            <label>ID veh&iacute;culo</label>
            <input id="t_vid" type="text" placeholder="1">
          </div>
          <div>
            <label>l&iacute;mite</label>
            <input id="t_limit" type="number" value="100">
          </div>
          <div>
            <button id="t_list">Listar historial</button>
            <button id="t_last">&uacute;ltima telemetr&iacute;a</button>
          </div>
        </div>

        <h4>Registrar telemetr&iacute;a (simulador)</h4>
        <div>
          <label>vehiculoId</label><input id="t_vehId" type="number" value="1">
          <label>nivelBateria</label><input id="t_nivelBateria" type="number" value="75.5">
          <label>latitud</label><input id="t_lat" type="number" value="20.5288">
          <label>longitud</label><input id="t_lon" type="number" value="-100.8157">
          <label>temperaturaMotor</label><input id="t_temp" type="number" value="45.2">
          <label>velocidadActual</label><input id="t_vel" type="number" value="60">
          <label>kilometrajeActual</label><input id="t_km" type="number" value="1250.5">
          <div style="margin-top:8px"><button id="t_post">Registrar telemetr&iacute;a (POST)</button></div>
        </div>

        <hr>
        <div id="t_result"></div>
      `

      document.getElementById('t_list').onclick = async()=>{
        const id = $('#t_vid').value.trim(); if(!id){alert('ID requerido');return}
        const lim = $('#t_limit').value || 100
        try{ const r = await apiFetch(`/telemetria/vehiculo/${id}?limite=${lim}`); $('#t_result').innerHTML = `<pre>${JSON.stringify(r.data, null,2)}</pre>` }catch(e){if(e.status===404) alert('No encontrado')}
      }
      document.getElementById('t_last').onclick = async()=>{
        const id = $('#t_vid').value.trim(); if(!id){alert('ID requerido');return}
        try{ const r = await apiFetch(`/telemetria/vehiculo/${id}/ultima`); $('#t_result').innerHTML = `<pre>${JSON.stringify(r.data, null,2)}</pre>` }catch(e){if(e.status===404) alert('No encontrado')}
      }
      document.getElementById('t_post').onclick = async()=>{
        const body = {
          vehiculoId: Number($('#t_vehId').value), nivelBateria: Number($('#t_nivelBateria').value), latitud: Number($('#t_lat').value), longitud: Number($('#t_lon').value), temperaturaMotor: Number($('#t_temp').value), velocidadActual: Number($('#t_vel').value), kilometrajeActual: Number($('#t_km').value)
        }
        try{ const r = await apiFetch('/telemetria',{method:'POST', body: JSON.stringify(body)}); alert('Telemetr&iacute;a registrada ID: '+r.data) }catch(e){}
      }
    }

    // ---------- RUTAS UI
    async function renderRutas(container){
      container.innerHTML = `
        <h3>Rutas</h3>
        <div style="display:flex;gap:8px;align-items:end">
          <div>
            <label>ID ruta</label><input id="r_id" type="text" placeholder="1">
          </div>
          <div>
            <button id="r_list">Listar activas</button>
            <button id="r_get">Obtener ruta</button>
            <button id="r_entregas">Ver entregas</button>
          </div>
        </div>

        <hr>
        <div style="display:flex;gap:12px">
          <section style="flex:1">
            <h4>Crear ruta</h4>
            <label>nombre</label><input id="r_nombre" type="text" value="Entregas Zona Centro">
            <label>distanciaTotal</label><input id="r_dist" type="number" value="15.5">
            <label>vehiculoId</label><input id="r_vid" type="number" value="1">
            <div style="margin-top:8px"><button id="r_post">Crear ruta</button></div>

            <h4 style="margin-top:12px">Agregar entrega a ruta</h4>
            <label>rutaId</label><input id="re_rutaId" type="number" value="1">
            <label>direccionDestino</label><input id="re_dir" type="text" value="Av. Ju&aacute;rez 123">
            <label>latitud</label><input id="re_lat" type="number" value="20.5288">
            <label>longitud</label><input id="re_lon" type="number" value="-100.8157">
            <label>descripcionPaquete</label><input id="re_desc" type="text" value="Documentos importantes">
            <label>pesoKg</label><input id="re_peso" type="number" value="2.5">
            <div style="margin-top:8px"><button id="re_post">Agregar entrega</button></div>

            <h4 style="margin-top:12px">Operaciones</h4>
            <label>rutaId</label><input id="op_rid" type="number" value="1">
            <label>vehiculoId (para asignar)</label><input id="op_vid" type="number" value="1">
            <div style="margin-top:8px">
              <button id="op_asign">Asignar veh&iacute;culo</button>
              <button id="op_comp">Marcar completada</button>
            </div>
          </section>

          <section style="width:340px">
            <h4>Resultado</h4>
            <div id="r_result"></div>
          </section>
        </div>
      `

      document.getElementById('r_list').onclick = async()=>{
        try{ const r = await apiFetch('/rutas'); $('#r_result').innerHTML = `<pre>${JSON.stringify(r.data, null,2)}</pre>` }catch(e){}
      }
      document.getElementById('r_get').onclick = async()=>{
        const id = $('#r_id').value.trim(); if(!id){alert('ID requerido');return}
        try{ const r = await apiFetch(`/rutas/${id}`); $('#r_result').innerHTML = `<pre>${JSON.stringify(r.data, null,2)}</pre>` }catch(e){if(e.status===404) alert('No encontrado')}
      }
      document.getElementById('r_entregas').onclick = async()=>{
        const id = $('#r_id').value.trim(); if(!id){alert('ID requerido');return}
        try{ const r = await apiFetch(`/rutas/${id}/entregas`); $('#r_result').innerHTML = `<pre>${JSON.stringify(r.data, null,2)}</pre>` }catch(e){if(e.status===404) alert('No encontrado')}
      }

      document.getElementById('r_post').onclick = async()=>{
        const body = {nombre: $('#r_nombre').value, distanciaTotal: Number($('#r_dist').value)||0, vehiculoId: Number($('#r_vid').value)||null}
        try{ const r = await apiFetch('/rutas',{method:'POST', body: JSON.stringify(body)}); alert('Ruta creada ID: '+r.data) }catch(e){}
      }
      document.getElementById('re_post').onclick = async()=>{
        const rutaId = Number($('#re_rutaId').value)
        const body = {direccionDestino: $('#re_dir').value, latitud: Number($('#re_lat').value), longitud: Number($('#re_lon').value), descripcionPaquete: $('#re_desc').value, pesoKg: Number($('#re_peso').value)}
        try{ const r = await apiFetch(`/rutas/${rutaId}/entregas`,{method:'POST', body: JSON.stringify(body)}); alert('Entrega agregada ID: '+r.data) }catch(e){}
      }

      document.getElementById('op_asign').onclick = async()=>{
        const id = $('#op_rid').value; const vid = Number($('#op_vid').value)
        try{ await apiFetch(`/rutas/${id}/asignar`,{method:'PUT', body: JSON.stringify({vehiculoId: vid})}); alert('Veh&iacute;culo asignado') }catch(e){}
      }
      document.getElementById('op_comp').onclick = async()=>{
        const id = $('#op_rid').value
        try{ await apiFetch(`/rutas/${id}/completar`,{method:'PUT'}); alert('Ruta marcada como completada') }catch(e){}
      }
    }

    // ---------- RAW / Consola
    function renderRaw(container){
      container.innerHTML = `
        <h3>Raw / Peticiones manuales</h3>
        <div>
          <label>Path (ej. /vehiculos)</label>
          <input id="raw_path" type="text" value="/vehiculos">
          <label>M&eacute;todo</label>
          <select id="raw_method"><option>GET</option><option>POST</option><option>PUT</option><option>DELETE</option></select>
          <label>Body JSON (opcional)</label>
          <textarea id="raw_body" rows="6" style="width:100%;background:#06121b;border-radius:8px;color:var(--muted);padding:8px"></textarea>
          <div style="margin-top:8px"><button id="raw_send">Enviar</button></div>
          <div id="raw_out" style="margin-top:8px"></div>
        </div>
      `
      document.getElementById('raw_send').onclick = async()=>{
        const path = $('#raw_path').value
        const method = $('#raw_method').value
        const body = $('#raw_body').value.trim()
        try{ const r = await apiFetch(path, {method, body: body? body: undefined}); document.getElementById('raw_out').innerHTML = `<pre>${JSON.stringify(r.data, null,2)}</pre>` }catch(e){document.getElementById('raw_out').innerHTML = `<pre style="color:#ffb4b4">ERROR ${e.status||''}</pre>`}
      }
    }

    // ---------- Ping button
    $('#btnPing').onclick = async()=>{
      const testPath = '/vehiculos'
      try{ await apiFetch(testPath); $('#pingResult').textContent = 'OK' }catch(e){ $('#pingResult').textContent = 'Error' }
    }

    // init
    showTab('vehiculos')
  </script>
</body>
</html>
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