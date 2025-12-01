import React, { useState, useEffect } from "react";
import NavBar from "../components/Navbar";
import SimpleMap from "../components/SimpleMap";
import Estadisticas from "../components/Estadisticas";
import ListaVehiculos from "../components/ListaVehiculos";
import FormRutas from "../components/FormRutas";
import DetalleVehiculo from "../components/DetalleVehiculo";
import { validateToken } from "../auth/validateToken";
import { useAuth } from "../auth/AuthContext";

export default function Dashboard() {
  const { logout } = useAuth();
  const [vehiculoSeleccionado, setVehiculoSeleccionado] = useState(null);

  // Validación de token al cargar
  useEffect(() => {
    async function checkToken() {
      const valido = await validateToken();

      if (!valido) {
        alert("Tu sesión ha expirado. Inicia sesión nuevamente.");
        logout(); // Manejo correcto con AuthContext
      }
    }

    checkToken();
  }, [logout]);

  // Si un vehículo fue seleccionado → ver detalles
  if (vehiculoSeleccionado) {
    return (
      <>
        <NavBar />
        <DetalleVehiculo
          vehiculo={vehiculoSeleccionado}
          onBack={() => setVehiculoSeleccionado(null)}
        />
      </>
    );
  }

  // Vista normal del dashboard
  return (
    <div>
      <NavBar />

      <div className="p-4 grid grid-cols-1 md:grid-cols-2 gap-4">
        <SimpleMap />
        <Estadisticas />
        <ListaVehiculos onSelect={setVehiculoSeleccionado} />
        <FormRutas />
      </div>
    </div>
  );
}
