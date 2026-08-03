package uk.gov.moj.cp.controller;

import com.moj.generated.hmcts.CourtSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.moj.cp.service.CourtScheduleService;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.params.provider.Arguments.of;

class CourtScheduleControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CourtScheduleController(new CourtScheduleService(), 0))
                .build();
    }

    // caseUrn, expectedHearingType, expectedSittings, expectedHearingCount
    private static Stream<Arguments> caseUrnParams() {
        return Stream.of(
                // -- all hearing types, bare prefix --
                of("T",    "Trial",                                            1, 1),   // Trial
                of("TNW",  "Trial - no witnesses",                             1, 1),   // Trial - no witnesses
                of("TB",   "Trial (Backer)",                                   1, 1),   // Trial backer
                of("TF",   "Trial (Floater)",                                  1, 1),   // Trial floater
                of("TFW",  "Trial (First Warning)",                            1, 1),   // Trial first warning
                of("TPH",  "Trial (Part Heard)",                               1, 1),   // Trial part heard
                of("TOPI", "Trial of Preliminary Issue",                       1, 1),   // Trial of preliminary issue
                of("TP",   "Trial (Priority)",                                 1, 1),   // Trial priority
                of("TPW",  "Trial (Previously Warned)",                        1, 1),   // Trial previously warned
                of("TR",   "Trial (Reserve)",                                  1, 1),   // Trial reserve
                of("TL",   "Trial Linked",                                     1, 1),   // Trial linked
                of("TFTW", "Trial (Fixed for this Week)",                      1, 1),   // Trial fixed for this week
                of("S",    "Sentence",                                         1, 1),   // Sentence
                of("SAAC", "Sentence (at another Court)",                      1, 1),   // Sentence at another court
                of("SOTA", "Sentence (Officer to Attend)",                     1, 1),   // Sentence - officer to attend
                of("SPTA", "Sentence (Prosecution to Attend)",                 1, 1),   // Sentence - prosecution to attend
                of("SPOA", "Sentence (Prosecution and Officer to Attend)",     1, 1),   // Sentence - prosecution and officer to attend
                of("SPR",  "Sentence (Prosecution Released)",                  1, 1),   // Sentence - prosecution released
                of("CFS",  "Committal for Sentence",                           1, 1),   // Committal for sentence
                of("CSPH", "Committal for Sentence (Part Heard)",              1, 1),   // Committal for sentence - part heard
                of("DS",   "Deferred Sentence",                                1, 1),   // Deferred sentence
                of("DSRR", "Deferred Sentence (Respondent Released)",          1, 1),   // Deferred sentence - respondent released
                of("DSPR", "Deferred Sentence - Prosecution Released",         1, 1),   // Deferred sentence - prosecution released

                // -- body combinations (Trial used as representative type) --
                of("T0D",       "Trial",    1, 1),   // today
                of("T1D",       "Trial",    1, 1),   // 1 day ahead
                of("T99D",      "Trial",    1, 1),   // 99 days ahead
                of("T2M",       "Trial",    1, 1),   // 2 months ahead
                of("T3M5D",     "Trial",    1, 1),   // 3 months and 5 days ahead
                of("TN1D",      "Trial",    1, 1),   // 1 day back
                of("TN9D",      "Trial",    1, 1),   // 9 days back
                of("TN",        "Trial",    1, 1),   // bare N = -1 day
                of("TN1",       "Trial",    1, 1),   // N1 = same as N1D
                of("TN1M1D",    "Trial",    1, 1),   // 1 month and 1 day back (compact negative)
                of("TMD",       "Trial",    1, 1),   // bare MD = +1 month +1 day
                of("T5",        "Trial",    5, 1),   // 5 sittings, today
                of("TN1D2",     "Trial",    2, 1),   // 2 sittings, 1 day back
                of("TN1M1D2",   "Trial",    2, 1),   // 2 sittings, 1 month and 1 day back
                of("TN1D0",     "Trial",    0, 1),   // 0 sittings, 1 day back

                // -- body combinations on sentence sub-types --
                of("SN2D",      "Sentence",                    1, 1),   // 2 days back
                of("CFSN3D",    "Committal for Sentence",      1, 1),   // 3 days back
                of("CFS2M3D",   "Committal for Sentence",      1, 1),   // 2 months and 3 days ahead
                of("DSN1D5",    "Deferred Sentence",           5, 1),   // 1 day back, 5 sittings
                of("TFTW99M",   "Trial (Fixed for this Week)", 1, 1),   // 99 months ahead

                // -- MH1: all hearing types, second hearing 1 day after last sitting --
                of("TMH1",    "Trial",                                            1, 2),
                of("TNWMH1",  "Trial - no witnesses",                             1, 2),
                of("TBMH1",   "Trial (Backer)",                                   1, 2),
                of("TFMH1",   "Trial (Floater)",                                  1, 2),
                of("TFWMH1",  "Trial (First Warning)",                            1, 2),
                of("TPHMH1",  "Trial (Part Heard)",                               1, 2),
                of("TOPIMH1", "Trial of Preliminary Issue",                       1, 2),
                of("TPMH1",   "Trial (Priority)",                                 1, 2),
                of("TPWMH1",  "Trial (Previously Warned)",                        1, 2),
                of("TRMH1",   "Trial (Reserve)",                                  1, 2),
                of("TLMH1",   "Trial Linked",                                     1, 2),
                of("TFTWMH1", "Trial (Fixed for this Week)",                      1, 2),
                of("SMH1",    "Sentence",                                         1, 2),
                of("SAACMH1", "Sentence (at another Court)",                      1, 2),
                of("SOTAMH1", "Sentence (Officer to Attend)",                     1, 2),
                of("SPTAMH1", "Sentence (Prosecution to Attend)",                 1, 2),
                of("SPOAMH1", "Sentence (Prosecution and Officer to Attend)",     1, 2),
                of("SPRMH1",  "Sentence (Prosecution Released)",                  1, 2),
                of("CFSMH1",  "Committal for Sentence",                           1, 2),
                of("CSPHMH1", "Committal for Sentence (Part Heard)",              1, 2),
                of("DSMH1",   "Deferred Sentence",                                1, 2),
                of("DSRRMH1", "Deferred Sentence (Respondent Released)",          1, 2),
                of("DSPRMH1", "Deferred Sentence - Prosecution Released",         1, 2),

                // -- MHN1: all hearing types, second hearing 1 day before first hearing --
                of("TMHN1",    "Trial",                                            1, 2),
                of("TNWMHN1",  "Trial - no witnesses",                             1, 2),
                of("TBMHN1",   "Trial (Backer)",                                   1, 2),
                of("TFMHN1",   "Trial (Floater)",                                  1, 2),
                of("TFWMHN1",  "Trial (First Warning)",                            1, 2),
                of("TPHMHN1",  "Trial (Part Heard)",                               1, 2),
                of("TOPIMHN1", "Trial of Preliminary Issue",                       1, 2),
                of("TPMHN1",   "Trial (Priority)",                                 1, 2),
                of("TPWMHN1",  "Trial (Previously Warned)",                        1, 2),
                of("TRMHN1",   "Trial (Reserve)",                                  1, 2),
                of("TLMHN1",   "Trial Linked",                                     1, 2),
                of("TFTWMHN1", "Trial (Fixed for this Week)",                      1, 2),
                of("SMHN1",    "Sentence",                                         1, 2),
                of("SAACMHN1", "Sentence (at another Court)",                      1, 2),
                of("SOTAMHN1", "Sentence (Officer to Attend)",                     1, 2),
                of("SPTAMHN1", "Sentence (Prosecution to Attend)",                 1, 2),
                of("SPOAMHN1", "Sentence (Prosecution and Officer to Attend)",     1, 2),
                of("SPRMHN1",  "Sentence (Prosecution Released)",                  1, 2),
                of("CFSMHN1",  "Committal for Sentence",                           1, 2),
                of("CSPHMHN1", "Committal for Sentence (Part Heard)",              1, 2),
                of("DSMHN1",   "Deferred Sentence",                                1, 2),
                of("DSRRMHN1", "Deferred Sentence (Respondent Released)",          1, 2),
                of("DSPRMHN1", "Deferred Sentence - Prosecution Released",         1, 2),

                // -- MH2: all hearing types, second hearing 2 days after last sitting --
                of("TMH2",    "Trial",                                            1, 2),
                of("TNWMH2",  "Trial - no witnesses",                             1, 2),
                of("TBMH2",   "Trial (Backer)",                                   1, 2),
                of("TFMH2",   "Trial (Floater)",                                  1, 2),
                of("TFWMH2",  "Trial (First Warning)",                            1, 2),
                of("TPHMH2",  "Trial (Part Heard)",                               1, 2),
                of("TOPIMH2", "Trial of Preliminary Issue",                       1, 2),
                of("TPMH2",   "Trial (Priority)",                                 1, 2),
                of("TPWMH2",  "Trial (Previously Warned)",                        1, 2),
                of("TRMH2",   "Trial (Reserve)",                                  1, 2),
                of("TLMH2",   "Trial Linked",                                     1, 2),
                of("TFTWMH2", "Trial (Fixed for this Week)",                      1, 2),
                of("SMH2",    "Sentence",                                         1, 2),
                of("SAACMH2", "Sentence (at another Court)",                      1, 2),
                of("SOTAMH2", "Sentence (Officer to Attend)",                     1, 2),
                of("SPTAMH2", "Sentence (Prosecution to Attend)",                 1, 2),
                of("SPOAMH2", "Sentence (Prosecution and Officer to Attend)",     1, 2),
                of("SPRMH2",  "Sentence (Prosecution Released)",                  1, 2),
                of("CFSMH2",  "Committal for Sentence",                           1, 2),
                of("CSPHMH2", "Committal for Sentence (Part Heard)",              1, 2),
                of("DSMH2",   "Deferred Sentence",                                1, 2),
                of("DSRRMH2", "Deferred Sentence (Respondent Released)",          1, 2),
                of("DSPRMH2", "Deferred Sentence - Prosecution Released",         1, 2),

                // -- MHN2: all hearing types, second hearing 2 days before first hearing --
                of("TMHN2",    "Trial",                                            1, 2),
                of("TNWMHN2",  "Trial - no witnesses",                             1, 2),
                of("TBMHN2",   "Trial (Backer)",                                   1, 2),
                of("TFMHN2",   "Trial (Floater)",                                  1, 2),
                of("TFWMHN2",  "Trial (First Warning)",                            1, 2),
                of("TPHMHN2",  "Trial (Part Heard)",                               1, 2),
                of("TOPIMHN2", "Trial of Preliminary Issue",                       1, 2),
                of("TPMHN2",   "Trial (Priority)",                                 1, 2),
                of("TPWMHN2",  "Trial (Previously Warned)",                        1, 2),
                of("TRMHN2",   "Trial (Reserve)",                                  1, 2),
                of("TLMHN2",   "Trial Linked",                                     1, 2),
                of("TFTWMHN2", "Trial (Fixed for this Week)",                      1, 2),
                of("SMHN2",    "Sentence",                                         1, 2),
                of("SAACMHN2", "Sentence (at another Court)",                      1, 2),
                of("SOTAMHN2", "Sentence (Officer to Attend)",                     1, 2),
                of("SPTAMHN2", "Sentence (Prosecution to Attend)",                 1, 2),
                of("SPOAMHN2", "Sentence (Prosecution and Officer to Attend)",     1, 2),
                of("SPRMHN2",  "Sentence (Prosecution Released)",                  1, 2),
                of("CFSMHN2",  "Committal for Sentence",                           1, 2),
                of("CSPHMHN2", "Committal for Sentence (Part Heard)",              1, 2),
                of("DSMHN2",   "Deferred Sentence",                                1, 2),
                of("DSRRMHN2", "Deferred Sentence (Respondent Released)",          1, 2),
                of("DSPRMHN2", "Deferred Sentence - Prosecution Released",         1, 2),

                // -- MH/MHN with body (date + sitting combinations) --
                of("T0DMH1",    "Trial",    1, 2),   // today + MH1
                of("T0DMH2",    "Trial",    1, 2),   // today + MH2
                of("T0DMHN1",   "Trial",    1, 2),   // today + MHN1
                of("T0DMHN2",   "Trial",    1, 2),   // today + MHN2
                of("TN1DMH1",   "Trial",    1, 2),   // 1 day back + MH1
                of("TN1DMH2",   "Trial",    1, 2),   // 1 day back + MH2
                of("TN1DMHN1",  "Trial",    1, 2),   // 1 day back + MHN1
                of("TN1DMHN2",  "Trial",    1, 2),   // 1 day back + MHN2
                of("T1DMH1",    "Trial",    1, 2),   // 1 day ahead + MH1
                of("T1DMHN1",   "Trial",    1, 2),   // 1 day ahead + MHN1
                of("TN1D2MH1",  "Trial",    2, 2),   // 2 sittings + MH1
                of("TN1D2MH2",  "Trial",    2, 2),   // 2 sittings + MH2
                of("TN1D2MHN1", "Trial",    2, 2),   // 2 sittings + MHN1
                of("TN1D2MHN2", "Trial",    2, 2),   // 2 sittings + MHN2
                of("TN1D3MH2",  "Trial",    3, 2),   // 3 sittings + MH2
                of("SN2DMH1",   "Sentence", 1, 2),   // sentence 2 days back + MH1
                of("SN2DMH2",   "Sentence", 1, 2),   // sentence 2 days back + MH2
                of("SN2DMHN1",  "Sentence", 1, 2),   // sentence 2 days back + MHN1
                of("SN2DMHN2",  "Sentence", 1, 2),   // sentence 2 days back + MHN2
                of("CFSMH1",    "Committal for Sentence", 1, 2),   // CFS + MH1
                of("CFSMHN1",   "Committal for Sentence", 1, 2),   // CFS + MHN1
                of("DSMH2",     "Deferred Sentence",      1, 2),   // DS + MH2
                of("DSMHN2",    "Deferred Sentence",      1, 2)    // DS + MHN2
        );
    }

    @ParameterizedTest(name = "GET /slc/case/{0}/courtschedule -> hearingType={1}, sittings={2}, hearings={3}")
    @MethodSource("caseUrnParams")
    void courtScheduleForAllCaseUrns(
            String caseUrn,
            String expectedHearingType,
            int expectedSittings,
            int expectedHearingCount
    ) throws Exception {
        ResultActions result = mockMvc.perform(get("/slc/case/{caseUrn}/courtschedule", caseUrn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courtSchedule").isArray())
                .andExpect(jsonPath("$.courtSchedule.length()").value(1))
                .andExpect(jsonPath("$.courtSchedule[0].hearings").isArray())
                .andExpect(jsonPath("$.courtSchedule[0].hearings.length()").value(expectedHearingCount))
                .andExpect(jsonPath("$.courtSchedule[0].hearings[0].hearingType").value(expectedHearingType))
                .andExpect(jsonPath("$.courtSchedule[0].hearings[0].courtSittings.length()").value(expectedSittings));

        if (expectedSittings > 0) {
            result
                    .andExpect(jsonPath("$.courtSchedule[0].hearings[0].courtSittings[0].sittingStart").exists())
                    .andExpect(jsonPath("$.courtSchedule[0].hearings[0].courtSittings[0].sittingEnd").exists())
                    .andExpect(jsonPath("$.courtSchedule[0].hearings[0].courtSittings[0].sittingStart").isNotEmpty())
                    .andExpect(jsonPath("$.courtSchedule[0].hearings[0].courtSittings[0].sittingEnd").isNotEmpty());
        }

        if (expectedHearingCount == 2) {
            result
                    .andExpect(jsonPath("$.courtSchedule[0].hearings[1].hearingType").value(expectedHearingType))
                    .andExpect(jsonPath("$.courtSchedule[0].hearings[1].courtSittings.length()").value(1))
                    .andExpect(jsonPath("$.courtSchedule[0].hearings[1].courtSittings[0].sittingStart").exists())
                    .andExpect(jsonPath("$.courtSchedule[0].hearings[1].courtSittings[0].sittingEnd").exists())
                    .andExpect(jsonPath("$.courtSchedule[0].hearings[1].listNote").value("Note for second hearing"));
        }
    }

    @Test
    void courtScheduleDelegatesToService() throws Exception {
        CourtScheduleService courtScheduleService = new CourtScheduleService() {
            @Override
            public List<CourtSchedule> courtScheduleForCase(String caseUrn) {
                if ("URN123".equals(caseUrn)) {
                    return List.of(new CourtSchedule(List.of()));
                }
                return super.courtScheduleForCase(caseUrn);
            }
        };
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CourtScheduleController(courtScheduleService, 0)).build();

        mockMvc.perform(get("/slc/case/URN123/courtschedule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courtSchedule").isArray())
                .andExpect(jsonPath("$.courtSchedule.length()").value(1))
                .andExpect(jsonPath("$.courtSchedule[0].hearings").isArray())
                .andExpect(jsonPath("$.courtSchedule[0].hearings").isEmpty());
    }
}
