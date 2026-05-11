package uk.gov.moj.cp.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest {

    @Test
    void getResponseLoadsClasspathMockJson() {
        ResponseEntity<String> response = Utils.getResponse("oauth-token.json");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertTrue(response.getBody().contains("wiremock-docker-mock-oauth-access-token"));
        assertTrue(response.getBody().contains("Bearer"));
    }

    @Test
    void getResponseThrowsWhenFileMissing() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Utils.getResponse("does-not-exist-xyz.json"));
        assertTrue(ex.getMessage().contains("Missing mock JSON"));
    }
}
