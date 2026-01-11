package ro.unitbv.smarthome.sensor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SensorService {

  private final SensorRepository repo;

  public SensorService(SensorRepository repo) {
    this.repo = repo;
  }

  public List<Sensor> list(UUID userId, UUID homeId) {
    return repo.findByUserIdAndHomeIdOrderByCreatedAtDesc(userId, homeId);
  }

  public Sensor add(UUID userId, UUID homeId, String name, String type, String unit) {
    Sensor s = new Sensor();
    s.setUserId(userId);
    s.setHomeId(homeId);
    s.setName(name);
    s.setType(type);
    s.setUnit(unit);
    return repo.save(s);
  }

  public boolean delete(UUID userId, UUID sensorId) {
    return repo.findByIdAndUserId(sensorId, userId)
        .map(s -> { repo.delete(s); return true; })
        .orElse(false);
  }

  public Sensor get(UUID userId, UUID sensorId) {
    return repo.findByIdAndUserId(sensorId, userId)
        .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));
  }

  public Sensor getAny(UUID sensorId) {
    return repo.findById(sensorId)
        .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));
  }
}
