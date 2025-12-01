import React, { useState } from "react";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  // TU API REAL EN INTERNET
  const API_URL = "https://apisigefve.xipatlani.tk/login";

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(API_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: username,
          password: password,
        }),
      });

      const data = await response.json();

      if (!response.ok || !data.token) {
        alert("Usuario o contraseña incorrectos");
        return;
      }

      // GUARDAR TOKEN EN LOCALSTORAGE
      localStorage.setItem("token", data.token);

      // Redirigir al dashboard
      window.location.href = "/dashboard";

    } catch (error) {
      console.error("Error login:", error);
      alert("Error conectando con el servidor. Intenta nuevamente.");
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-200">
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow-md w-80">
        <h2 className="text-xl font-bold mb-4 text-center">Login</h2>

        <input
          className="w-full mb-3 p-2 border rounded"
          placeholder="Usuario"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <input
          type="password"
          className="w-full mb-3 p-2 border rounded"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600">
          Entrar
        </button>
      </form>
    </div>
  );
}
