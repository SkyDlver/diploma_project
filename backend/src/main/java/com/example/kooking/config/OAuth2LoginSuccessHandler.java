package com.example.kooking.config;

import com.example.kooking.model.User;
import com.example.kooking.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        logger.info("OAuth2 authentication success handler triggered");

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        // Log all attributes for debugging
        Map<String, Object> attributes = oAuth2User.getAttributes();
        logger.info("OAuth2 user attributes: {}", attributes);

        // Extract email from Google user info
        String email = oAuth2User.getAttribute("email");
        logger.info("Extracted email: {}", email);

        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            // User exists, generate JWT token
            String token = jwtService.generateToken(email);
            logger.info("User found, JWT token generated");

            // Redirect to frontend with token
            String redirectUrl = frontendBaseUrl + "/oauth2/callback?token=" + token;
            logger.info("Redirecting to: {}", redirectUrl);
            response.sendRedirect(redirectUrl);
        } else {
            // User doesn't exist - redirect to login page with error
            logger.warn("No user found with email: {}", email);

            response.sendRedirect(frontendBaseUrl + "/login?error=user_not_found");
        }
    }
}