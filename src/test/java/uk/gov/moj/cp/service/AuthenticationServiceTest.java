package uk.gov.moj.cp.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationServiceTest {

    private final AuthenticationService authenticationService = new AuthenticationService();

    @Test
    void oauthTokenReturnsMockOAuthPayload() {
        ResponseEntity<String> response = authenticationService.oauthToken();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertTrue(response.getBody().contains("access_token"));
    }
}
