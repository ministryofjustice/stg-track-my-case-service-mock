package uk.gov.moj.cp.service;

import com.moj.generated.hmcts.CourtHouse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtHouseServiceTest {

    private final CourtHouseService courtHouseService = new CourtHouseService();
    private final int DEFAULT_COURT_ROOM_ID = 2975;

    @Test
    void courthouseByIdUsesDefaultCourtRoom() {
        CourtHouse courtHouse = courtHouseService.courthouse("B01IX00");

        assertEquals("B01IX00", courtHouse.getCourtHouseCode());
        assertEquals("Westminster Magistrates' Court", courtHouse.getCourtHouseName());
        assertEquals(DEFAULT_COURT_ROOM_ID, courtHouse.getCourtRoom().getFirst().getCourtRoomId());
    }

    @Test
    void courthouseAndCourtroomUsesPathCourtRoomId() {
        CourtHouse courtHouse = courtHouseService.courthouse("B01IX00", "123");

        assertEquals(DEFAULT_COURT_ROOM_ID, courtHouse.getCourtRoom().getFirst().getCourtRoomId());
    }
}
