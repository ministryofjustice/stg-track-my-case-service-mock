package uk.gov.moj.cp.controller;

import com.moj.generated.hmcts.CourtScheduleSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.moj.cp.service.CourtScheduleService;

@RestController
public class CourtScheduleController {

    private final CourtScheduleService courtScheduleService;

    public CourtScheduleController(CourtScheduleService courtScheduleService) {
        this.courtScheduleService = courtScheduleService;
    }

    @GetMapping("/case/{caseUrn}/courtschedule")
    public ResponseEntity<CourtScheduleSchema>  courtSchedule(@PathVariable String caseUrn) {
        return ResponseEntity.ok(new CourtScheduleSchema(courtScheduleService.courtScheduleForCase(caseUrn)));
    }
}
