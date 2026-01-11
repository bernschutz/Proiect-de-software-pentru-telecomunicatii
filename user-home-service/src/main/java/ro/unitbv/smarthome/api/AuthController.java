package ro.unitbv.smarthome.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unitbv.smarthome.api.dto.AuthDtos;
import ro.unitbv.smarthome.security.JwtService;
import ro.unitbv.smarthome.user.AppUser;
import ro.unitbv.smarthome.user.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final JwtService jwtService;

  public AuthController(AuthService authService, JwtService jwtService) {
    this.authService = authService;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
    AppUser u = authService.register(req.email(), req.password(), req.fullName());
    String token = jwtService.generateToken(u.getId(), u.getEmail());
    return ResponseEntity.ok(new AuthDtos.AuthResponse(token));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
    AppUser u = authService.login(req.email(), req.password());
    String token = jwtService.generateToken(u.getId(), u.getEmail());
    return ResponseEntity.ok(new AuthDtos.AuthResponse(token));
  }
}
