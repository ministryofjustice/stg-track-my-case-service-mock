package uk.gov.moj.cp.controller;

import com.moj.generated.hmcts.CourtScheduleSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.moj.cp.service.CourtScheduleService;

import static uk.gov.moj.cp.util.Utils.executeWithRemainingDelay;

@RestController
public class CourtScheduleController {

    private final CourtScheduleService courtScheduleService;
    private final int responseDelayMillis;

    public CourtScheduleController(
            CourtScheduleService courtScheduleService,
            @Value("${services.rcc_latency_millisecond:0}") int responseDelayMillis
    ) {
        this.courtScheduleService = courtScheduleService;
        this.responseDelayMillis = responseDelayMillis;
    }

    @GetMapping("/slc/case/{caseUrn}/courtschedule")
    public ResponseEntity<CourtScheduleSchema> courtSchedule(@PathVariable String caseUrn) {
        CourtScheduleSchema courtScheduleSchema = executeWithRemainingDelay(
                responseDelayMillis,
                () -> new CourtScheduleSchema(courtScheduleService.courtScheduleForCase(caseUrn))
        );
        return ResponseEntity.ok(courtScheduleSchema);
    }
}
