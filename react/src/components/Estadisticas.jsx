import React, { useEffect, useState } from "react";
import { apiFetch } from "../auth/api";

export default function Estadisticas() {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    async function cargarStats() {
      const data = await apiFetch("https://apisigefve.xipatlani.tk/estadisticas");
      if (data) setStats(data);
    }

    cargarStats();
  }, []);

  return (
    <div className="bg-white p-4 rounded shadow">
      <h2 className="text-xl font-bold mb-2">Estadísticas</h2>

      {!stats && <p>Cargando estadísticas...</p>}

      {stats && (
        <div>
          <p><strong>Total vehículos:</strong> {stats.totalVehiculos}</p>
          <p><strong>Disponibles:</strong> {stats.disponibles}</p>
          <p><strong>Ocupados:</strong> {stats.ocupados}</p>
        </div>
      )}
    </div>
  );
}
