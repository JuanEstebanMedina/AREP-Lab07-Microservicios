package com.twitter.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Returns the authenticated principal details or a 401 payload when no session
     * exists.
     *
     * @param principal resolved OAuth2 user (null when unauthenticated)
     * @return JSON payload describing the session state
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("authenticated", false));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);

        String username = principal.getAttribute("cognito:username");
        if (username == null) {
            username = principal.getAttribute("preferred_username");
        }
        if (username == null) {
            username = principal.getAttribute("email");
        }
        if (username == null) {
            username = principal.getName();
        }

        response.put("username", username);
        response.put("email", principal.getAttribute("email"));
        response.put("attributes", principal.getAttributes());

        return ResponseEntity.ok(response);
    }

    /**
     * Declarative endpoint used by the SPA to trigger Spring Security's OAuth2
     * login redirect.
     */
    @GetMapping("/login")
    public void login() {

    }

    /**
     * Convenience endpoint to provide a JSON body for logout requests.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
