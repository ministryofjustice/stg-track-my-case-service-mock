package uk.gov.moj.cp.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HearingTypeTest {

    @Test
    void valuesExposeDisplayStrings() {
        assertEquals("Trial", HearingType.TRIAL.getValue());
        assertEquals("Sentence", HearingType.SENTENCE.getValue());
        assertEquals("Unknown", HearingType.UNKNOWN.getValue());
    }
}
