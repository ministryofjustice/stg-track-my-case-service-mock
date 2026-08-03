package uk.gov.moj.cp.service;

import com.moj.generated.hmcts.ProsecutionCase;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProsecutionCaseDetailService {

    public ProsecutionCase caseDetail(String caseUrn) {
        ProsecutionCase prosecutionCase = new ProsecutionCase();
        prosecutionCase.setCaseStatus("ACTIVE");
        prosecutionCase.setReportingRestrictions(false);
        return prosecutionCase;
    }
}
