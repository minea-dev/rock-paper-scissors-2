package com.mlicer.rockpaperscissors.infrastructure.adapter.in.web;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UserContextResolver {

    public String resolvePlayerId(OAuth2User principal, String guestId) {
        if (principal != null) {
            String email = principal.getAttribute("email");
            if (email != null && !email.isBlank()) {
                return email;
            }

            String login = principal.getAttribute("login");
            if (login != null && !login.isBlank()) {
                return login;
            }

            return principal.getName();
        }

        if (guestId != null && !guestId.isBlank()) {
            return guestId.trim();
        }

        throw new IllegalArgumentException("User identity could not be resolved. Missing X-Guest-Id header for anonymous session.");
    }
}