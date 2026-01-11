package ro.unitbv.smarthome.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class AutomationDtos {

  public record TriggerDto(@NotNull UUID sensorId, @NotBlank String operator, double threshold) {}
  public record ActionDto(@NotNull UUID deviceId, boolean setOn) {}

  public record CreateAutomationRequest(
      @NotBlank String name,
      @NotNull TriggerDto trigger,
      @NotNull ActionDto action
  ) {}

  public record UpdateAutomationRequest(
      @NotBlank String name,
      boolean enabled,
      @NotNull TriggerDto trigger,
      @NotNull ActionDto action
  ) {}

  public record AutomationResponse(
      String id,
      UUID homeId,
      String name,
      boolean enabled,
      TriggerDto trigger,
      ActionDto action,
      Instant lastTriggeredAt
  ) {}
}
