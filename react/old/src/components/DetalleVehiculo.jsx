import React, { useEffect, useState } from "react";
import { apiFetch } from "../auth/api";

export default function DetalleVehiculo({ vehiculo, onBack }) {
  const [detalle, setDetalle] = useState(null);

  useEffect(() => {
    async function cargarDetalle() {
      if (!vehiculo) return;

      const data = await apiFetch(
        `https://apisigefve.xipatlani.tk/vehiculos/${vehiculo.id}`
      );

      if (data) setDetalle(data);
    }

    cargarDetalle();
  }, [vehiculo]);

  if (!vehiculo) return null;

  return (
    <div className="p-4 bg-white rounded shadow">
      <button
        className="mb-3 bg-blue-500 text-white px-4 py-2 rounded"
        onClick={onBack}
      >
        ← Regresar
      </button>

      <h2 className="text-xl font-bold">Detalle del Vehículo</h2>

      {!detalle && <p>Cargando detalles...</p>}

      {detalle && (
        <div className="mt-3">
          <p><strong>ID:</strong> {detalle.id}</p>
          <p><strong>Marca:</strong> {detalle.marca}</p>
          <p><strong>Modelo:</strong> {detalle.modelo}</p>
          <p><strong>Estado:</strong> {detalle.estado}</p>
        </div>
      )}
    </div>
  );
}
