// Lista de vehículos con estado, batería y última ubicación

import React, { useEffect, useState } from "react";
import { apiFetch } from "../auth/api";

export default function ListaVehiculos({ onSelect }) {
  const [vehiculos, setVehiculos] = useState([]);

  // Cargar lista de vehículos desde API
  useEffect(() => {
    async function cargarVehiculos() {
      const data = await apiFetch("https://apisigefve.xipatlani.tk/vehiculos");

      if (data) {
        setVehiculos(data);
      }
    }

    cargarVehiculos();
  }, []);

  return (
    <div className="bg-white p-3 rounded shadow">
      <h3 className="text-lg font-bold mb-3">Vehículos</h3>

      {vehiculos.length === 0 && <p>Cargando vehículos...</p>}

      {vehiculos.map((v, index) => (
        <div
          key={index}
          className="border p-3 mb-2 rounded cursor-pointer hover:bg-gray-100"
          onClick={() => onSelect(v)}
        >
          <p className="font-bold">{v.nombre || `Vehículo ${v.id}`}</p>
          <p>Estado: {v.estado}</p>
          <p>Batería: {v.bateria}%</p>
          <p>Última ubicación: {v.ubicacion}</p>
        </div>
      ))}
    </div>
  );
}
