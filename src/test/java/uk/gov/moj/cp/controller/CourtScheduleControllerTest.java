package uk.gov.moj.cp.controller;

import com.moj.generated.hmcts.CourtSchedule;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.moj.cp.service.CourtScheduleService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourtScheduleControllerTest {

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
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CourtScheduleController(courtScheduleService)).build();

        mockMvc.perform(get("/case/URN123/courtschedule"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.courtSchedule").isArray())
            .andExpect(jsonPath("$.courtSchedule.length()").value(1))
            .andExpect(jsonPath("$.courtSchedule[0].hearings").isArray())
            .andExpect(jsonPath("$.courtSchedule[0].hearings").isEmpty());
    }
}
