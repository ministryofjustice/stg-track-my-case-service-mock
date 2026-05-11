package uk.gov.moj.cp.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static uk.gov.moj.cp.util.Utils.getResponse;

@Service
public class ProsecutionCaseDetailService {

    private static final String PCD_CASE_MOCK = "pcd-case.json";

    public ResponseEntity<String> caseDetail() {
        return getResponse(PCD_CASE_MOCK);
    }
}
