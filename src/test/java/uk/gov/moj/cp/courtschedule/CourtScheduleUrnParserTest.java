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
                // caseUrn, hearingType, months, days, totalSittings, secondHearingDayOffset (null = single hearing)

                // Trial / sentence defaults
                of("T",  HearingType.TRIAL,    0, 0, 1, null),
                of("S",  HearingType.SENTENCE,  0, 0, 1, null),

                // Positive month/day
                of("T99M99D", HearingType.TRIAL, 99, 99, 1, null),
                of("T99D",    HearingType.TRIAL,  0, 99, 1, null),
                of("T99M",    HearingType.TRIAL, 99,  0, 1, null),

                // Negative offsets
                of("TN",    HearingType.TRIAL,    0, -1, 1, null),
                of("TN1",   HearingType.TRIAL,    0, -1, 1, null),
                of("TN9D",  HearingType.TRIAL,    0, -9, 1, null),
                of("SN2D",  HearingType.SENTENCE,  0, -2, 1, null),

                // Compact N#M#D form
                of("TN1M1D",  HearingType.TRIAL, -1, -1, 1, null),
                of("TN1M1D2", HearingType.TRIAL, -1, -1, 2, null),

                // Bare MD defaults to +1 month, +1 day
                of("TMD", HearingType.TRIAL,    1, 1, 1, null),
                of("SMD", HearingType.SENTENCE,  1, 1, 1, null),

                // Hearing count suffix handling
                of("TN1D2", HearingType.TRIAL, 0, -1, 2, null),
                of("TN1D0", HearingType.TRIAL, 0, -1, 0, null),
                of("T5",    HearingType.TRIAL, 0,  0, 5, null),
                of("T1",    HearingType.TRIAL, 0,  0, 1, null),

                // Trial sub-types
                of("TNW",  HearingType.TRIAL_NO_WITNESSES,         0, 0, 1, null),
                of("TB",   HearingType.TRIAL_BACKER,               0, 0, 1, null),
                of("TF",   HearingType.TRIAL_FLOATER,              0, 0, 1, null),
                of("TFW",  HearingType.TRIAL_FIRST_WARNING,        0, 0, 1, null),
                of("TPH",  HearingType.TRIAL_PART_HEARD,           0, 0, 1, null),
                of("TOPI", HearingType.TRIAL_OF_PRELIMINARY_ISSUE, 0, 0, 1, null),
                of("TP",   HearingType.TRIAL_PRIORITY,             0, 0, 1, null),
                of("TPW",  HearingType.TRIAL_PREVIOUSLY_WARNED,    0, 0, 1, null),
                of("TR",   HearingType.TRIAL_RESERVE,              0, 0, 1, null),
                of("TL",   HearingType.TRIAL_LINKED,               0, 0, 1, null),
                of("TFTW", HearingType.TRIAL_FIXED_THIS_WEEK,      0, 0, 1, null),

                // Sentence sub-types
                of("SAAC", HearingType.SENTENCE_AT_ANOTHER_COURT,                   0, 0, 1, null),
                of("SOTA", HearingType.SENTENCE_OFFICER_TO_ATTEND,                  0, 0, 1, null),
                of("SPTA", HearingType.SENTENCE_PROSECUTION_TO_ATTEND,              0, 0, 1, null),
                of("SPOA", HearingType.SENTENCE_PROSECUTION_AND_OFFICER_TO_ATTEND,  0, 0, 1, null),
                of("SPR",  HearingType.SENTENCE_PROSECUTION_RELEASED,               0, 0, 1, null),
                of("CFS",  HearingType.COMMITTAL_FOR_SENTENCE,                      0, 0, 1, null),
                of("CSPH", HearingType.COMMITTAL_FOR_SENTENCE_PART_HEARD,           0, 0, 1, null),
                of("DS",   HearingType.DEFERRED_SENTENCE,                           0, 0, 1, null),
                of("DSRR", HearingType.DEFERRED_SENTENCE_RESPONDENT_RELEASED,       0, 0, 1, null),
                of("DSPR", HearingType.DEFERRED_SENTENCE_PROSECUTION_RELEASED,      0, 0, 1, null),

                // Sub-type with body
                of("TNW1D2",  HearingType.TRIAL_NO_WITNESSES,       0,  1, 2, null),
                of("CFSN3D",  HearingType.COMMITTAL_FOR_SENTENCE,   0, -3, 1, null),
                of("TFTW99M", HearingType.TRIAL_FIXED_THIS_WEEK,   99,  0, 1, null),

                // Multiple hearings — MH<n> (n days after first hearing)
                of("TMH1",     HearingType.TRIAL,    0,  0, 1,  1),   // second hearing 1 day after
                of("TMH2",     HearingType.TRIAL,    0,  0, 1,  2),   // second hearing 2 days after
                of("T0DMH1",   HearingType.TRIAL,    0,  0, 1,  1),   // today + second hearing tomorrow
                of("TN1DMH2",  HearingType.TRIAL,    0, -1, 1,  2),   // 1 day back + second hearing 2 days after first
                of("TN1D2MH1", HearingType.TRIAL,    0, -1, 2,  1),   // 2 sittings + second hearing 1 day after

                // Multiple hearings — MHN<n> (n days before first hearing)
                of("TMHN1",    HearingType.TRIAL,    0,  0, 1, -1),   // second hearing 1 day before
                of("TMHN2",    HearingType.TRIAL,    0,  0, 1, -2),   // second hearing 2 days before
                of("T0DMHN1",  HearingType.TRIAL,    0,  0, 1, -1),   // today + second hearing yesterday
                of("SN2DMH1",  HearingType.SENTENCE, 0, -2, 1,  1),   // sentence 2 days back + second 1 day after

                // Invalid / unknown fallbacks
                of("X",      HearingType.UNKNOWN, 0, 0, 1, null),
                of("OTHER",  HearingType.UNKNOWN, 0, 0, 1, null),
                of(null,     HearingType.UNKNOWN, 0, 0, 1, null)
        );
    }

    @ParameterizedTest(name = "parse({0}) -> type={1}, months={2}, days={3}, sittings={4}, 2ndHearingOffset={5}")
    @MethodSource("parseCaseUrnParams")
    void parseCaseUrnTest(
            String caseUrn,
            HearingType expectedType,
            int expectedMonths,
            int expectedDays,
            int expectedTotalHearings,
            Integer expectedSecondHearingDayOffset
    ) {
        DataSummary summary = CourtScheduleUrnParser.parseCaseUrn(caseUrn);

        assertEquals(expectedType,                 summary.getHearingType(),            "hearingType");
        assertEquals(expectedMonths,               summary.getMonths(),                 "months");
        assertEquals(expectedDays,                 summary.getDays(),                   "days");
        assertEquals(expectedTotalHearings,        summary.getTotalHearings(),          "totalHearings");
        assertEquals(expectedSecondHearingDayOffset, summary.getSecondHearingDayOffset(), "secondHearingDayOffset");
    }
}
