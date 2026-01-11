package ro.unitbv.smarthome.automation;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AutomationService {

  private final AutomationRepository repo;

  public AutomationService(AutomationRepository repo) {
    this.repo = repo;
  }

  public List<AutomationRule> list(UUID userId, UUID homeId) {
    return repo.findByUserIdAndHomeIdOrderByNameAsc(userId, homeId);
  }

  public AutomationRule create(UUID userId, UUID homeId, String name, AutomationRule.Trigger trigger, AutomationRule.Action action) {
    AutomationRule r = new AutomationRule();
    r.setUserId(userId);
    r.setHomeId(homeId);
    r.setName(name);
    r.setEnabled(true);
    r.setTrigger(trigger);
    r.setAction(action);
    return repo.save(r);
  }

  public AutomationRule update(UUID userId, String id, AutomationRule updated) {
    AutomationRule existing = repo.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new IllegalArgumentException("Automation not found"));
    existing.setName(updated.getName());
    existing.setEnabled(updated.isEnabled());
    existing.setTrigger(updated.getTrigger());
    existing.setAction(updated.getAction());
    return repo.save(existing);
  }

  public boolean delete(UUID userId, String id) {
    return repo.findByIdAndUserId(id, userId)
        .map(r -> { repo.delete(r); return true; })
        .orElse(false);
  }

  public List<AutomationRule> enabledRules() {
    return repo.findByEnabledTrue();
  }

  public AutomationRule save(AutomationRule r) {
    return repo.save(r);
  }
}
