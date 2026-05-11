package uk.gov.moj.cp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.moj.cp.service.CourtScheduleService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourtScheduleControllerTest {

    @Test
    void courtScheduleDelegatesToService() throws Exception {
        CourtScheduleService courtScheduleService = new CourtScheduleService() {
            @Override
            public ResponseEntity<String> courtScheduleForCase(String caseUrn) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"courtSchedule\":[]}");
            }
        };
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CourtScheduleController(courtScheduleService)).build();

        mockMvc.perform(get("/case/URN123/courtschedule"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"courtSchedule\":[]}"));
    }
}
