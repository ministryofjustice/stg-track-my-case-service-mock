package uk.gov.moj.cp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataSummary {
    HearingType hearingType;
    int months;
    int days;
    int totalHearings;
}
