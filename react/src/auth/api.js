// src/auth/api.js
export async function apiFetch(endpoint, options = {}) {
  const token = localStorage.getItem("token");

  const headers = {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  };

  try {
    const response = await fetch(endpoint, {
      ...options,
      headers
    });

    return await response.json();

  } catch (error) {
    console.error("Error en apiFetch:", error);
    return null;
  }
}
