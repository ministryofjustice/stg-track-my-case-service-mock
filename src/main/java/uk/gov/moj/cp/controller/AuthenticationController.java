package uk.gov.moj.cp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.moj.cp.service.AuthenticationService;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/{tenant}/oauth2/v2.0/token")
    @SuppressWarnings("unused")
    public ResponseEntity<String> oauthToken(@PathVariable String tenant) {
        return authenticationService.oauthToken();
    }
}
