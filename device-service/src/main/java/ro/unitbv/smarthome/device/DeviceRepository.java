package ro.unitbv.smarthome.device;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
  List<Device> findByUserIdAndHomeIdOrderByUpdatedAtDesc(UUID userId, UUID homeId);
  Optional<Device> findByIdAndUserId(UUID id, UUID userId);
}
