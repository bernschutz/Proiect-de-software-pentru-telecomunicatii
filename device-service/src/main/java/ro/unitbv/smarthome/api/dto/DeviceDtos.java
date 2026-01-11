package ro.unitbv.smarthome.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class DeviceDtos {
  public record CreateDeviceRequest(@NotBlank String name, @NotBlank String type) {}
  public record ToggleRequest(boolean on) {}
  public record DeviceResponse(UUID id, UUID homeId, String name, String type, boolean on) {}
}
