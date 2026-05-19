package uk.gov.moj.cp.controller;

import com.moj.generated.hmcts.ProsecutionCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.moj.cp.service.ProsecutionCaseDetailService;

import static uk.gov.moj.cp.util.Utils.executeWithRemainingDelay;

@RestController
public class ProsecutionCaseDetailController {

    private final ProsecutionCaseDetailService prosecutionCaseDetailService;
    private final int responseDelayMillis;

    public ProsecutionCaseDetailController(
            ProsecutionCaseDetailService prosecutionCaseDetailService,
            @Value("${services.pcd_latency_millisecond:0}") int responseDelayMillis
    ) {
        this.prosecutionCaseDetailService = prosecutionCaseDetailService;
        this.responseDelayMillis = responseDelayMillis;
    }

    @GetMapping("/pcd/cases/{caseUrn}")
    @SuppressWarnings("unused")
    public ResponseEntity<ProsecutionCase> pcdCase(@PathVariable String caseUrn) {
        ProsecutionCase prosecutionCase = executeWithRemainingDelay(
                responseDelayMillis,
                () -> prosecutionCaseDetailService.caseDetail(caseUrn)
        );
        return ResponseEntity.ok(prosecutionCase);
    }
}
