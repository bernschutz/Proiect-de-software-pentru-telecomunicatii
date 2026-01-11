package ro.unitbv.smarthome.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unitbv.smarthome.api.dto.DeviceDtos;
import ro.unitbv.smarthome.device.DeviceService;
import ro.unitbv.smarthome.security.AuthUtil;

import java.util.List;
import java.util.UUID;

@RestController
public class DeviceController {

  private final DeviceService deviceService;

  public DeviceController(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  @GetMapping("/api/homes/{homeId}/devices")
  public List<DeviceDtos.DeviceResponse> list(@PathVariable UUID homeId) {
    UUID userId = AuthUtil.currentUserId();
    return deviceService.list(userId, homeId).stream()
        .map(d -> new DeviceDtos.DeviceResponse(d.getId(), d.getHomeId(), d.getName(), d.getType(), d.isOnState()))
        .toList();
  }

  @PostMapping("/api/homes/{homeId}/devices")
  public DeviceDtos.DeviceResponse add(@PathVariable UUID homeId, @Valid @RequestBody DeviceDtos.CreateDeviceRequest req) {
    UUID userId = AuthUtil.currentUserId();
    var d = deviceService.add(userId, homeId, req.name(), req.type());
    return new DeviceDtos.DeviceResponse(d.getId(), d.getHomeId(), d.getName(), d.getType(), d.isOnState());
  }

  @DeleteMapping("/api/devices/{deviceId}")
  public ResponseEntity<?> delete(@PathVariable UUID deviceId) {
    UUID userId = AuthUtil.currentUserId();
    boolean ok = deviceService.delete(userId, deviceId);
    return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }

  @PostMapping("/api/devices/{deviceId}/toggle")
  public DeviceDtos.DeviceResponse toggle(@PathVariable UUID deviceId, @Valid @RequestBody DeviceDtos.ToggleRequest req) {
    UUID userId = AuthUtil.currentUserId();
    var d = deviceService.toggle(userId, deviceId, req.on());
    return new DeviceDtos.DeviceResponse(d.getId(), d.getHomeId(), d.getName(), d.getType(), d.isOnState());
  }

  @GetMapping("/api/devices/{deviceId}/status")
  public DeviceDtos.DeviceResponse status(@PathVariable UUID deviceId) {
    UUID userId = AuthUtil.currentUserId();
    var d = deviceService.get(userId, deviceId);
    return new DeviceDtos.DeviceResponse(d.getId(), d.getHomeId(), d.getName(), d.getType(), d.isOnState());
  }
}
