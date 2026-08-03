package uk.gov.moj.cp.service;

import com.moj.generated.hmcts.Address;
import com.moj.generated.hmcts.CourtHouse;
import com.moj.generated.hmcts.CourtRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourtHouseService {

    private static final int DEFAULT_COURT_ROOM_ID = 2975;

    public CourtHouse courthouse(final String courthouseId, final String courtroomId) {
        return getCourtHouse(courthouseId, courtroomId);
    }

    public CourtHouse courthouse(final String courthouseId) {
        return getCourtHouse(courthouseId, null);
    }

    private CourtHouse getCourtHouse(final String courthouseId, final String courtroomId) {
        /*int courtRoomId = DEFAULT_COURT_ROOM_ID;
        if (courtroomId != null && !courtroomId.isBlank()) {
            courtRoomId = Integer.parseInt(courtroomId);
        }*/
        Address address = new Address(
                "181 Marylebone Road",
                "London",
                null,
                null,
                "NW1 5BR",
                "UK"
        );
        CourtRoom courtRoom = new CourtRoom(DEFAULT_COURT_ROOM_ID, "Courtroom 01");
        return new CourtHouse(
                CourtHouse.CourtHouseType.MAGISTRATE,
                courthouseId,
                "Westminster Magistrates' Court",
                address,
                List.of(courtRoom)
        );
    }
}
