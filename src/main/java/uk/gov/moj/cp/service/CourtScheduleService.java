package uk.gov.moj.cp.service;

import com.moj.generated.hmcts.CourtSchedule;
import com.moj.generated.hmcts.CourtSitting;
import com.moj.generated.hmcts.Hearing;
import org.springframework.stereotype.Service;
import uk.gov.moj.cp.courtschedule.CourtScheduleUrnParser;
import uk.gov.moj.cp.dto.DataSummary;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

@Service
public class CourtScheduleService {

    public List<CourtSchedule> courtScheduleForCase(final String caseUrn) {
        DataSummary summary = CourtScheduleUrnParser.parseCaseUrn(caseUrn);
        return generateData(caseUrn, summary);
    }

    private List<CourtSchedule> generateData(final String caseUrn, DataSummary mockDataSummary) {
        List<Hearing> hearings = new ArrayList<>();

        ZonedDateTime futureDate = ZonedDateTime.now()
                .plusMonths(mockDataSummary.getMonths())
                .plusDays(mockDataSummary.getDays())
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        List<CourtSitting> courtSittings = new ArrayList<>();

        int days = mockDataSummary.getTotalHearings();
        for (int i = 0; i < days; i++) {
            ZonedDateTime sittingStart = futureDate;
            ZonedDateTime sittingEnd = futureDate.plusHours(1);

            final String judiciaryId = randomUUID().toString();
            final String courtHouseId = randomUUID().toString();
            final String courtRoomId = randomUUID().toString();

            final CourtSitting courtSitting = new CourtSitting(
                    sittingStart,
                    sittingEnd,
                    judiciaryId,
                    courtHouseId,
                    courtRoomId
            );
            courtSittings.add(courtSitting);

            futureDate = futureDate.plusDays(1);
        }

        final String hearingType = mockDataSummary.getHearingType().getValue();
        final String hearingId = randomUUID().toString();
        final Hearing hearing = new Hearing(
                hearingId,
                hearingType,
                "Follow-up " + hearingType.toLowerCase() + " hearing description for case " + caseUrn,
                "Note for first hearing",
                null,
                courtSittings
        );

        hearings.add(hearing);
        return List.of(new CourtSchedule(hearings));
    }
}
