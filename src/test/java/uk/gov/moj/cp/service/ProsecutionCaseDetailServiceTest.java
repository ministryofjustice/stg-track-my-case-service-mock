package uk.gov.moj.cp.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProsecutionCaseDetailServiceTest {

    private final ProsecutionCaseDetailService prosecutionCaseDetailService = new ProsecutionCaseDetailService();

    @Test
    void caseDetailReturnsPcdStubJson() {
        ResponseEntity<String> response = prosecutionCaseDetailService.caseDetail();

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("ACTIVE"));
        assertTrue(response.getBody().contains("caseStatus"));
    }
}
