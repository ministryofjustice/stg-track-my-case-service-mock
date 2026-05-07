package uk.gov.moj.cp.transformer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.Response;
import uk.gov.moj.cp.dto.HearingType;
import uk.gov.moj.cp.dto.MockDataSummary;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.gov.moj.cp.util.Utils.objectMapper;

public class CourtScheduleResponseTransformer implements ResponseTransformerV2 {

    public static final String NAME = "court-schedule-transformer";

    private static final DateTimeFormatter ISO_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private static final String MOCK_DATA_URN_PREFIX_TMC = "TMC";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Response transform(Response response, ServeEvent serveEvent) {
        String url = serveEvent.getRequest().getUrl(); // includes query string if any

        String caseUrn = extractCaseUrn(url);
        MockDataSummary summary = parseCaseUrn(caseUrn);

        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode courtSchedule = root.putArray("courtSchedule");

        ObjectNode schedule = objectMapper.createObjectNode();
        ArrayNode hearings = schedule.putArray("hearings");

        ObjectNode hearing = objectMapper.createObjectNode();
        String hearingType = summary.getHearingType().getValue();
        hearing.put("hearingId", caseUrn + "-hearing-id");
        hearing.put("hearingType", hearingType);
        hearing.put("hearingDescription", "Follow-up " + hearingType.toLowerCase() + " hearing description for case " + caseUrn);
        hearing.put("listNote", "Note for first hearing");
        hearing.putNull("weekCommencing");

        ArrayNode courtSittings = hearing.putArray("courtSittings");

        ZonedDateTime futureDate = ZonedDateTime.now()
            .plusMonths(summary.getMonths())
            .plusDays(summary.getDays())
            .withHour(10)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        for (int i = 0; i < summary.getTotalHearings(); i++) {
            ZonedDateTime sittingStart = futureDate;
            ZonedDateTime sittingEnd = futureDate.plusHours(1);

            ObjectNode sitting = objectMapper.createObjectNode();
            sitting.put("sittingStart", ISO_OFFSET.format(sittingStart));
            sitting.put("sittingEnd", ISO_OFFSET.format(sittingEnd));
            sitting.put("judiciaryId", caseUrn + "-judiciary-id-" + (i + 1));
            sitting.put("courtHouse", caseUrn + "-court-house-id-" + (i + 1));
            sitting.put("courtRoom", caseUrn + "-court-room-id-" + (i + 1));
            courtSittings.add(sitting);

            futureDate = futureDate.plusDays(1);
        }

        hearings.add(hearing);
        courtSchedule.add(schedule);

        return Response.Builder.like(response)
            .but()
            .status(200)
            .headers(new HttpHeaders(new HttpHeader("Content-Type", "application/json")))
            .body(root.toString())
            .build();
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    private static String extractCaseUrn(String url) {
        // expected: /case/{case_urn}/courtschedule
        Pattern p = Pattern.compile("^/case/([^/?]+)/courtschedule(?:\\?.*)?$");
        Matcher m = p.matcher(url);
        return m.matches() ? m.group(1) : "UNKNOWN";
    }

    public static MockDataSummary parseCaseUrn(final String caseUrn) {
        //    TMCTR99M99D / TMCTR99D / TMCTR99M / TMCTRN9D / TMCTRN1D5
        //    TMCSE99M99D / TMCSE99D / TMCSE99M / TMCSEN9M / TMCSEN1D3
        //    TMC - is custom test prefix
        //    TR or SE - Trial or Sentence type of hearing
        //    99M - maximum 2 digits number of months (optional)
        //    99D - maximum 2 digits number of days (optional)
        //    N1 - negative 1 day, N9 - negative 9 days
        //    2-999 number at the end - multi day hearing (totalHearings, 1–3 digits)
        //
        //    TMCTR0D - today single trial hearing
        //    TMCTRN / TMCTRN1 / TMCTRN1D / TMCTRN1D1 - same: N defaults to N1; start offset -1d, default 1 day / 1 sitting
        //    TMCTRN1D0 - N1D offset, 0 court sittings (still -1d for generateData anchor)
        //    TMCTRN1D2 - 1 day before, multi day hearing (2 days)
        //    TMCTRN1M1D / TMCTRN1M1D2 - compact N#M#D: −months and −day counts (N1M1D → -1M -1D); trailing digit = sittings
        //    TMCTRMD / TMCSEMD - bare MD (no digits) = 1 month, 1 day, 1 hearing
        //    TMCSEN2D5 - sentencing hearing started 2 days before and has multi day hearing (for 5 days)
        //
        //    TMCTRV1 /  TMCTRV21 - trial hearing with custom data, id=1 / id=21 (future feature, like multi hearing)

        if (caseUrn == null || caseUrn.length() < 5 || !caseUrn.toUpperCase().startsWith(MOCK_DATA_URN_PREFIX_TMC)) {
            return defaultSummary();
        }
        String rest = caseUrn.substring(MOCK_DATA_URN_PREFIX_TMC.length());
        HearingType hearingType = rest.startsWith(HearingType.TRIAL.getValue().substring(
                0,
                2
        ).toUpperCase()) ? HearingType.TRIAL
                : rest.startsWith(HearingType.SENTENCE.getValue().substring(0, 2).toUpperCase()) ? HearingType.SENTENCE
                : null;
        if (hearingType == null) {
            return defaultSummary();
        }
        String body = rest.substring(2);
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
            // TMCTRN1M1D — N1M1D as -1 month and -1 day, not (N1M) + (1D)
            months = -Integer.parseInt(compactMatcher.group(1), 10);
            days = -Integer.parseInt(compactMatcher.group(2), 10);
        } else if ("MD".equals(body)) {
            // TMCTRMD / TMCSEMD — M and D with no numbers default to 1
            months = 1;
            days = 1;
        } else {
            Pattern bodyPattern = Pattern.compile("^(N?\\d{1,5}M)?(N?\\d{1,5}D)?$");
            Matcher matcher = bodyPattern.matcher(body);
            if (matcher.matches()) {
                months = parseSignedUnit(matcher.group(1));
                days = parseSignedUnit(matcher.group(2));
            } else if ("N".equals(body)) {
                // TMCTRN / TMCSEN — bare N defaults to N1 (one calendar day back)
                days = -1;
            } else if (body.matches("N\\d{1,5}")) {
                // TMCTRN1 — N1 = same as N1D (started one calendar day back), default 1 day/sitting
                days = -1 * Integer.parseInt(body.substring(1), 10);
            }
        }
        return MockDataSummary.builder()
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

    private static MockDataSummary defaultSummary() {
        return MockDataSummary.builder()
                .hearingType(HearingType.UNKNOWN)
                .months(0)
                .days(0)
                .totalHearings(1)
                .build();
    }

}

