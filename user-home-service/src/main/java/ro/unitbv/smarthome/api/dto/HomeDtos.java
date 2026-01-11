package ro.unitbv.smarthome.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class HomeDtos {
  public record CreateHomeRequest(@NotBlank String name) {}
  public record HomeResponse(UUID id, String name) {}
}
