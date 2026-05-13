package uk.gov.moj.cp.service;

import com.moj.generated.hmcts.CourtSchedule;
import com.moj.generated.hmcts.CourtSitting;
import com.moj.generated.hmcts.Hearing;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourtScheduleServiceTest {

    private final CourtScheduleService courtScheduleService = new CourtScheduleService();

    @Test
    void trialTodayUrn_returnsSingleScheduleWithOneSitting() {
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase("TMCTR0D");

        assertEquals(1, schedules.size());
        Hearing hearing = schedules.getFirst().getHearings().getFirst();
        assertEquals("TMCTR0D-hearing-id", hearing.getHearingId());
        assertEquals("Trial", hearing.getHearingType());
        assertNull(hearing.getWeekCommencing());
        assertEquals(1, hearing.getCourtSittings().size());

        CourtSitting sitting = hearing.getCourtSittings().getFirst();
        assertEquals("TMCTR0D-judiciary-id-1", sitting.getJudiciaryId());
        assertEquals("TMCTR0D-court-house-id-1", sitting.getCourtHouse());
        assertEquals("TMCTR0D-court-room-id-1", sitting.getCourtRoom());
        assertTrue(hearing.getHearingDescription().contains("TMCTR0D"));
        assertEquals("Note for first hearing", hearing.getListNote());
    }

    @Test
    void sentenceUrnWithMultiDay_returnsMultipleSittingsInOrder() {
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase("TMCSEN1D2");

        assertEquals(1, schedules.size());
        Hearing hearing = schedules.getFirst().getHearings().getFirst();
        assertEquals("Sentence", hearing.getHearingType());
        assertTrue(hearing.getHearingId().startsWith("TMCSEN1D2"));
        List<CourtSitting> sittings = hearing.getCourtSittings();
        assertEquals(2, sittings.size());

        assertEquals("TMCSEN1D2-judiciary-id-1", sittings.get(0).getJudiciaryId());
        assertEquals("TMCSEN1D2-judiciary-id-2", sittings.get(1).getJudiciaryId());

        ZonedDateTime firstStart = sittings.get(0).getSittingStart();
        ZonedDateTime secondStart = sittings.get(1).getSittingStart();
        assertNotNull(firstStart);
        assertNotNull(secondStart);
        assertEquals(Duration.ofDays(1), Duration.between(firstStart, secondStart));

        assertEquals(Duration.ofHours(1), Duration.between(sittings.get(0).getSittingStart(), sittings.get(0).getSittingEnd()));
    }

    @Test
    void urnWithZeroSittings_returnsHearingWithEmptyCourtSittings() {
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase("TMCTRN1D0");

        assertEquals(1, schedules.size());
        Hearing hearing = schedules.getFirst().getHearings().getFirst();
        assertNotNull(hearing.getCourtSittings());
        assertEquals(0, hearing.getCourtSittings().size());
    }

    @Test
    void nonMockUrn_returnsUnknownHearingTypeWithDefaultSittingCount() {
        List<CourtSchedule> schedules = courtScheduleService.courtScheduleForCase("OTHER");

        assertEquals(1, schedules.size());
        Hearing hearing = schedules.getFirst().getHearings().getFirst();
        assertEquals("Unknown", hearing.getHearingType());
        assertEquals("OTHER-hearing-id", hearing.getHearingId());
        assertEquals(1, hearing.getCourtSittings().size());
    }
}
