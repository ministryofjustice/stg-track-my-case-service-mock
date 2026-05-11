package uk.gov.moj.cp.controller;

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

    @GetMapping("/courthouses/{courthouseId}/courtrooms/{courtroomId}")
    @SuppressWarnings("unused")
    public ResponseEntity<String> courthouseAndCourtroom(
            @PathVariable String courthouseId,
            @PathVariable String courtroomId
    ) {
        return courtHouseService.courthouse();
    }

    @GetMapping("/courthouses/{courthouseId}")
    @SuppressWarnings("unused")
    public ResponseEntity<String> courthouse(@PathVariable String courthouseId) {
        return courtHouseService.courthouse();
    }
}
