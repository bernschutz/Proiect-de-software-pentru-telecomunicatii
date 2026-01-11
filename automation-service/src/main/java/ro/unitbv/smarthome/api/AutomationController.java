package ro.unitbv.smarthome.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unitbv.smarthome.api.dto.AutomationDtos;
import ro.unitbv.smarthome.automation.AutomationRule;
import ro.unitbv.smarthome.automation.AutomationService;
import ro.unitbv.smarthome.security.AuthUtil;

import java.util.List;
import java.util.UUID;

@RestController
public class AutomationController {

  private final AutomationService automationService;

  public AutomationController(AutomationService automationService) {
    this.automationService = automationService;
  }

  @GetMapping("/api/homes/{homeId}/automations")
  public List<AutomationDtos.AutomationResponse> list(@PathVariable UUID homeId) {
    UUID userId = AuthUtil.currentUserId();
    return automationService.list(userId, homeId).stream().map(this::toResp).toList();
  }

  @PostMapping("/api/homes/{homeId}/automations")
  public AutomationDtos.AutomationResponse create(@PathVariable UUID homeId, @Valid @RequestBody AutomationDtos.CreateAutomationRequest req) {
    UUID userId = AuthUtil.currentUserId();
    AutomationRule.Trigger trig = new AutomationRule.Trigger(req.trigger().sensorId(), req.trigger().operator(), req.trigger().threshold());
    AutomationRule.Action act = new AutomationRule.Action(req.action().deviceId(), req.action().setOn());
    AutomationRule r = automationService.create(userId, homeId, req.name(), trig, act);
    return toResp(r);
  }

  @PutMapping("/api/automations/{id}")
  public AutomationDtos.AutomationResponse update(@PathVariable String id, @Valid @RequestBody AutomationDtos.UpdateAutomationRequest req) {
    UUID userId = AuthUtil.currentUserId();

    AutomationRule updated = new AutomationRule();
    updated.setName(req.name());
    updated.setEnabled(req.enabled());
    updated.setTrigger(new AutomationRule.Trigger(req.trigger().sensorId(), req.trigger().operator(), req.trigger().threshold()));
    updated.setAction(new AutomationRule.Action(req.action().deviceId(), req.action().setOn()));

    AutomationRule r = automationService.update(userId, id, updated);
    return toResp(r);
  }

  @DeleteMapping("/api/automations/{id}")
  public ResponseEntity<?> delete(@PathVariable String id) {
    UUID userId = AuthUtil.currentUserId();
    boolean ok = automationService.delete(userId, id);
    return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }

  private AutomationDtos.AutomationResponse toResp(AutomationRule r) {
    var trig = new AutomationDtos.TriggerDto(r.getTrigger().sensorId(), r.getTrigger().operator(), r.getTrigger().threshold());
    var act = new AutomationDtos.ActionDto(r.getAction().deviceId(), r.getAction().setOn());
    return new AutomationDtos.AutomationResponse(r.getId(), r.getHomeId(), r.getName(), r.isEnabled(), trig, act, r.getLastTriggeredAt());
  }
}
