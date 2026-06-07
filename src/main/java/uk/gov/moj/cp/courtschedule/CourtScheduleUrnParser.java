package uk.gov.moj.cp.courtschedule;

import uk.gov.moj.cp.dto.HearingType;
import uk.gov.moj.cp.dto.DataSummary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CourtScheduleUrnParser {

    private CourtScheduleUrnParser() {
    }

    public static DataSummary parseCaseUrn(final String caseUrn) {
        //  URN = <hearing-type-prefix> + <body>
        //
        //  Hearing-type prefixes (longest match wins — see HearingType enum for full list):
        //    Trial:    T, TNW, TB, TF, TFW, TPH, TOPI, TP, TPW, TR, TL, TFTW
        //    Sentence: S, SAAC, SOTA, SPTA, SPOA, SPR, CFS, CSPH, DS, DSRR, DSPR
        //
        //  Body:
        //    99M - up to 2 digits, months offset (optional)
        //    99D - up to 2 digits, days offset (optional)
        //    N1  - negative 1 day; N9 - negative 9 days
        //    2-999 trailing digits - number of court sittings
        //
        //  Examples:
        //    T0D        - today, single trial hearing
        //    TN / TN1 / TN1D / TN1D1 - same: start offset -1d, 1 sitting
        //    TN1D0      - 1 day before, 0 court sittings
        //    TN1D2      - 1 day before, 2-day (multi-sitting) hearing
        //    TN1M1D / TN1M1D2 - compact N#M#D: −months and −days; trailing digit = sittings
        //    TMD / SMD  - bare MD (no digits) = 1 month, 1 day, 1 hearing
        //    SN2D5      - sentence started 2 days before, 5 sittings
        //    TR         - Trial (Reserve), default date/sittings
        //    CFS99M     - Committal for Sentence, 99 months out

        HearingType hearingType = HearingType.fromUrn(caseUrn);
        if (hearingType == HearingType.UNKNOWN) {
            return defaultSummary();
        }
        String body = caseUrn.toUpperCase().substring(hearingType.getPrefix().length());
        int totalHearings = 1;
        if (!body.isEmpty()) {
            int i = body.length();
            while (i > 0 && Character.isDigit(body.charAt(i - 1))) {
                i--;
            }
            if (i < body.length()) {
                int suffix = Integer.parseInt(body.substring(i), 10);
                if (i > 0) {
                    char c = body.charAt(i - 1);
                    // N1D1, 0D1, N1D2 — count after M/D; N1D0, 0D0 — zero sittings, strip & parse left part
                    if (c == 'D' || c == 'M') {
                        if (suffix == 0) {
                            totalHearings = 0;
                        } else {
                            totalHearings = suffix;
                        }
                        body = body.substring(0, i);
                    } else if (suffix == 0) {
                        // e.g. trailing 0 with no M/D before digit run — keep body
                    } else if (suffix >= 2) {
                        totalHearings = suffix;
                        body = body.substring(0, i);
                    }
                } else if (suffix >= 2) {
                    // whole body is digits, e.g. "5" → 5 sittings, no M/D
                    totalHearings = suffix;
                    body = body.substring(0, i);
                }
            }
        }
        int months = 0;
        int days = 0;
        Pattern compactNegativeMnD = Pattern.compile("^N(\\d{1,5})M(\\d{1,5})D$");
        Matcher compactMatcher = compactNegativeMnD.matcher(body);
        if (compactMatcher.matches()) {
            // TN1M1D — N1M1D as -1 month and -1 day, not (N1M) + (1D)
            months = -Integer.parseInt(compactMatcher.group(1), 10);
            days = -Integer.parseInt(compactMatcher.group(2), 10);
        } else if ("MD".equals(body)) {
            // TMD / SMD — M and D with no numbers default to 1
            months = 1;
            days = 1;
        } else {
            Pattern bodyPattern = Pattern.compile("^(N?\\d{1,5}M)?(N?\\d{1,5}D)?$");
            Matcher matcher = bodyPattern.matcher(body);
            if (matcher.matches()) {
                months = parseSignedUnit(matcher.group(1));
                days = parseSignedUnit(matcher.group(2));
            } else if ("N".equals(body)) {
                // TN / SN — bare N defaults to N1 (one calendar day back)
                days = -1;
            } else if (body.matches("N\\d{1,5}")) {
                // TN1 — N1 = same as N1D (started one calendar day back), default 1 sitting
                days = -1 * Integer.parseInt(body.substring(1), 10);
            }
        }
        return DataSummary.builder()
                .hearingType(hearingType)
                .months(months)
                .days(days)
                .totalHearings(totalHearings)
                .build();
    }

    private static int parseSignedUnit(String group) {
        if (group == null || group.isEmpty()) {
            return 0;
        }
        boolean negative = group.startsWith("N");
        String digits = negative ? group.substring(1, group.length() - 1) : group.substring(0, group.length() - 1);
        int value = Integer.parseInt(digits, 10);
        return negative ? -value : value;
    }

    private static DataSummary defaultSummary() {
        return DataSummary.builder()
                .hearingType(HearingType.UNKNOWN)
                .months(0)
                .days(0)
                .totalHearings(1)
                .build();
    }
}
