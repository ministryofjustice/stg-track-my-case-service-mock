package uk.gov.moj.cp.dto;

import lombok.Getter;

@Getter
public enum HearingType {
    TRIAL("Trial"),
    SENTENCE("Sentence"),
    UNKNOWN("Unknown");

    final String value;

    HearingType(String value) {
        this.value = value;
    }
}
