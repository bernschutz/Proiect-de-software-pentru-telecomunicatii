package ro.unitbv.smarthome.automation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

@Component
public class RuleEvaluator {

  private final AutomationService automationService;
  private final RestClient rest;
  private final String internalToken;
  private final String sensorInternalUrl;
  private final String deviceInternalUrl;

  public RuleEvaluator(
      AutomationService automationService,
      @Value("${security.internal-token}") String internalToken,
      @Value("${services.sensor-internal-url}") String sensorInternalUrl,
      @Value("${services.device-internal-url}") String deviceInternalUrl
  ) {
    this.automationService = automationService;
    this.internalToken = internalToken;
    this.sensorInternalUrl = sensorInternalUrl;
    this.deviceInternalUrl = deviceInternalUrl;
    this.rest = RestClient.builder()
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Scheduled(fixedDelayString = "${automation.evaluate-ms:10000}")
  public void tick() {
    for (AutomationRule r : automationService.enabledRules()) {
      try {
        Double value = readLatestSensorValue(r.getTrigger().sensorId().toString());
        if (value == null) continue;

        boolean condition = evaluate(value, r.getTrigger().operator(), r.getTrigger().threshold());
        if (!condition) continue;

        setDevice(r.getAction().deviceId().toString(), r.getAction().setOn());
        r.setLastTriggeredAt(Instant.now());
        automationService.save(r);
      } catch (Exception ignored) {
        // keep evaluator resilient
      }
    }
  }

  private Double readLatestSensorValue(String sensorId) {
    @SuppressWarnings("unchecked")
    Map<String, Object> resp = rest.get()
        .uri(sensorInternalUrl + "/internal/sensors/" + sensorId + "/latest")
        .header("X-Internal-Token", internalToken)
        .retrieve()
        .body(Map.class);

    if (resp == null) return null;
    Object v = resp.get("value");
    return v == null ? null : ((Number) v).doubleValue();
  }

  private void setDevice(String deviceId, boolean on) {
    rest.post()
        .uri(deviceInternalUrl + "/internal/devices/" + deviceId + "/set?on=" + on)
        .header("X-Internal-Token", internalToken)
        .retrieve()
        .toBodilessEntity();
  }

  private boolean evaluate(double value, String operator, double threshold) {
    return switch (operator.trim()) {
      case ">" -> value > threshold;
      case ">=" -> value >= threshold;
      case "<" -> value < threshold;
      case "<=" -> value <= threshold;
      case "==" -> value == threshold;
      case "!=" -> value != threshold;
      default -> false;
    };
  }
}
