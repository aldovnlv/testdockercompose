import React, { useState } from "react";
import { apiFetch } from "../auth/api";

export default function Login() {
  const [usuario, setUsuario] = useState("");
  const [contrasena, setContrasena] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    const response = await apiFetch("/go/login", {
  method: "POST",
  body: JSON.stringify({ username, password })
});


    if (response?.token) {
      localStorage.setItem("token", response.token);
      window.location.href = "/dashboard";
    } else {
      alert("Credenciales incorrectas");
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">

      <div className="bg-white shadow-md p-10 rounded-lg w-96">

        <h2 className="text-2xl font-bold text-center mb-6">
          SIGEFVE — Iniciar sesión
        </h2>

        <form onSubmit={handleSubmit}>

          <label className="block mb-1 font-semibold">Usuario</label>
          <input
            className="w-full border px-3 py-2 rounded mb-4"
            placeholder="correo o usuario"
            value={usuario}
            onChange={(e) => setUsuario(e.target.value)}
          />

          <label className="block mb-1 font-semibold">Contraseña</label>
          <input
            type="password"
            className="w-full border px-3 py-2 rounded mb-6"
            placeholder="*******"
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
          />

          <button className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700">
            Iniciar sesión
          </button>

        </form>
      </div>
    </div>
  );
}
