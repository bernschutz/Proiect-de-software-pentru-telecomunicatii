const API_BASE = window.__API_BASE__ || import.meta.env.VITE_API_BASE || "http://localhost:8080";

export function getApiBase() {
  return API_BASE;
}

export function getToken() {
  return localStorage.getItem("token");
}

export function setToken(t) {
  if (t) localStorage.setItem("token", t);
  else localStorage.removeItem("token");
}

async function request(path, { method = "GET", body } = {}) {
  const headers = { "Content-Type": "application/json" };
  const token = getToken();
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const res = await fetch(API_BASE + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (res.status === 204) return null;

  const text = await res.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { data = text; }

  if (!res.ok) {
    const msg = (data && data.error) ? data.error : (typeof data === "string" ? data : "Request failed");
    throw new Error(msg);
  }
  return data;
}

export const api = {
  register: (email, password, fullName) => request("/api/auth/register", { method: "POST", body: { email, password, fullName } }),
  login: (email, password) => request("/api/auth/login", { method: "POST", body: { email, password } }),

  listHomes: () => request("/api/homes"),
  createHome: (name) => request("/api/homes", { method: "POST", body: { name } }),
  deleteHome: (id) => request(`/api/homes/${id}`, { method: "DELETE" }),

  listDevices: (homeId) => request(`/api/homes/${homeId}/devices`),
  addDevice: (homeId, name, type) => request(`/api/homes/${homeId}/devices`, { method: "POST", body: { name, type } }),
  deleteDevice: (id) => request(`/api/devices/${id}`, { method: "DELETE" }),
  toggleDevice: (id, on) => request(`/api/devices/${id}/toggle`, { method: "POST", body: { on } }),

  listSensors: (homeId) => request(`/api/homes/${homeId}/sensors`),
  addSensor: (homeId, name, type, unit) => request(`/api/homes/${homeId}/sensors`, { method: "POST", body: { name, type, unit } }),
  deleteSensor: (id) => request(`/api/sensors/${id}`, { method: "DELETE" }),
  addReading: (sensorId, value) => request(`/api/sensors/${sensorId}/readings`, { method: "POST", body: { value: Number(value) } }),
  readings: (sensorId, range="-24h", limit=50) => request(`/api/sensors/${sensorId}/readings?range=${encodeURIComponent(range)}&limit=${limit}`),

  listAutomations: (homeId) => request(`/api/homes/${homeId}/automations`),
  createAutomation: (homeId, payload) => request(`/api/homes/${homeId}/automations`, { method: "POST", body: payload }),
  updateAutomation: (id, payload) => request(`/api/automations/${id}`, { method: "PUT", body: payload }),
  deleteAutomation: (id) => request(`/api/automations/${id}`, { method: "DELETE" }),
};
