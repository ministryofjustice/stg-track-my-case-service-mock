package uk.gov.moj.cp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.moj.cp.service.CourtHouseService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourtHouseControllerTest {

    @Test
    void courthouseReturnsCourtHouseJson() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new CourtHouseController(new CourtHouseService())
        ).build();

        mockMvc.perform(get("/rcc/courthouses/B01IX00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courtHouseCode").value("B01IX00"))
                .andExpect(jsonPath("$.courtHouseName").value("Westminster Magistrates' Court"))
                .andExpect(jsonPath("$.courtRoom[0].courtRoomId").value(2975));
    }

    @Test
    void courthouseAndCourtroomReturnsCourtHouseJson() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new CourtHouseController(new CourtHouseService())
        ).build();

        mockMvc.perform(get("/rcc/courthouses/B01IX00/courtrooms/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courtHouseCode").value("B01IX00"))
                .andExpect(jsonPath("$.courtRoom[0].courtRoomId").value(42));
    }
}
