package uk.gov.moj.cp.dto;

import lombok.Getter;

@Getter
public enum HearingType {
    TRIAL_FIXED_THIS_WEEK         ("Trial (Fixed for this Week)",                     "TFTW"),
    TRIAL_OF_PRELIMINARY_ISSUE    ("Trial of Preliminary Issue",                      "TOPI"),
    TRIAL_NO_WITNESSES            ("Trial - no witnesses",                            "TNW"),
    TRIAL_FIRST_WARNING           ("Trial (First Warning)",                           "TFW"),
    TRIAL_PART_HEARD              ("Trial (Part Heard)",                              "TPH"),
    TRIAL_PREVIOUSLY_WARNED       ("Trial (Previously Warned)",                       "TPW"),
    TRIAL_BACKER                  ("Trial (Backer)",                                  "TB"),
    TRIAL_FLOATER                 ("Trial (Floater)",                                 "TF"),
    TRIAL_PRIORITY                ("Trial (Priority)",                                "TP"),
    TRIAL_RESERVE                 ("Trial (Reserve)",                                 "TR"),
    TRIAL_LINKED                  ("Trial Linked",                                    "TL"),
    TRIAL                         ("Trial",                                           "T"),
    SENTENCE_AT_ANOTHER_COURT     ("Sentence (at another Court)",                     "SAAC"),
    SENTENCE_OFFICER_TO_ATTEND    ("Sentence (Officer to Attend)",                    "SOTA"),
    SENTENCE_PROSECUTION_TO_ATTEND("Sentence (Prosecution to Attend)",                "SPTA"),
    SENTENCE_PROSECUTION_AND_OFFICER_TO_ATTEND("Sentence (Prosecution and Officer to Attend)", "SPOA"),
    COMMITTAL_FOR_SENTENCE_PART_HEARD("Committal for Sentence (Part Heard)",          "CSPH"),
    DEFERRED_SENTENCE_RESPONDENT_RELEASED("Deferred Sentence (Respondent Released)",  "DSRR"),
    DEFERRED_SENTENCE_PROSECUTION_RELEASED("Deferred Sentence - Prosecution Released","DSPR"),
    SENTENCE_PROSECUTION_RELEASED ("Sentence (Prosecution Released)",                 "SPR"),
    COMMITTAL_FOR_SENTENCE        ("Committal for Sentence",                          "CFS"),
    DEFERRED_SENTENCE             ("Deferred Sentence",                               "DS"),
    SENTENCE                      ("Sentence",                                        "S"),
    UNKNOWN                       ("Unknown",                                         null);

    private final String value;
    private final String prefix;

    HearingType(String value, String prefix) {
        this.value = value;
        this.prefix = prefix;
    }

    public static HearingType getHearingTypeFromCaseUrn(String urn) {
        if (urn == null || urn.isEmpty()) {
            return UNKNOWN;
        }
        String upper = urn.toUpperCase();
        for (HearingType ht : values()) {
            if (ht.prefix != null && upper.startsWith(ht.prefix)) {
                return ht;
            }
        }
        return UNKNOWN;
    }
}
