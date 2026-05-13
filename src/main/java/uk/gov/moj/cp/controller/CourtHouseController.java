package uk.gov.moj.cp.controller;

import com.moj.generated.hmcts.CourtHouse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.moj.cp.service.CourtHouseService;

@RestController
public class CourtHouseController {

    private final CourtHouseService courtHouseService;

    public CourtHouseController(CourtHouseService courtHouseService) {
        this.courtHouseService = courtHouseService;
    }

    @GetMapping("/rcc/courthouses/{courthouseId}/courtrooms/{courtroomId}")
    @SuppressWarnings("unused")
    public ResponseEntity<CourtHouse> courthouseAndCourtroom(
            @PathVariable String courthouseId,
            @PathVariable String courtroomId
    ) {
        return ResponseEntity.ok(courtHouseService.courthouse(courthouseId, courtroomId));
    }

    @GetMapping("/rcc/courthouses/{courthouseId}")
    @SuppressWarnings("unused")
    public ResponseEntity<CourtHouse> courthouse(@PathVariable String courthouseId) {
        return ResponseEntity.ok(courtHouseService.courthouse(courthouseId));
    }
}
