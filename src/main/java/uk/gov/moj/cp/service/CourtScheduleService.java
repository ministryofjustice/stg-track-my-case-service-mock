package uk.gov.moj.cp.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.moj.cp.courtschedule.CourtScheduleUrnParser;
import uk.gov.moj.cp.dto.DataSummary;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static uk.gov.moj.cp.util.Utils.objectMapper;

@Service
public class CourtScheduleService {

    private static final DateTimeFormatter ISO_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public ResponseEntity<String> courtScheduleForCase(String caseUrn) {
        DataSummary summary = CourtScheduleUrnParser.parseCaseUrn(caseUrn);

        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode courtSchedule = root.putArray("courtSchedule");

        ObjectNode schedule = objectMapper.createObjectNode();
        ArrayNode hearings = schedule.putArray("hearings");

        ObjectNode hearing = objectMapper.createObjectNode();
        String hearingType = summary.getHearingType().getValue();
        hearing.put("hearingId", caseUrn + "-hearing-id");
        hearing.put("hearingType", hearingType);
        hearing.put("hearingDescription", "Follow-up " + hearingType.toLowerCase() + " hearing description for case " + caseUrn);
        hearing.put("listNote", "Note for first hearing");
        hearing.putNull("weekCommencing");

        ArrayNode courtSittings = hearing.putArray("courtSittings");

        ZonedDateTime futureDate = ZonedDateTime.now()
                .plusMonths(summary.getMonths())
                .plusDays(summary.getDays())
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        for (int i = 0; i < summary.getTotalHearings(); i++) {
            ZonedDateTime sittingStart = futureDate;
            ZonedDateTime sittingEnd = futureDate.plusHours(1);

            ObjectNode sitting = objectMapper.createObjectNode();
            sitting.put("sittingStart", ISO_OFFSET.format(sittingStart));
            sitting.put("sittingEnd", ISO_OFFSET.format(sittingEnd));
            sitting.put("judiciaryId", caseUrn + "-judiciary-id-" + (i + 1));
            sitting.put("courtHouse", caseUrn + "-court-house-id-" + (i + 1));
            sitting.put("courtRoom", caseUrn + "-court-room-id-" + (i + 1));
            courtSittings.add(sitting);

            futureDate = futureDate.plusDays(1);
        }

        hearings.add(hearing);
        courtSchedule.add(schedule);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(root.toString());
    }
}
