package uk.gov.moj.cp.service;

import com.moj.generated.hmcts.CourtSchedule;
import com.moj.generated.hmcts.CourtSitting;
import com.moj.generated.hmcts.Hearing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

class CourtScheduleServiceTest {

    private final CourtScheduleService courtScheduleService = new CourtScheduleService();

    private static Stream<Arguments> caseUrnParams() {
        return Stream.of(
                // caseUrn,                    expectedHearingType,                                    expectedMonths, expectedDays, expectedSittings

                // -- all hearing types, bare prefix (today, 1 sitting) --
                of("T",    "Trial",                                            0,   0, 1),   // Trial
                of("TNW",  "Trial - no witnesses",                             0,   0, 1),   // Trial - no witnesses
                of("TB",   "Trial (Backer)",                                   0,   0, 1),   // Trial backer
                of("TF",   "Trial (Floater)",                                  0,   0, 1),   // Trial floater
                of("TFW",  "Trial (First Warning)",                            0,   0, 1),   // Trial first warning
                of("TPH",  "Trial (Part Heard)",                               0,   0, 1),   // Trial part heard
                of("TOPI", "Trial of Preliminary Issue",                       0,   0, 1),   // Trial of preliminary issue
                of("TP",   "Trial (Priority)",                                 0,   0, 1),   // Trial priority
                of("TPW",  "Trial (Previously Warned)",                        0,   0, 1),   // Trial previously warned
                of("TR",   "Trial (Reserve)",                                  0,   0, 1),   // Trial reserve
                of("TL",   "Trial Linked",                                     0,   0, 1),   // Trial linked
                of("TFTW", "Trial (Fixed for this Week)",                      0,   0, 1),   // Trial fixed for this week
                of("S",    "Sentence",                                         0,   0, 1),   // Sentence
                of("SAAC", "Sentence (at another Court)",                      0,   0, 1),   // Sentence at another court
                of("SOTA", "Sentence (Officer to Attend)",                     0,   0, 1),   // Sentence - officer to attend
                of("SPTA", "Sentence (Prosecution to Attend)",                 0,   0, 1),   // Sentence - prosecution to attend
                of("SPOA", "Sentence (Prosecution and Officer to Attend)",     0,   0, 1),   // Sentence - prosecution and officer to attend
                of("SPR",  "Sentence (Prosecution Released)",                  0,   0, 1),   // Sentence - prosecution released
                of("CFS",  "Committal for Sentence",                           0,   0, 1),   // Committal for sentence
                of("CSPH", "Committal for Sentence (Part Heard)",              0,   0, 1),   // Committal for sentence - part heard
                of("DS",   "Deferred Sentence",                                0,   0, 1),   // Deferred sentence
                of("DSRR", "Deferred Sentence (Respondent Released)",          0,   0, 1),   // Deferred sentence - respondent released
                of("DSPR", "Deferred Sentence - Prosecution Released",         0,   0, 1),   // Deferred sentence - prosecution released

                // -- body combinations (Trial used as representative type) --
                of("T0D",       "Trial",    0,   0, 1),   // today
                of("T1D",       "Trial",    0,   1, 1),   // 1 day ahead
                of("T99D",      "Trial",    0,  99, 1),   // 99 days ahead
                of("T2M",       "Trial",    2,   0, 1),   // 2 months ahead
                of("T3M5D",     "Trial",    3,   5, 1),   // 3 months and 5 days ahead
                of("TN1D",      "Trial",    0,  -1, 1),   // 1 day back
                of("TN9D",      "Trial",    0,  -9, 1),   // 9 days back
                of("TN",        "Trial",    0,  -1, 1),   // bare N = -1 day
                of("TN1",       "Trial",    0,  -1, 1),   // N1 = same as N1D
                of("TN1M1D",    "Trial",   -1,  -1, 1),   // 1 month and 1 day back (compact negative)
                of("TMD",       "Trial",    1,   1, 1),   // bare MD = +1 month +1 day
                of("T5",        "Trial",    0,   0, 5),   // 5 sittings, today
                of("TN1D2",     "Trial",    0,  -1, 2),   // 2 sittings, 1 day back
                of("TN1M1D2",   "Trial",   -1,  -1, 2),   // 2 sittings, 1 month and 1 day back
                of("TN1D0",     "Trial",    0,  -1, 0),   // 0 sittings, 1 day back

                // -- body combinations on sentence sub-types --
                of("SN2D",      "Sentence",                    0,  -2, 1),   // 2 days back
                of("CFSN3D",    "Committal for Sentence",      0,  -3, 1),   // 3 days back
                of("CFS2M3D",   "Committal for Sentence",      2,   3, 1),   // 2 months and 3 days ahead
                of("DSN1D5",    "Deferred Sentence",           0,  -1, 5),   // 1 day back, 5 sittings
                of("TFTW99M",   "Trial (Fixed for this Week)", 99,  0, 1),   // 99 months ahead

                // -- multiple hearings (MH) --
                of("TMH1",      "Trial",    0,  0, 1),   // second hearing 1 day after last sitting
                of("TMH2",      "Trial",    0,  0, 1),   // second hearing 2 days after last sitting
                of("T0DMH1",    "Trial",    0,  0, 1),   // today, second hearing 1 day after last sitting
                of("TN1DMH2",   "Trial",    0, -1, 1),   // 1 day back, second hearing 2 days after last sitting
                of("TN1D3MH2",  "Trial",    0, -1, 3),   // 3 sittings, second hearing 2 days after last sitting
                of("TMHN1",     "Trial",    0,  0, 1),   // second hearing 1 day before first hearing
                of("T0DMHN1",   "Trial",    0,  0, 1),   // today, second hearing 1 day before first hearing
                of("SN2DMH1",   "Sentence", 0, -2, 1)    // sentence 2 days back, second hearing 1 day after last sitting
        );
    }

    @ParameterizedTest(name = "caseUrn={0} -> hearingType={1}, months={2}, days={3}, sittings={4}")
    @MethodSource("caseUrnParams")
    void courtScheduleForCase(
            String caseUrn,
            String expectedHearingType,
            int expectedMonths,
            int expectedDays,
            int expectedSittings
    ) {
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase(caseUrn);

        assertEquals(1, schedules.size());
        Hearing hearing = schedules.getFirst().getHearings().getFirst();

        assertEquals(expectedHearingType, hearing.getHearingType());
        assertTrue(hearing.getHearingDescription().contains(caseUrn));
        assertEquals(expectedSittings, hearing.getCourtSittings().size());

        if (expectedSittings > 0) {
            ZonedDateTime expected = ZonedDateTime.now()
                    .plusMonths(expectedMonths)
                    .plusDays(expectedDays)
                    .withHour(10).withMinute(0).withSecond(0).withNano(0);
            ZonedDateTime actual = hearing.getCourtSittings().getFirst().getSittingStart();
            assertTrue(Duration.between(expected, actual).abs().toSeconds() < 60,
                    "sittingStart expected ~" + expected + " but was " + actual);
        }
    }

    // -------------------------------------------------------------------------
    // Focused tests for structural assertions not covered by the parameterised test
    // -------------------------------------------------------------------------

    @Test
    void mhUrn_secondHearingStartsAfterLastSitting() {
        // T0D = 1 sitting today; MH1 = second hearing 1 day after the last (only) sitting
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase("T0DMH1");

        assertEquals(1, schedules.size());
        List<Hearing> hearings = schedules.getFirst().getHearings();
        assertEquals(2, hearings.size());

        ZonedDateTime lastSittingStart = hearings.get(0).getCourtSittings().getLast().getSittingStart();
        ZonedDateTime secondStart      = hearings.get(1).getCourtSittings().getFirst().getSittingStart();
        assertNotNull(lastSittingStart);
        assertNotNull(secondStart);
        assertEquals(Duration.ofDays(1), Duration.between(lastSittingStart, secondStart),
                "second hearing should start 1 day after the last sitting");
        assertEquals("Note for first hearing",  hearings.get(0).getListNote());
        assertEquals("Note for second hearing", hearings.get(1).getListNote());
    }

    @Test
    void mhUrn_multiSitting_secondHearingStartsAfterLastSitting() {
        // TN1D3 = 3 sittings starting 1 day back; MH2 = second hearing 2 days after the last sitting
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase("TN1D3MH2");

        List<Hearing> hearings = schedules.getFirst().getHearings();
        assertEquals(2, hearings.size());
        assertEquals(3, hearings.get(0).getCourtSittings().size());

        ZonedDateTime lastSittingStart = hearings.get(0).getCourtSittings().getLast().getSittingStart();
        ZonedDateTime secondStart      = hearings.get(1).getCourtSittings().getFirst().getSittingStart();
        assertEquals(Duration.ofDays(2), Duration.between(lastSittingStart, secondStart),
                "second hearing should start 2 days after the last sitting");
    }

    @Test
    void mhnUrn_secondHearingStartsBeforeFirstHearing() {
        // T0D = today; MHN1 = second hearing 1 day before first hearing start
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase("T0DMHN1");

        List<Hearing> hearings = schedules.getFirst().getHearings();
        assertEquals(2, hearings.size());

        ZonedDateTime firstHearingStart = hearings.get(0).getCourtSittings().getFirst().getSittingStart();
        ZonedDateTime secondStart       = hearings.get(1).getCourtSittings().getFirst().getSittingStart();
        assertEquals(Duration.ofDays(1), Duration.between(secondStart, firstHearingStart),
                "second hearing should start 1 day before the first hearing");
    }

    @Test
    void trialTodayUrn_sittingStructureIsCorrect() {
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase("T0D");

        Hearing hearing = schedules.getFirst().getHearings().getFirst();
        assertNull(hearing.getWeekCommencing());
        assertEquals("Note for first hearing", hearing.getListNote());

        CourtSitting sitting = hearing.getCourtSittings().getFirst();
        assertEquals(Duration.ofHours(1), Duration.between(sitting.getSittingStart(), sitting.getSittingEnd()));
    }

    @Test
    void multiSittingUrn_sittingsAreOneDayApart() {
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase("SN1D2");

        List<CourtSitting> sittings = schedules.getFirst().getHearings().getFirst().getCourtSittings();
        assertEquals(2, sittings.size());

        ZonedDateTime first = sittings.get(0).getSittingStart();
        ZonedDateTime second = sittings.get(1).getSittingStart();
        assertNotNull(first);
        assertNotNull(second);
        assertEquals(Duration.ofDays(1), Duration.between(first, second));
    }

    @Test
    void unknownUrn_returnsUnknownHearingType() {
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase("OTHER");

        Hearing hearing = schedules.getFirst().getHearings().getFirst();
        assertEquals("Unknown", hearing.getHearingType());
        assertEquals(1, hearing.getCourtSittings().size());
    }
}
