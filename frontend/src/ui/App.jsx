import React, { useEffect, useMemo, useState } from "react";
import { api, setToken, getToken, getApiBase } from "./api.js";

function SectionTitle({ title, right }) {
  return (
    <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 10 }}>
      <h2 style={{ margin: 0, fontSize: 16 }}>{title}</h2>
      {right}
    </div>
  );
}

function ErrorToast({ error }) {
  if (!error) return null;
  return <div className="toast">⚠️ {error}</div>;
}

function Auth({ onAuthed }) {
  const [mode, setMode] = useState("login");
  const [email, setEmail] = useState("");
  const [fullName, setFullName] = useState("");
  const [password, setPassword] = useState("");
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState("");

  async function submit(e) {
    e.preventDefault();
    setErr("");
    setBusy(true);
    try {
      const res =
        mode === "login"
          ? await api.login(email, password)
          : await api.register(email, password, fullName);
      setToken(res.token);
      onAuthed();
    } catch (e2) {
      setErr(e2.message || "Eroare");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="card">
      <SectionTitle
        title="Autentificare"
        right={
          <button className="btn ghost" onClick={() => setMode(mode === "login" ? "register" : "login")}>
            {mode === "login" ? "Register" : "Login"}
          </button>
        }
      />
      <p className="sub" style={{ marginTop: 6 }}>API: {getApiBase()}</p>
      <form onSubmit={submit}>
        {mode === "register" && (
          <>
            <label>Nume</label>
            <input value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Numele Utilizatorului" />
          </>
        )}
        <label>Email</label>
        <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="email@exemplu.ro" />
        <label>Parolă</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" />
        <div style={{ display: "flex", gap: 10, marginTop: 12 }}>
          <button className="btn" disabled={busy}>{busy ? "..." : (mode === "login" ? "Login" : "Create account")}</button>
        </div>
        <ErrorToast error={err} />
      </form>
    </div>
  );
}

function HomesPane({ homes, selectedHomeId, onSelect, onCreate, onDelete }) {
  const [name, setName] = useState("");

  return (
    <div className="card">
      <SectionTitle title="Homes" />
      <div style={{ display: "flex", gap: 10, marginTop: 10 }}>
        <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Nume locuință (ex: Apartament)" />
        <button className="btn" onClick={() => { if (!name.trim()) return; onCreate(name.trim()); setName(""); }}>Create</button>
      </div>
      <hr />
      <div className="list">
        {homes.map((h) => (
          <div key={h.id} className="item" style={{ outline: h.id === selectedHomeId ? "2px solid rgba(59,130,246,.45)" : "none" }}>
            <div onClick={() => onSelect(h.id)} style={{ cursor: "pointer" }}>
              <strong>{h.name}</strong>
              <small>{h.id}</small>
            </div>
            <button className="btn danger" onClick={() => onDelete(h.id)}>Delete</button>
          </div>
        ))}
        {homes.length === 0 && <div className="sub">Nu ai încă o locuință. Creează una.</div>}
      </div>
    </div>
  );
}

function DevicesPane({ homeId }) {
  const [items, setItems] = useState([]);
  const [err, setErr] = useState("");
  const [name, setName] = useState("");
  const [type, setType] = useState("light");

  async function refresh() {
    setErr("");
    try { setItems(await api.listDevices(homeId)); } catch (e) { setErr(e.message); }
  }
  useEffect(() => { refresh(); }, [homeId]);

  async function add() {
    setErr("");
    try {
      await api.addDevice(homeId, name, type);
      setName("");
      await refresh();
    } catch (e) { setErr(e.message); }
  }

  async function toggle(d) {
    setErr("");
    try { await api.toggleDevice(d.id, !d.on); await refresh(); } catch (e) { setErr(e.message); }
  }

  async function del(id) {
    setErr("");
    try { await api.deleteDevice(id); await refresh(); } catch (e) { setErr(e.message); }
  }

  return (
    <div className="card">
      <SectionTitle title="Devices (Add/Delete/Status/ON-OFF)" right={<button className="btn ghost" onClick={refresh}>Refresh</button>} />
      <div className="row" style={{ marginTop: 10 }}>
        <div className="col">
          <label>Nume</label>
          <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Ex: Living room light" />
        </div>
        <div className="col">
          <label>Tip</label>
          <select value={type} onChange={(e) => setType(e.target.value)}>
            <option value="light">light</option>
            <option value="plug">plug</option>
            <option value="heater">heater</option>
            <option value="lock">lock</option>
          </select>
        </div>
        <div style={{ display: "flex", alignItems: "flex-end" }}>
          <button className="btn" onClick={add} disabled={!name.trim()}>Add</button>
        </div>
      </div>

      <hr />
      <div className="list">
        {items.map((d) => (
          <div key={d.id} className="item">
            <div>
              <strong>{d.name}</strong>
              <small>{d.type} • {d.on ? "ON" : "OFF"}</small>
            </div>
            <div style={{ display: "flex", gap: 8 }}>
              <button className={"btn " + (d.on ? "ghost" : "ok")} onClick={() => toggle(d)}>{d.on ? "Turn OFF" : "Turn ON"}</button>
              <button className="btn danger" onClick={() => del(d.id)}>Delete</button>
            </div>
          </div>
        ))}
        {items.length === 0 && <div className="sub">Nu ai device-uri.</div>}
      </div>
      <ErrorToast error={err} />
    </div>
  );
}

function SensorsPane({ homeId }) {
  const [items, setItems] = useState([]);
  const [err, setErr] = useState("");
  const [name, setName] = useState("");
  const [type, setType] = useState("temperature");
  const [unit, setUnit] = useState("°C");
  const [selected, setSelected] = useState(null);
  const [range, setRange] = useState("-24h");
  const [readings, setReadings] = useState([]);

  async function refresh() {
    setErr("");
    try { 
      const s = await api.listSensors(homeId);
      setItems(s);
      if (selected && !s.find(x => x.id === selected)) { setSelected(null); setReadings([]); }
    } catch (e) { setErr(e.message); }
  }
  useEffect(() => { refresh(); }, [homeId]);

  async function add() {
    setErr("");
    try {
      await api.addSensor(homeId, name, type, unit);
      setName("");
      await refresh();
    } catch (e) { setErr(e.message); }
  }

  async function del(id) {
    setErr("");
    try { await api.deleteSensor(id); if (selected === id) { setSelected(null); setReadings([]); } await refresh(); } catch (e) { setErr(e.message); }
  }

  async function loadReadings(sensorId) {
    setErr("");
    try {
      const rs = await api.readings(sensorId, range, 50);
      setReadings(rs);
    } catch (e) { setErr(e.message); }
  }

  async function addRandomReading(sensorId) {
    setErr("");
    try {
      const v = (Math.random() * 10 + 20).toFixed(2);
      await api.addReading(sensorId, v);
      await loadReadings(sensorId);
    } catch (e) { setErr(e.message); }
  }

  return (
    <div className="card">
      <SectionTitle title="Senzori (Add/Delete/Vizualizare)" right={<button className="btn ghost" onClick={refresh}>Refresh</button>} />
      <div className="row" style={{ marginTop: 10 }}>
        <div className="col">
          <label>Nume</label>
          <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Ex: Temp living" />
        </div>
        <div className="col">
          <label>Tip</label>
          <select value={type} onChange={(e) => setType(e.target.value)}>
            <option value="temperature">temperature</option>
            <option value="humidity">humidity</option>
            <option value="motion">motion</option>
            <option value="co2">co2</option>
          </select>
        </div>
        <div className="col">
          <label>Unitate</label>
          <input value={unit} onChange={(e) => setUnit(e.target.value)} placeholder="°C / % / ppm" />
        </div>
        <div style={{ display: "flex", alignItems: "flex-end" }}>
          <button className="btn" onClick={add} disabled={!name.trim()}>Add</button>
        </div>
      </div>

      <hr />
      <div className="row">
        <div className="col">
          <div className="list">
            {items.map((s) => (
              <div key={s.id} className="item" style={{ outline: s.id === selected ? "2px solid rgba(34,197,94,.35)" : "none" }}>
                <div onClick={() => { setSelected(s.id); loadReadings(s.id); }} style={{ cursor: "pointer" }}>
                  <strong>{s.name}</strong>
                  <small>{s.type} • {s.unit}</small>
                </div>
                <div style={{ display: "flex", gap: 8 }}>
                  <button className="btn ghost" onClick={() => addRandomReading(s.id)}>+ reading</button>
                  <button className="btn danger" onClick={() => del(s.id)}>Delete</button>
                </div>
              </div>
            ))}
            {items.length === 0 && <div className="sub">Nu ai senzori.</div>}
          </div>
        </div>
        <div className="col">
          <SectionTitle
            title="Vizualizare senzor (citiri)"
            right={
              <div style={{ display: "flex", gap: 8 }}>
                <select value={range} onChange={(e) => setRange(e.target.value)} disabled={!selected}>
                  <option value="-1h">-1h</option>
                  <option value="-6h">-6h</option>
                  <option value="-24h">-24h</option>
                  <option value="-7d">-7d</option>
                </select>
                <button className="btn ghost" disabled={!selected} onClick={() => loadReadings(selected)}>Load</button>
              </div>
            }
          />
          <hr />
          {!selected && <div className="sub">Selectează un senzor pentru citiri.</div>}
          {selected && readings.length === 0 && <div className="sub">Nu există citiri încă. Apasă “+ reading”.</div>}
          {selected && readings.length > 0 && (
            <div className="list">
              {readings.map((r, idx) => (
                <div key={idx} className="item">
                  <div>
                    <strong>{r.value}</strong>
                    <small>{new Date(r.time).toLocaleString()}</small>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <ErrorToast error={err} />
    </div>
  );
}

function AutomationsPane({ homeId, devices, sensors }) {
  const [items, setItems] = useState([]);
  const [err, setErr] = useState("");

  const [name, setName] = useState("");
  const [sensorId, setSensorId] = useState("");
  const [op, setOp] = useState(">");
  const [threshold, setThreshold] = useState("25");
  const [deviceId, setDeviceId] = useState("");
  const [setOn, setSetOn] = useState(true);

  async function refresh() {
    setErr("");
    try { setItems(await api.listAutomations(homeId)); } catch (e) { setErr(e.message); }
  }
  useEffect(() => { refresh(); }, [homeId]);

  async function create() {
    setErr("");
    try {
      const payload = {
        name,
        trigger: { sensorId, operator: op, threshold: Number(threshold) },
        action: { deviceId, setOn: !!setOn },
      };
      await api.createAutomation(homeId, payload);
      setName("");
      await refresh();
    } catch (e) { setErr(e.message); }
  }

  async function del(id) {
    setErr("");
    try { await api.deleteAutomation(id); await refresh(); } catch (e) { setErr(e.message); }
  }

  async function toggleEnabled(a) {
    setErr("");
    try {
      const payload = {
        name: a.name,
        enabled: !a.enabled,
        trigger: a.trigger,
        action: a.action,
      };
      await api.updateAutomation(a.id, payload);
      await refresh();
    } catch (e) { setErr(e.message); }
  }

  return (
    <div className="card">
      <SectionTitle title="Automatizări (Creare/Editare/Ștergere)" right={<button className="btn ghost" onClick={refresh}>Refresh</button>} />
      <div className="row" style={{ marginTop: 10 }}>
        <div className="col">
          <label>Nume</label>
          <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Ex: Pornește încălzirea" />
        </div>
        <div className="col">
          <label>Trigger: Senzor</label>
          <select value={sensorId} onChange={(e) => setSensorId(e.target.value)}>
            <option value="">-- select --</option>
            {sensors.map(s => <option key={s.id} value={s.id}>{s.name} ({s.type})</option>)}
          </select>
        </div>
        <div className="col">
          <label>Operator</label>
          <select value={op} onChange={(e) => setOp(e.target.value)}>
            <option value=">">&gt;</option>
            <option value=">=">&gt;=</option>
            <option value="<">&lt;</option>
            <option value="<=">&lt;=</option>
            <option value="==">==</option>
            <option value="!=">!=</option>
          </select>
        </div>
        <div className="col">
          <label>Prag</label>
          <input value={threshold} onChange={(e) => setThreshold(e.target.value)} />
        </div>
        <div className="col">
          <label>Acțiune: Device</label>
          <select value={deviceId} onChange={(e) => setDeviceId(e.target.value)}>
            <option value="">-- select --</option>
            {devices.map(d => <option key={d.id} value={d.id}>{d.name} ({d.type})</option>)}
          </select>
        </div>
        <div className="col">
          <label>Set ON</label>
          <select value={setOn ? "true" : "false"} onChange={(e) => setSetOn(e.target.value === "true")}>
            <option value="true">true</option>
            <option value="false">false</option>
          </select>
        </div>
        <div style={{ display: "flex", alignItems: "flex-end" }}>
          <button className="btn" onClick={create} disabled={!name.trim() || !sensorId || !deviceId}>Create</button>
        </div>
      </div>

      <hr />
      <div className="list">
        {items.map((a) => (
          <div key={a.id} className="item">
            <div>
              <strong>{a.name}</strong>
              <small>
                {a.enabled ? "ENABLED" : "DISABLED"} • if sensor {a.trigger.sensorId} {a.trigger.operator} {a.trigger.threshold} ⇒ set device {a.action.deviceId} on={String(a.action.setOn)}
              </small>
            </div>
            <div style={{ display: "flex", gap: 8 }}>
              <button className="btn ghost" onClick={() => toggleEnabled(a)}>{a.enabled ? "Disable" : "Enable"}</button>
              <button className="btn danger" onClick={() => del(a.id)}>Delete</button>
            </div>
          </div>
        ))}
        {items.length === 0 && <div className="sub">Nu ai automatizări.</div>}
      </div>
      <ErrorToast error={err} />
    </div>
  );
}

export default function App() {
  const [authed, setAuthed] = useState(!!getToken());
  const [homes, setHomes] = useState([]);
  const [homeId, setHomeId] = useState(null);
  const [tab, setTab] = useState("devices");
  const [err, setErr] = useState("");

  const [devices, setDevices] = useState([]);
  const [sensors, setSensors] = useState([]);

  async function loadHomes() {
    setErr("");
    try {
      const hs = await api.listHomes();
      setHomes(hs);
      if (hs.length && !homeId) setHomeId(hs[0].id);
      if (hs.length === 0) setHomeId(null);
    } catch (e) {
      setErr(e.message);
    }
  }

  useEffect(() => {
    if (!authed) return;
    loadHomes();
  }, [authed]);

  useEffect(() => {
    async function loadContext() {
      if (!homeId) return;
      try {
        const [ds, ss] = await Promise.all([api.listDevices(homeId), api.listSensors(homeId)]);
        setDevices(ds); setSensors(ss);
      } catch {}
    }
    loadContext();
  }, [homeId, tab]);

  async function createHome(name) {
    setErr("");
    try {
      await api.createHome(name);
      await loadHomes();
    } catch (e) { setErr(e.message); }
  }

  async function deleteHome(id) {
    setErr("");
    try {
      await api.deleteHome(id);
      await loadHomes();
    } catch (e) { setErr(e.message); }
  }

  function logout() {
    setToken(null);
    setAuthed(false);
    setHomes([]);
    setHomeId(null);
    setErr("");
  }

  if (!authed) {
    return (
      <div className="container">
        <div className="header">
          <div className="brand">
            <div className="logo" />
            <div>
              <p className="h1">SmartHome</p>
              <p className="sub">UI Web • Spring Boot Microservices • Docker</p>
            </div>
          </div>
        </div>
        <Auth onAuthed={() => setAuthed(true)} />
      </div>
    );
  }

  return (
    <div className="container">
      <div className="header">
        <div className="brand">
          <div className="logo" />
          <div>
            <p className="h1">SmartHome</p>
            <p className="sub">Selectează un Home, apoi gestionează Devices/Senzori/Automatizări.</p>
          </div>
        </div>
        <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
          <button className="btn ghost" onClick={loadHomes}>Reload homes</button>
          <button className="btn danger" onClick={logout}>Logout</button>
        </div>
      </div>

      <div className="row">
        <div className="col">
          <HomesPane
            homes={homes}
            selectedHomeId={homeId}
            onSelect={(id) => setHomeId(id)}
            onCreate={createHome}
            onDelete={deleteHome}
          />
          <ErrorToast error={err} />
        </div>

        <div className="col" style={{ flex: 2 }}>
          {!homeId ? (
            <div className="card">
              <SectionTitle title="Alege un Home" />
              <p className="sub">Creează o locuință și selecteaz-o.</p>
            </div>
          ) : (
            <>
              <div className="tabs">
                <button className={"tab " + (tab === "devices" ? "active" : "")} onClick={() => setTab("devices")}>Devices</button>
                <button className={"tab " + (tab === "sensors" ? "active" : "")} onClick={() => setTab("sensors")}>Senzori</button>
                <button className={"tab " + (tab === "automations" ? "active" : "")} onClick={() => setTab("automations")}>Automatizări</button>
              </div>
              <div style={{ marginTop: 10 }}>
                {tab === "devices" && <DevicesPane homeId={homeId} />}
                {tab === "sensors" && <SensorsPane homeId={homeId} />}
                {tab === "automations" && <AutomationsPane homeId={homeId} devices={devices} sensors={sensors} />}
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
