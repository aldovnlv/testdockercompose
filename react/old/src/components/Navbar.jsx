import React from "react";
import { useAuth } from "../auth/AuthContext";
import Alertas from "./Alertas";

export default function NavBar() {
  const { logout } = useAuth();

  return (
    <div className="w-full bg-gray-800 flex justify-between items-center px-4 py-3 text-white shadow-md">

      {/* Título */}
      <h1 className="text-xl font-bold tracking-wide">
        Fleet Dashboard
      </h1>

      {/* Alertas */}
      <div className="flex items-center gap-4">
        <Alertas />

        {/* Botón Salir */}
        <button
          onClick={logout}
          className="bg-red-500 px-4 py-2 rounded hover:bg-red-600 transition"
        >
          Salir
        </button>
      </div>

    </div>
  );
}
