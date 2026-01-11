package ro.unitbv.smarthome.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unitbv.smarthome.api.dto.SensorDtos;
import ro.unitbv.smarthome.influx.InfluxService;
import ro.unitbv.smarthome.sensor.SensorService;

import java.util.UUID;

@RestController
@RequestMapping("/internal/sensors")
public class InternalSensorController {

  private final SensorService sensorService;
  private final InfluxService influxService;
  private final String internalToken;

  public InternalSensorController(SensorService sensorService, InfluxService influxService, @Value("${security.internal-token}") String internalToken) {
    this.sensorService = sensorService;
    this.influxService = influxService;
    this.internalToken = internalToken;
  }

  @GetMapping("/{sensorId}/latest")
  public ResponseEntity<?> latest(@PathVariable UUID sensorId, @RequestHeader(name = "X-Internal-Token", required = false) String token) {
    if (token == null || !token.equals(internalToken)) {
      return ResponseEntity.status(403).body("Forbidden");
    }
    sensorService.getAny(sensorId);
    InfluxService.Reading r = influxService.latest(sensorId);
    if (r == null) {
      return ResponseEntity.ok(new SensorDtos.LatestReadingResponse(null, null));
    }
    return ResponseEntity.ok(new SensorDtos.LatestReadingResponse(r.time(), r.value()));
  }
}
