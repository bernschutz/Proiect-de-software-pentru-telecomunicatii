package ro.unitbv.smarthome.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AppUserRepository repo;
  private final PasswordEncoder encoder;

  public AuthService(AppUserRepository repo, PasswordEncoder encoder) {
    this.repo = repo;
    this.encoder = encoder;
  }

  public AppUser register(String email, String password, String fullName) {
    repo.findByEmailIgnoreCase(email).ifPresent(u -> {
      throw new IllegalArgumentException("Email already in use");
    });

    AppUser u = new AppUser();
    u.setEmail(email.trim().toLowerCase());
    u.setPasswordHash(encoder.encode(password));
    u.setFullName(fullName);
    return repo.save(u);
  }

  public AppUser login(String email, String password) {
    AppUser u = repo.findByEmailIgnoreCase(email.trim().toLowerCase())
        .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

    if (!encoder.matches(password, u.getPasswordHash())) {
      throw new IllegalArgumentException("Invalid credentials");
    }
    return u;
  }
}
