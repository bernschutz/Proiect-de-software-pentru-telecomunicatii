# SmartHome – Platformă de management inteligent al locuinței (microservicii)

Implementare **end‑to‑end**: UI Web + API Gateway + 4 microservicii Spring Boot + DB-uri în Docker.

## Funcționalități (conform diagramei corecte)
- **Home**: Create Home, Delete Home
- **Devices**: Add, Delete, Status, ON/OFF
- **Senzori**: Add, Delete, Vizualizare senzor (citiri)
- **Automatizări**: Creare proces, Editare proces, Ștergere (+ evaluare periodică)

## Stack
- Spring Boot 3.x (arhitectură stratificată: controller → service → repository)
- Spring Security (JWT HMAC)
- Spring Cloud Gateway (rutare)
- Postgres (User/Home, Devices, Sensors metadata)
- InfluxDB (citiri senzori – time-series)
- MongoDB (reguli automatizări – NoSQL)
- React (UI) + Nginx (servire)

## Pornire rapidă (Docker Desktop)
1. Instalează Docker Desktop + Docker Compose.
2. Din rădăcina proiectului:
   ```bash
   docker compose up --build
   ```
3. UI: http://localhost:3000  
   API Gateway: http://localhost:8080

### Cont de test
- Îți faci cont din UI (Register), apoi Login.

## Note importante
- JWT secret și INTERNAL_TOKEN pot fi setate în `.env` (vezi `.env.example`).
- Automatizările sunt evaluate la fiecare ~10 secunde și pot comuta device‑uri în funcție de ultima citire a unui senzor.

## Structură repo
- `gateway/` – API Gateway
- `user-home-service/` – autentificare + gestionare Home
- `device-service/` – gestionare Devices
- `sensor-service/` – gestionare Senzori + citiri în InfluxDB
- `automation-service/` – reguli în MongoDB + evaluator
- `frontend/` – UI Web
- `k8s/` – manifest-uri Kubernetes (exemplu deploy în cloud)
