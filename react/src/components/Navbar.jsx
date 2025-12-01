import React from "react";
import { useAuth } from "../auth/AuthContext";

export default function NavBar() {
  const { logout } = useAuth();

  return (
    <div className="w-full bg-white shadow flex justify-between px-6 py-4 items-center">

      <h1 className="text-xl font-bold">SIGEFVE - Dashboard</h1>

      <div className="flex gap-5 items-center">

        <span className="font-semibold text-gray-700">
          {localStorage.getItem("username") || "usuario"}
        </span>

        <button
          onClick={logout}
          className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
        >
          Cerrar sesión
        </button>

      </div>
    </div>
  );
}
