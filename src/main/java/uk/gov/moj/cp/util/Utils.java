package uk.gov.moj.cp.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class Utils {

    private static final String MOCK_RESPONSES_DIR = "responses/";

    public static ResponseEntity<String> getResponse(final String fileName) {
        ClassPathResource resource = new ClassPathResource(MOCK_RESPONSES_DIR + fileName);
        if (!resource.exists()) {
            throw new IllegalArgumentException("Missing mock JSON: " + fileName);
        }
        try {
            String body = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read mock JSON: " + fileName, e);
        }
    }
}
