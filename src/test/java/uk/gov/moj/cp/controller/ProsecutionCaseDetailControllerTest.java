package uk.gov.moj.cp.controller;

import com.moj.generated.hmcts.ProsecutionCase;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.moj.cp.service.ProsecutionCaseDetailService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProsecutionCaseDetailControllerTest {

   /* @Mock
    ProsecutionCaseDetailService prosecutionCaseDetailService;*/

    @Test
    void pcdCaseReturnsServiceBody() throws Exception {
        final String caseurn = "2NXABC12";
        final ProsecutionCase prosecutionCase = new ProsecutionCase();
        prosecutionCase.setCaseStatus("ACTIVE");
        prosecutionCase.setReportingRestrictions(false);

        ProsecutionCaseDetailService prosecutionCaseDetailService = mock(ProsecutionCaseDetailService.class);
        when(prosecutionCaseDetailService.caseDetail(caseurn)).thenReturn(prosecutionCase);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new ProsecutionCaseDetailController(prosecutionCaseDetailService)
        ).build();

        mockMvc.perform(get("/pcd/cases/any-urn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.reportingRestrictions").value(false));
    }
}
