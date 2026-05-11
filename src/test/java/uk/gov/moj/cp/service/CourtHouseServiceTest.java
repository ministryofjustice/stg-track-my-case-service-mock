package uk.gov.moj.cp.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourtHouseServiceTest {

    private final CourtHouseService courtHouseService = new CourtHouseService();

    @Test
    void courthouseReturnsWestminsterStubJson() {
        ResponseEntity<String> response = courtHouseService.courthouse();

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Westminster Magistrates"));
        assertTrue(response.getBody().contains("courtHouseCode"));
    }
}
