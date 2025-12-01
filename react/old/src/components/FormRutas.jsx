import React, { useState } from "react";
import { apiFetch } from "../auth/api";

export default function FormRutas() {
  const [origen, setOrigen] = useState("");
  const [destino, setDestino] = useState("");
  const [fecha, setFecha] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validación básica antes de enviar
    if (!origen.trim() || !destino.trim() || !fecha) {
      alert("Todos los campos son obligatorios.");
      return;
    }

    await guardarRuta();
  };

  // 🔥 Guardar ruta en tu API real
  async function guardarRuta() {
    const data = await apiFetch("https://apisigefve.xipatlani.tk/rutas", {
      method: "POST",
      body: JSON.stringify({
        origen,
        destino,
        fecha
      })
    });

    if (data) {
      alert("Ruta guardada correctamente");

      // Limpiar formulario
      setOrigen("");
      setDestino("");
      setFecha("");
    }
  }

  return (
    <div className="bg-white p-4 rounded shadow">
      <h2 className="text-lg font-bold mb-3">Registrar Nueva Ruta</h2>

      <form onSubmit={handleSubmit}>

        {/* Origen */}
        <input
          type="text"
          className="w-full mb-3 border p-2 rounded"
          placeholder="Origen"
          value={origen}
          onChange={(e) => setOrigen(e.target.value)}
        />

        {/* Destino */}
        <input
          type="text"
          className="w-full mb-3 border p-2 rounded"
          placeholder="Destino"
          value={destino}
          onChange={(e) => setDestino(e.target.value)}
        />

        {/* Fecha */}
        <input
          type="date"
          className="w-full mb-3 border p-2 rounded"
          value={fecha}
          onChange={(e) => setFecha(e.target.value)}
        />

        <button
          type="submit"
          className="w-full bg-green-500 text-white py-2 rounded hover:bg-green-600 transition"
        >
          Guardar Ruta
        </button>

      </form>
    </div>
  );
}
