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

        ZonedDateTime firstHearingDate = ZonedDateTime.now()
                .plusMonths(mockDataSummary.getMonths())
                .plusDays(mockDataSummary.getDays())
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        hearings.add(buildHearing(caseUrn, mockDataSummary.getHearingType().getValue(),
                firstHearingDate, mockDataSummary.getTotalHearings(), "Note for first hearing"));

        if (mockDataSummary.getSecondHearingDayOffset() != null) {
            int offset = mockDataSummary.getSecondHearingDayOffset();
            ZonedDateTime secondHearingDate;
            if (offset < 0) {
                // Negative: count back from the start of the first hearing
                secondHearingDate = firstHearingDate.plusDays(offset);
            } else {
                // Positive: count forward from the last sitting of the first hearing
                int lastSittingIndex = Math.max(0, mockDataSummary.getTotalHearings() - 1);
                secondHearingDate = firstHearingDate.plusDays(lastSittingIndex + offset);
            }
            hearings.add(buildHearing(caseUrn, mockDataSummary.getHearingType().getValue(),
                    secondHearingDate, 1, "Note for second hearing"));
        }

        return List.of(new CourtSchedule(hearings));
    }

    private Hearing buildHearing(String caseUrn, String hearingType, ZonedDateTime startDate,
                                 int totalSittings, String listNote) {
        List<CourtSitting> courtSittings = new ArrayList<>();
        ZonedDateTime sittingDate = startDate;
        for (int i = 0; i < totalSittings; i++) {
            courtSittings.add(new CourtSitting(
                    sittingDate,
                    sittingDate.plusHours(1),
                    randomUUID().toString(),
                    randomUUID().toString(),
                    randomUUID().toString()
            ));
            sittingDate = sittingDate.plusDays(1);
        }

        return new Hearing(
                randomUUID().toString(),
                hearingType,
                "Follow-up " + hearingType.toLowerCase() + " hearing description for case " + caseUrn,
                listNote,
                null,
                courtSittings
        );
    }
}
