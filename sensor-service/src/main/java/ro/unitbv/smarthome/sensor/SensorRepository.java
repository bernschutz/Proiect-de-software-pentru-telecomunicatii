package ro.unitbv.smarthome.sensor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {
  List<Sensor> findByUserIdAndHomeIdOrderByCreatedAtDesc(UUID userId, UUID homeId);
  Optional<Sensor> findByIdAndUserId(UUID id, UUID userId);
}
