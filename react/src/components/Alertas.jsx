import React, { useEffect, useState, useRef } from "react";
import { apiFetch } from "../auth/api";

export default function Alertas() {
  const [alertas, setAlertas] = useState([]);
  const [visible, setVisible] = useState(false);
  const lastAlertId = useRef(null); // Para detectar alertas nuevas

  // 🔥 Cargar alertas desde API real
  async function cargarAlertas() {
    const data = await apiFetch("https://apisigefve.xipatlani.tk/alertas");

    if (!data || data.length === 0) return;

    setAlertas(data);

    // Detectar si hay alerta nueva
    const alertaReciente = data[0];

    if (alertaReciente.id !== lastAlertId.current) {
      lastAlertId.current = alertaReciente.id;

      // Mostrar popup
      setVisible(true);

      // Ocultar en 4 segundos
      setTimeout(() => setVisible(false), 4000);
    }
  }

  // 🔄 Recargar alertas automáticamente cada 8 segundos
  useEffect(() => {
    cargarAlertas(); // carga al inicio

    const interval = setInterval(() => {
      cargarAlertas();
    }, 8000);

    return () => clearInterval(interval);
  }, []);

  // 🛑 Si no hay alertas aún
  const ultima = alertas.length > 0 ? alertas[0] : null;

  return (
    <div style={{ position: "relative" }}>
      {/* Ícono campana */}
      <button
        onClick={() => setVisible((v) => !v)}
        style={{
          background: "transparent",
          border: "none",
          cursor: "pointer",
          fontSize: "20px",
        }}
      >
        🔔 {alertas.length}
      </button>

      {/* Popup */}
      {visible && ultima && (
        <div
          style={{
            position: "absolute",
            top: 30,
            right: 0,
            background: "#fff",
            padding: 12,
            borderRadius: 8,
            minWidth: 220,
            boxShadow: "0 2px 10px rgba(0,0,0,0.25)",
            zIndex: 100,
          }}
        >
          <div style={{ fontWeight: "bold", marginBottom: 6 }}>
            {ultima.titulo || "Nueva alerta"}
          </div>
          <div>{ultima.mensaje || "Se ha detectado una nueva alerta."}</div>
        </div>
      )}
    </div>
  );
}
