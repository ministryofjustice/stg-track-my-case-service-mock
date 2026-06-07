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
                of("T",  HearingType.TRIAL,    0, 0, 1),
                of("S",  HearingType.SENTENCE,  0, 0, 1),

                // Positive month/day
                of("T99M99D", HearingType.TRIAL, 99, 99, 1),
                of("T99D",    HearingType.TRIAL,  0, 99, 1),
                of("T99M",    HearingType.TRIAL, 99,  0, 1),

                // Negative offsets
                of("TN",    HearingType.TRIAL,    0, -1, 1),
                of("TN1",   HearingType.TRIAL,    0, -1, 1),
                of("TN9D",  HearingType.TRIAL,    0, -9, 1),
                of("SN2D",  HearingType.SENTENCE,  0, -2, 1),

                // Compact N#M#D form
                of("TN1M1D",  HearingType.TRIAL, -1, -1, 1),
                of("TN1M1D2", HearingType.TRIAL, -1, -1, 2),

                // Bare MD defaults to +1 month, +1 day
                of("TMD", HearingType.TRIAL,    1, 1, 1),
                of("SMD", HearingType.SENTENCE,  1, 1, 1),

                // Hearing count suffix handling
                of("TN1D2", HearingType.TRIAL, 0, -1, 2),
                of("TN1D0", HearingType.TRIAL, 0, -1, 0),
                of("T5",    HearingType.TRIAL, 0,  0, 5),
                of("T1",    HearingType.TRIAL, 0,  0, 1),

                // Trial sub-types
                of("TNW",  HearingType.TRIAL_NO_WITNESSES,         0, 0, 1),
                of("TB",   HearingType.TRIAL_BACKER,               0, 0, 1),
                of("TF",   HearingType.TRIAL_FLOATER,              0, 0, 1),
                of("TFW",  HearingType.TRIAL_FIRST_WARNING,        0, 0, 1),
                of("TPH",  HearingType.TRIAL_PART_HEARD,           0, 0, 1),
                of("TOPI", HearingType.TRIAL_OF_PRELIMINARY_ISSUE, 0, 0, 1),
                of("TP",   HearingType.TRIAL_PRIORITY,             0, 0, 1),
                of("TPW",  HearingType.TRIAL_PREVIOUSLY_WARNED,    0, 0, 1),
                of("TR",   HearingType.TRIAL_RESERVE,              0, 0, 1),
                of("TL",   HearingType.TRIAL_LINKED,               0, 0, 1),
                of("TFTW", HearingType.TRIAL_FIXED_THIS_WEEK,      0, 0, 1),

                // Sentence sub-types
                of("SAAC", HearingType.SENTENCE_AT_ANOTHER_COURT,                   0, 0, 1),
                of("SOTA", HearingType.SENTENCE_OFFICER_TO_ATTEND,                  0, 0, 1),
                of("SPTA", HearingType.SENTENCE_PROSECUTION_TO_ATTEND,              0, 0, 1),
                of("SPOA", HearingType.SENTENCE_PROSECUTION_AND_OFFICER_TO_ATTEND,  0, 0, 1),
                of("SPR",  HearingType.SENTENCE_PROSECUTION_RELEASED,               0, 0, 1),
                of("CFS",  HearingType.COMMITTAL_FOR_SENTENCE,                      0, 0, 1),
                of("CSPH", HearingType.COMMITTAL_FOR_SENTENCE_PART_HEARD,           0, 0, 1),
                of("DS",   HearingType.DEFERRED_SENTENCE,                           0, 0, 1),
                of("DSRR", HearingType.DEFERRED_SENTENCE_RESPONDENT_RELEASED,       0, 0, 1),
                of("DSPR", HearingType.DEFERRED_SENTENCE_PROSECUTION_RELEASED,      0, 0, 1),

                // Sub-type with body
                of("TNW1D2",  HearingType.TRIAL_NO_WITNESSES,  0,  1, 2),
                of("CFSN3D",  HearingType.COMMITTAL_FOR_SENTENCE, 0, -3, 1),
                of("TFTW99M", HearingType.TRIAL_FIXED_THIS_WEEK, 99, 0, 1),

                // Invalid / unknown fallbacks
                of("X",      HearingType.UNKNOWN, 0, 0, 1),
                of("OTHER",  HearingType.UNKNOWN, 0, 0, 1),
                of(null,     HearingType.UNKNOWN, 0, 0, 1)
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
