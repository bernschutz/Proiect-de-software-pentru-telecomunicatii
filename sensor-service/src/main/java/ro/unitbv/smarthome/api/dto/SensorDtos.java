package ro.unitbv.smarthome.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public class SensorDtos {

  public record CreateSensorRequest(@NotBlank String name, @NotBlank String type, @NotBlank String unit) {}
  public record SensorResponse(UUID id, UUID homeId, String name, String type, String unit) {}

  public record AddReadingRequest(double value) {}
  public record ReadingResponse(Instant time, double value) {}
  public record LatestReadingResponse(Instant time, Double value) {}
}
