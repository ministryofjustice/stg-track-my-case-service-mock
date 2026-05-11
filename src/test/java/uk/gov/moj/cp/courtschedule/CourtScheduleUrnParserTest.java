package uk.gov.moj.cp.courtschedule;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.moj.cp.dto.DataSummary;
import uk.gov.moj.cp.dto.HearingType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

class CourtScheduleUrnParserTest {

    private static Stream<Arguments> parseCaseUrnParams() {
        return Stream.of(
                // Trial / sentence defaults
                of("TMCTR", HearingType.TRIAL, 0, 0, 1),
                of("TMCSE", HearingType.SENTENCE, 0, 0, 1),

                // Positive month/day
                of("TMCTR99M99D", HearingType.TRIAL, 99, 99, 1),
                of("TMCTR99D", HearingType.TRIAL, 0, 99, 1),
                of("TMCTR99M", HearingType.TRIAL, 99, 0, 1),

                // Negative offsets
                of("TMCTRN", HearingType.TRIAL, 0, -1, 1),
                of("TMCTRN1", HearingType.TRIAL, 0, -1, 1),
                of("TMCTRN9D", HearingType.TRIAL, 0, -9, 1),
                of("TMCSEN2D", HearingType.SENTENCE, 0, -2, 1),

                // Compact N#M#D form
                of("TMCTRN1M1D", HearingType.TRIAL, -1, -1, 1),
                of("TMCTRN1M1D2", HearingType.TRIAL, -1, -1, 2),

                // Bare MD defaults to +1 month, +1 day
                of("TMCTRMD", HearingType.TRIAL, 1, 1, 1),
                of("TMCSEMD", HearingType.SENTENCE, 1, 1, 1),

                // Hearing count suffix handling
                of("TMCTRN1D2", HearingType.TRIAL, 0, -1, 2),
                of("TMCTRN1D0", HearingType.TRIAL, 0, -1, 0),
                of("TMCTR5", HearingType.TRIAL, 0, 0, 5),
                of("TMCTR1", HearingType.TRIAL, 0, 0, 1),

                // Invalid / unknown fallbacks
                of("X", HearingType.UNKNOWN, 0, 0, 1),
                of("TMCXX", HearingType.UNKNOWN, 0, 0, 1),
                of(null, HearingType.UNKNOWN, 0, 0, 1)
        );
    }

    @ParameterizedTest(name = "parse({0}) -> type={1}, months={2}, days={3}, hearings={4}")
    @MethodSource("parseCaseUrnParams")
    void parseCaseUrnTest(
            String caseUrn,
            HearingType expectedType,
            int expectedMonths,
            int expectedDays,
            int expectedTotalHearings
    ) {
        DataSummary summary = CourtScheduleUrnParser.parseCaseUrn(caseUrn);

        assertEquals(expectedType, summary.getHearingType(), "hearingType");
        assertEquals(expectedMonths, summary.getMonths(), "months");
        assertEquals(expectedDays, summary.getDays(), "days");
        assertEquals(expectedTotalHearings, summary.getTotalHearings(), "totalHearings");
    }
}
