package uk.gov.moj.cp.dto;

import lombok.Getter;

@Getter
public enum HearingType {
    TRIAL("Trial"),
    SENTENCE("Sentence"),
    UNKNOWN("Unknown");

    private final String value;

    HearingType(String value) {
        this.value = value;
    }
}
