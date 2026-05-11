package uk.gov.moj.cp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StgTrackMyCaseServiceMockIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"UP\"}"));
    }

    @Test
    void oauthTokenEndpoint() throws Exception {
        mockMvc.perform(post("/mytenant/oauth2/v2.0/token").contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("access_token")));
    }

    @Test
    void courthouseEndpoint() throws Exception {
        mockMvc.perform(get("/courthouses/B01IX00"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Westminster")));
    }

    @Test
    void courthouseCourtroomEndpoint() throws Exception {
        mockMvc.perform(get("/courthouses/B01IX00/courtrooms/2975"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("courtHouseCode")));
    }

    @Test
    void pcdCaseEndpoint() throws Exception {
        mockMvc.perform(get("/pcd/cases/test-urn"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ACTIVE")));
    }

    @Test
    void courtScheduleEndpoint() throws Exception {
        mockMvc.perform(get("/case/TMCTR0D/courtschedule"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("courtSchedule")))
                .andExpect(content().string(containsString("Trial")));
    }
}
