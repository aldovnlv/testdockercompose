// Mapa simulado usando un contenedor con puntos representando vehículos

import React, { useEffect, useState } from "react";
import { apiFetch } from "../auth/api";

export default function SimpleMap() {
  const [vehiculos, setVehiculos] = useState([]);

  // Cargar vehículos desde API
  useEffect(() => {
    async function cargarMapa() {
      const data = await apiFetch("https://apisigefve.xipatlani.tk/vehiculos");

      if (data) {
        setVehiculos(data);
      }
    }

    cargarMapa();
  }, []);

  return (
    <div className="bg-blue-100 h-64 w-full rounded relative overflow-hidden border">
      <h3 className="absolute top-1 left-2 text-sm font-bold">
        Mapa de Vehículos (simulado)
      </h3>

      {/* Marcadores de vehículos */}
      {vehiculos.map((v, i) => (
        <div
          key={i}
          className="absolute bg-red-500 w-4 h-4 rounded-full"
          style={{
            top: v.y || 20 + i * 30, // Coordenadas simuladas
            left: v.x || 40 + i * 40,
          }}
          title={v.nombre || v.id}
        ></div>
      ))}
    </div>
  );
}
