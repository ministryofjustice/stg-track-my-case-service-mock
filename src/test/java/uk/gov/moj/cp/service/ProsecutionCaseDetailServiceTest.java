package uk.gov.moj.cp.service;

import com.moj.generated.hmcts.ProsecutionCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ProsecutionCaseDetailServiceTest {

    private final ProsecutionCaseDetailService prosecutionCaseDetailService = new ProsecutionCaseDetailService();

    @Test
    void caseDetailReturnsPcdStubJson() {
        String caseUrn = "2NX12W";
        ProsecutionCase prosecutionCase = prosecutionCaseDetailService.caseDetail(caseUrn);
        assertEquals("ACTIVE",  prosecutionCase.getCaseStatus());
        assertFalse(prosecutionCase.isReportingRestrictions());
    }
}
