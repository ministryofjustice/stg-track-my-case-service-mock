package uk.gov.moj.cp.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.moj.cp.util.Utils.objectMapper;

class CourtScheduleServiceTest {

    private final CourtScheduleService courtScheduleService = new CourtScheduleService();

    @Test
    void courtScheduleForCaseReturnsJsonWithHearingStructure() throws Exception {
        ResponseEntity<String> response = courtScheduleService.courtScheduleForCase("TMCTR0D");

        assertEquals(200, response.getStatusCode().value());
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode hearing = root.path("courtSchedule").get(0).path("hearings").get(0);
        assertEquals("TMCTR0D-hearing-id", hearing.path("hearingId").asText());
        assertEquals("Trial", hearing.path("hearingType").asText());
        assertTrue(hearing.path("courtSittings").isArray());
        assertEquals(1, hearing.path("courtSittings").size());
    }

    @Test
    void courtScheduleEmbedsCaseUrnInIds() throws Exception {
        ResponseEntity<String> response = courtScheduleService.courtScheduleForCase("TMCSEN1D2");

        JsonNode hearing = objectMapper.readTree(response.getBody())
                .path("courtSchedule").get(0).path("hearings").get(0);
        assertTrue(hearing.path("hearingId").asText().startsWith("TMCSEN1D2"));
        assertEquals("Sentence", hearing.path("hearingType").asText());
        assertEquals(2, hearing.path("courtSittings").size());
    }
}
