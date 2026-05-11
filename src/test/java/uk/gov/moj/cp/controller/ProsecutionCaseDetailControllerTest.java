package uk.gov.moj.cp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.moj.cp.service.ProsecutionCaseDetailService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProsecutionCaseDetailControllerTest {

    @Test
    void pcdCaseReturnsServiceBody() throws Exception {
        ProsecutionCaseDetailService prosecutionCaseDetailService = new ProsecutionCaseDetailService() {
            @Override
            public ResponseEntity<String> caseDetail() {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"caseStatus\":\"ACTIVE\"}");
            }
        };
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new ProsecutionCaseDetailController(prosecutionCaseDetailService)
        ).build();

        mockMvc.perform(get("/pcd/cases/any-urn"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"caseStatus\":\"ACTIVE\"}"));
    }
}
