package ro.unitbv.smarthome.security;

import java.util.UUID;

public record AuthUser(UUID id, String email) {}
