package uk.gov.moj.cp.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String root() {
        return """
                stg-track-my-case-service-mock (AMP stubs)

                GET /health — JSON { "status": "UP" }

                There is no API at / alone — use the paths your client calls, for example:

                  POST /{tenant}/oauth2/v2.0/token
                  GET  /courthouses/{courthouseId}
                  GET  /courthouses/{courthouseId}/courtrooms/{courtroomId}
                  GET  /pcd/cases/{caseUrn}
                  GET  /case/{caseUrn}/courtschedule
                """;
    }
}
