package ro.unitbv.smarthome.home;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HomeService {

  private final HomeRepository repo;

  public HomeService(HomeRepository repo) {
    this.repo = repo;
  }

  public Home create(UUID userId, String name) {
    Home h = new Home();
    h.setUserId(userId);
    h.setName(name);
    return repo.save(h);
  }

  public List<Home> list(UUID userId) {
    return repo.findByUserIdOrderByCreatedAtDesc(userId);
  }

  public boolean delete(UUID userId, UUID homeId) {
    return repo.deleteByIdAndUserId(homeId, userId) > 0;
  }
}
