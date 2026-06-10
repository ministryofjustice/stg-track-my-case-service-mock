package uk.gov.moj.cp.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class HearingTypeTest {

    @Test
    void valuesExposeDisplayStrings() {
        assertEquals("Trial",    HearingType.TRIAL.getValue());
        assertEquals("Sentence", HearingType.SENTENCE.getValue());
        assertEquals("Unknown",  HearingType.UNKNOWN.getValue());
    }

    @Test
    void valuesExposePrefixes() {
        assertEquals("T",    HearingType.TRIAL.getPrefix());
        assertEquals("TNW",  HearingType.TRIAL_NO_WITNESSES.getPrefix());
        assertEquals("TB",   HearingType.TRIAL_BACKER.getPrefix());
        assertEquals("TF",   HearingType.TRIAL_FLOATER.getPrefix());
        assertEquals("TFW",  HearingType.TRIAL_FIRST_WARNING.getPrefix());
        assertEquals("TPH",  HearingType.TRIAL_PART_HEARD.getPrefix());
        assertEquals("TOPI", HearingType.TRIAL_OF_PRELIMINARY_ISSUE.getPrefix());
        assertEquals("TP",   HearingType.TRIAL_PRIORITY.getPrefix());
        assertEquals("TPW",  HearingType.TRIAL_PREVIOUSLY_WARNED.getPrefix());
        assertEquals("TR",   HearingType.TRIAL_RESERVE.getPrefix());
        assertEquals("TL",   HearingType.TRIAL_LINKED.getPrefix());
        assertEquals("TFTW", HearingType.TRIAL_FIXED_THIS_WEEK.getPrefix());
        assertEquals("S",    HearingType.SENTENCE.getPrefix());
        assertEquals("SAAC", HearingType.SENTENCE_AT_ANOTHER_COURT.getPrefix());
        assertEquals("SOTA", HearingType.SENTENCE_OFFICER_TO_ATTEND.getPrefix());
        assertEquals("SPTA", HearingType.SENTENCE_PROSECUTION_TO_ATTEND.getPrefix());
        assertEquals("SPOA", HearingType.SENTENCE_PROSECUTION_AND_OFFICER_TO_ATTEND.getPrefix());
        assertEquals("SPR",  HearingType.SENTENCE_PROSECUTION_RELEASED.getPrefix());
        assertEquals("CFS",  HearingType.COMMITTAL_FOR_SENTENCE.getPrefix());
        assertEquals("CSPH", HearingType.COMMITTAL_FOR_SENTENCE_PART_HEARD.getPrefix());
        assertEquals("DS",   HearingType.DEFERRED_SENTENCE.getPrefix());
        assertEquals("DSRR", HearingType.DEFERRED_SENTENCE_RESPONDENT_RELEASED.getPrefix());
        assertEquals("DSPR", HearingType.DEFERRED_SENTENCE_PROSECUTION_RELEASED.getPrefix());
        assertNull(HearingType.UNKNOWN.getPrefix());
    }

    @Test
    void allNonUnknownValuesHaveUniqueNonNullPrefixes() {
        List<String> prefixes = Arrays.stream(HearingType.values())
                .filter(ht -> ht != HearingType.UNKNOWN)
                .map(HearingType::getPrefix)
                .toList();
        prefixes.forEach(p -> assertNotNull(p, "prefix must not be null"));
        assertEquals(prefixes.size(), prefixes.stream().distinct().count(), "prefixes must be unique");
    }
}
