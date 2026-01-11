package ro.unitbv.smarthome.device;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeviceService {

  private final DeviceRepository repo;

  public DeviceService(DeviceRepository repo) {
    this.repo = repo;
  }

  public List<Device> list(UUID userId, UUID homeId) {
    return repo.findByUserIdAndHomeIdOrderByUpdatedAtDesc(userId, homeId);
  }

  public Device add(UUID userId, UUID homeId, String name, String type) {
    Device d = new Device();
    d.setUserId(userId);
    d.setHomeId(homeId);
    d.setName(name);
    d.setType(type);
    d.setOnState(false);
    return repo.save(d);
  }

  public boolean delete(UUID userId, UUID deviceId) {
    return repo.findByIdAndUserId(deviceId, userId)
        .map(d -> { repo.delete(d); return true; })
        .orElse(false);
  }

  public Device toggle(UUID userId, UUID deviceId, boolean on) {
    Device d = repo.findByIdAndUserId(deviceId, userId)
        .orElseThrow(() -> new IllegalArgumentException("Device not found"));
    d.setOnState(on);
    d.touch();
    return repo.save(d);
  }

  public Device setInternal(UUID deviceId, boolean on) {
    Device d = repo.findById(deviceId)
        .orElseThrow(() -> new IllegalArgumentException("Device not found"));
    d.setOnState(on);
    d.touch();
    return repo.save(d);
  }

  public Device get(UUID userId, UUID deviceId) {
    return repo.findByIdAndUserId(deviceId, userId)
        .orElseThrow(() -> new IllegalArgumentException("Device not found"));
  }
}
