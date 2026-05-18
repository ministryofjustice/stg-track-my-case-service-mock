package uk.gov.moj.cp.controller;

import com.moj.generated.hmcts.CourtHouse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.moj.cp.service.CourtHouseService;

import static uk.gov.moj.cp.util.Utils.executeWithRemainingDelay;

@RestController
public class CourtHouseController {

    private final CourtHouseService courtHouseService;
    private final int responseDelayMillis;

    public CourtHouseController(
            CourtHouseService courtHouseService,
            @Value("${services.courtHouseController:0}") int responseDelayMillis
    ) {
        this.courtHouseService = courtHouseService;
        this.responseDelayMillis = responseDelayMillis;
    }

    @GetMapping("/rcc/courthouses/{courthouseId}/courtrooms/{courtroomId}")
    @SuppressWarnings("unused")
    public ResponseEntity<CourtHouse> courthouseAndCourtroom(
            @PathVariable String courthouseId,
            @PathVariable String courtroomId
    ) {
        CourtHouse courtHouse = executeWithRemainingDelay(
                responseDelayMillis,
                () -> courtHouseService.courthouse(courthouseId, courtroomId)
        );
        return ResponseEntity.ok(courtHouse);
    }

    @GetMapping("/rcc/courthouses/{courthouseId}")
    @SuppressWarnings("unused")
    public ResponseEntity<CourtHouse> courthouse(@PathVariable String courthouseId) {
        CourtHouse courtHouse = executeWithRemainingDelay(
                responseDelayMillis,
                () -> courtHouseService.courthouse(courthouseId)
        );
        return ResponseEntity.ok(courtHouse);
    }
}
