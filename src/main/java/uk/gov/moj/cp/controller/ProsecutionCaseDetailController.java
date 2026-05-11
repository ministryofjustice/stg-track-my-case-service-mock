package uk.gov.moj.cp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.moj.cp.service.ProsecutionCaseDetailService;

@RestController
public class ProsecutionCaseDetailController {

    private final ProsecutionCaseDetailService prosecutionCaseDetailService;

    public ProsecutionCaseDetailController(ProsecutionCaseDetailService prosecutionCaseDetailService) {
        this.prosecutionCaseDetailService = prosecutionCaseDetailService;
    }

    @GetMapping("/pcd/cases/{caseUrn}")
    @SuppressWarnings("unused")
    public ResponseEntity<String> pcdCase(@PathVariable String caseUrn) {
        return prosecutionCaseDetailService.caseDetail();
    }
}
