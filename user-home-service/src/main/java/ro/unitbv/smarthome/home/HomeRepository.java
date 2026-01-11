package ro.unitbv.smarthome.home;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HomeRepository extends JpaRepository<Home, UUID> {
  List<Home> findByUserIdOrderByCreatedAtDesc(UUID userId);
  long deleteByIdAndUserId(UUID id, UUID userId);
}
