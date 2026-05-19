package uk.gov.moj.cp.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@UtilityClass
public class Utils {

    private static final String MOCK_RESPONSES_DIR = "responses/";

    /**
     * Simulates upstream latency for mock controllers. Value is in milliseconds (e.g. 10 = 10ms).
     */
    public static void applyControllerDelay(final int delayMillis) {
        if (delayMillis <= 0) {
            return;
        }
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted during mock response delay", e);
        }
    }

    /**
     * Sleeps only long enough so that {@code elapsedMillis + sleep} reaches {@code targetDelayMillis}.
     */
    public static void applyRemainingControllerDelay(final int targetDelayMillis, final long elapsedMillis) {
        if (targetDelayMillis <= 0) {
            return;
        }
        long remainingMillis = (long) targetDelayMillis - elapsedMillis;
        if (remainingMillis > 0) {
            applyControllerDelay(remainingMillis > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) remainingMillis);
        }
    }

    public static <T> T executeWithRemainingDelay(final int targetDelayMillis, final Supplier<T> serviceCall) {
        long startedAtNanos = System.nanoTime();
        T result = serviceCall.get();
        long elapsedMillis = (System.nanoTime() - startedAtNanos) / 1_000_000L;
        applyRemainingControllerDelay(targetDelayMillis, elapsedMillis);
        return result;
    }

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
