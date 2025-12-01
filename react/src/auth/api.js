export async function apiFetch(url, options = {}) {
  const token = localStorage.getItem("token");

  const finalOptions = {
    ...options,
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`,
      ...(options.headers || {})
    }
  };

  try {
    const response = await fetch(url, finalOptions);

    // Si el token expiró o es inválido → API manda 401
    if (response.status === 401 || response.status === 403) {
      alert("Tu sesión expiró. Inicia sesión nuevamente.");
      localStorage.removeItem("token");
      window.location.href = "/";
      return null;
    }

    const data = await response.json();
    return data;

  } catch (error) {
    console.error("Error en apiFetch:", error);
    alert("No se pudo conectar con el servidor.");
    return null;
  }
}
