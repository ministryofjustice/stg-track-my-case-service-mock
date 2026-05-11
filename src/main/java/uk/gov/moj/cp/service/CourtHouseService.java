package uk.gov.moj.cp.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static uk.gov.moj.cp.util.Utils.getResponse;

@Service
public class CourtHouseService {

    private static final String COURTHOUSE_MOCK = "courthouse.json";

    public ResponseEntity<String> courthouse() {
        return getResponse(COURTHOUSE_MOCK);
    }
}
