package ro.unitbv.smarthome.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class AuthUtil {
  private AuthUtil() {}

  public static AuthUser currentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof AuthUser u)) {
      return null;
    }
    return u;
  }

  public static UUID currentUserId() {
    AuthUser u = currentUser();
    return u == null ? null : u.id();
  }
}
