package uk.gov.moj.cp.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static uk.gov.moj.cp.util.Utils.getResponse;

@Service
public class AuthenticationService {

    private static final String OAUTH_TOKEN_MOCK = "oauth-token.json";

    public ResponseEntity<String> oauthToken() {
        return getResponse(OAUTH_TOKEN_MOCK);
    }
}
