package ro.unitbv.smarthome.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unitbv.smarthome.api.dto.HomeDtos;
import ro.unitbv.smarthome.home.HomeService;
import ro.unitbv.smarthome.security.AuthUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/homes")
public class HomeController {

  private final HomeService homeService;

  public HomeController(HomeService homeService) {
    this.homeService = homeService;
  }

  @GetMapping
  public List<HomeDtos.HomeResponse> list() {
    UUID userId = AuthUtil.currentUserId();
    return homeService.list(userId).stream()
        .map(h -> new HomeDtos.HomeResponse(h.getId(), h.getName()))
        .toList();
  }

  @PostMapping
  public HomeDtos.HomeResponse create(@Valid @RequestBody HomeDtos.CreateHomeRequest req) {
    UUID userId = AuthUtil.currentUserId();
    var h = homeService.create(userId, req.name());
    return new HomeDtos.HomeResponse(h.getId(), h.getName());
  }

  @DeleteMapping("/{homeId}")
  public ResponseEntity<?> delete(@PathVariable UUID homeId) {
    UUID userId = AuthUtil.currentUserId();
    boolean ok = homeService.delete(userId, homeId);
    return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
