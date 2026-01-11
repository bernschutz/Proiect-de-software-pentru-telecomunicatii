package ro.unitbv.smarthome.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unitbv.smarthome.api.dto.SensorDtos;
import ro.unitbv.smarthome.influx.InfluxService;
import ro.unitbv.smarthome.security.AuthUtil;
import ro.unitbv.smarthome.sensor.SensorService;

import java.util.List;
import java.util.UUID;

@RestController
public class SensorController {

  private final SensorService sensorService;
  private final InfluxService influxService;

  public SensorController(SensorService sensorService, InfluxService influxService) {
    this.sensorService = sensorService;
    this.influxService = influxService;
  }

  @GetMapping("/api/homes/{homeId}/sensors")
  public List<SensorDtos.SensorResponse> list(@PathVariable UUID homeId) {
    UUID userId = AuthUtil.currentUserId();
    return sensorService.list(userId, homeId).stream()
        .map(s -> new SensorDtos.SensorResponse(s.getId(), s.getHomeId(), s.getName(), s.getType(), s.getUnit()))
        .toList();
  }

  @PostMapping("/api/homes/{homeId}/sensors")
  public SensorDtos.SensorResponse add(@PathVariable UUID homeId, @Valid @RequestBody SensorDtos.CreateSensorRequest req) {
    UUID userId = AuthUtil.currentUserId();
    var s = sensorService.add(userId, homeId, req.name(), req.type(), req.unit());
    return new SensorDtos.SensorResponse(s.getId(), s.getHomeId(), s.getName(), s.getType(), s.getUnit());
  }

  @DeleteMapping("/api/sensors/{sensorId}")
  public ResponseEntity<?> delete(@PathVariable UUID sensorId) {
    UUID userId = AuthUtil.currentUserId();
    boolean ok = sensorService.delete(userId, sensorId);
    return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }

  @PostMapping("/api/sensors/{sensorId}/readings")
  public ResponseEntity<?> addReading(@PathVariable UUID sensorId, @Valid @RequestBody SensorDtos.AddReadingRequest req) {
    UUID userId = AuthUtil.currentUserId();
    sensorService.get(userId, sensorId);
    influxService.writeReading(sensorId, req.value());
    return ResponseEntity.accepted().build();
  }

  @GetMapping("/api/sensors/{sensorId}/readings")
  public List<SensorDtos.ReadingResponse> readings(
      @PathVariable UUID sensorId,
      @RequestParam(defaultValue = "-24h") String range,
      @RequestParam(defaultValue = "50") int limit
  ) {
    UUID userId = AuthUtil.currentUserId();
    sensorService.get(userId, sensorId);
    return influxService.getReadings(sensorId, range, Math.min(limit, 500)).stream()
        .map(r -> new SensorDtos.ReadingResponse(r.time(), r.value()))
        .toList();
  }
}
