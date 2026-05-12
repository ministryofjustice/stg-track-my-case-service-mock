package uk.gov.moj.cp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.moj.cp.service.ProsecutionCaseDetailService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProsecutionCaseDetailControllerTest {

    @Test
    void pcdCaseReturnsServiceBody() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new ProsecutionCaseDetailController(new ProsecutionCaseDetailService())
        ).build();

        mockMvc.perform(get("/pcd/cases/2NXABC12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.reportingRestrictions").value(false));
    }
}
