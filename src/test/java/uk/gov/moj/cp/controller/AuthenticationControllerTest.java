package uk.gov.moj.cp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.moj.cp.service.AuthenticationService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthenticationService authenticationService = new AuthenticationService() {
            @Override
            public ResponseEntity<String> oauthToken() {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"token\":\"mock\"}");
            }
        };
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthenticationController(authenticationService)).build();
    }

    @Test
    void oauthTokenReturnsServiceBody() throws Exception {
        mockMvc.perform(post("/tenant-id/oauth2/v2.0/token"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"mock\"}"));
    }
}
