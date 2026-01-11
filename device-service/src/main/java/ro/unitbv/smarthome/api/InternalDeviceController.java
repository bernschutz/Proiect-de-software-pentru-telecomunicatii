package ro.unitbv.smarthome.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unitbv.smarthome.device.DeviceService;

import java.util.UUID;

@RestController
@RequestMapping("/internal/devices")
public class InternalDeviceController {

  private final DeviceService deviceService;
  private final String internalToken;

  public InternalDeviceController(DeviceService deviceService, @Value("${security.internal-token}") String internalToken) {
    this.deviceService = deviceService;
    this.internalToken = internalToken;
  }

  @PostMapping("/{deviceId}/set")
  public ResponseEntity<?> set(@PathVariable UUID deviceId, @RequestParam boolean on, @RequestHeader(name = "X-Internal-Token", required = false) String token) {
    if (token == null || !token.equals(internalToken)) {
      return ResponseEntity.status(403).body("Forbidden");
    }
    deviceService.setInternal(deviceId, on);
    return ResponseEntity.noContent().build();
  }
}
