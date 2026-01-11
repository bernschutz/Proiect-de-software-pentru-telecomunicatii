package ro.unitbv.smarthome.automation;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AutomationRepository extends MongoRepository<AutomationRule, String> {
  List<AutomationRule> findByUserIdAndHomeIdOrderByNameAsc(UUID userId, UUID homeId);
  Optional<AutomationRule> findByIdAndUserId(String id, UUID userId);
  List<AutomationRule> findByEnabledTrue();
}
