package uk.gov.moj.cp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.moj.cp.service.CourtHouseService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourtHouseControllerTest {

    @Test
    void courthouseReturnsServiceBody() throws Exception {
        CourtHouseService courtHouseService = new CourtHouseService() {
            @Override
            public ResponseEntity<String> courthouse() {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"courtHouseCode\":\"X\"}");
            }
        };
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CourtHouseController(courtHouseService)).build();

        mockMvc.perform(get("/courthouses/B01IX00"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"courtHouseCode\":\"X\"}"));
    }

    @Test
    void courthouseAndCourtroomReturnsServiceBody() throws Exception {
        CourtHouseService courtHouseService = new CourtHouseService() {
            @Override
            public ResponseEntity<String> courthouse() {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"courtHouseCode\":\"Y\"}");
            }
        };
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CourtHouseController(courtHouseService)).build();

        mockMvc.perform(get("/courthouses/B01IX00/courtrooms/2975"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"courtHouseCode\":\"Y\"}"));
    }
}
