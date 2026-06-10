package uk.gov.moj.cp.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DataSummaryTest {

    @Test
    void builderSetsFields() {
        DataSummary summary = DataSummary.builder()
                .hearingType(HearingType.TRIAL)
                .months(2)
                .days(3)
                .totalHearings(4)
                .build();

        assertEquals(HearingType.TRIAL, summary.getHearingType());
        assertEquals(2, summary.getMonths());
        assertEquals(3, summary.getDays());
        assertEquals(4, summary.getTotalHearings());
        assertNull(summary.getSecondHearingDayOffset());
    }

    @Test
    void builderSetsSecondHearingDayOffset() {
        DataSummary withSecond = DataSummary.builder()
                .hearingType(HearingType.TRIAL)
                .months(0)
                .days(0)
                .totalHearings(1)
                .secondHearingDayOffset(2)
                .build();

        assertEquals(2, withSecond.getSecondHearingDayOffset());
    }
}
