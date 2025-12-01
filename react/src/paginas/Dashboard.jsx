import React, { useEffect, useState } from "react";
import NavBar from "../components/Navbar";
import SimpleMap from "../components/SimpleMap";
import Estadisticas from "../components/Estadisticas";
import ListaVehiculos from "../components/ListaVehiculos";
import Alertas from "../components/Alertas";
import FormRutas from "../components/FormRutas";
import DetalleVehiculo from "../components/DetalleVehiculo";
import { validateToken } from "../auth/validateToken";

export default function Dashboard() {
  const [vehiculoSeleccionado, setVehiculoSeleccionado] = useState(null);

  useEffect(() => {
    async function checkToken() {
      const valido = await validateToken();

      if (!valido) {
        alert("Tu sesión ha expirado");
        localStorage.removeItem("token");
        window.location.href = "/";
      }
    }

    checkToken();
  }, []);

  return (
    <div>

      <NavBar />

      <div className="p-6 grid grid-cols-1 md:grid-cols-3 gap-6">

        {/* ------- Tarjetas estadísticas ------- */}
        <div className="md:col-span-3 grid grid-cols-3 gap-4">
          <Estadisticas />
        </div>

        {/* ------- Mapa ------- */}
        <div className="col-span-2 card">
          <SimpleMap />
        </div>

        {/* ------- Alertas ------- */}
        <div className="col-span-1">
          <Alertas />
        </div>

        {/* ------- Lista de vehículos ------- */}
        <div className="col-span-2 card">
          <ListaVehiculos onSelect={setVehiculoSeleccionado} />
        </div>

        {/* ------- Crear rutas ------- */}
        <div className="col-span-1 card">
          <FormRutas />
        </div>

      </div>

      {vehiculoSeleccionado && (
        <DetalleVehiculo
          vehiculo={vehiculoSeleccionado}
          onBack={() => setVehiculoSeleccionado(null)}
        />
      )}

    </div>
  );
}
