package ro.unitbv.smarthome.device;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class Device {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private UUID homeId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private boolean onState;

  @Column(nullable = false)
  private Instant updatedAt = Instant.now();

  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }
  public UUID getHomeId() { return homeId; }
  public void setHomeId(UUID homeId) { this.homeId = homeId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public boolean isOnState() { return onState; }
  public void setOnState(boolean onState) { this.onState = onState; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void touch() { this.updatedAt = Instant.now(); }
}
