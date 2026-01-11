package ro.unitbv.smarthome.automation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "automation_rules")
public class AutomationRule {

  @Id
  private String id;

  private UUID userId;
  private UUID homeId;

  private String name;
  private boolean enabled = true;

  private Trigger trigger;
  private Action action;

  private Instant lastTriggeredAt;

  public record Trigger(UUID sensorId, String operator, double threshold) {}
  public record Action(UUID deviceId, boolean setOn) {}

  public String getId() { return id; }
  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }
  public UUID getHomeId() { return homeId; }
  public void setHomeId(UUID homeId) { this.homeId = homeId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }
  public Trigger getTrigger() { return trigger; }
  public void setTrigger(Trigger trigger) { this.trigger = trigger; }
  public Action getAction() { return action; }
  public void setAction(Action action) { this.action = action; }
  public Instant getLastTriggeredAt() { return lastTriggeredAt; }
  public void setLastTriggeredAt(Instant lastTriggeredAt) { this.lastTriggeredAt = lastTriggeredAt; }
}
