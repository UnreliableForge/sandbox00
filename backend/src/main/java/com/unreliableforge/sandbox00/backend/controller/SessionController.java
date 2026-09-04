package com.unreliableforge.sandbox00.backend.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nimbusds.jwt.SignedJWT;
import com.unreliableforge.sandbox00.backend.constant.Severity;
import com.unreliableforge.sandbox00.backend.properties.CognitoProperties;
import com.unreliableforge.sandbox00.backend.repository.records.ApiResponse;
import com.unreliableforge.sandbox00.backend.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api")
@Slf4j
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private TokenVerifier tokenVerifier;

    @PostMapping("/v1/session")
    public ResponseEntity<?> session(HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, String> body) throws IOException {
        String idToken = body.get("idToken");

        Map<?, ?> decoded = tokenVerifier.verify(idToken);

        if (decoded == null) {
            var responseBody = new ApiResponse<Void>(Severity.ERROR, "認証されていません");
            return ResponseEntity.status(Response.SC_UNAUTHORIZED).body(responseBody);
        }

        // sub を取り出す
        String sub = (String) decoded.get("sub");

        // セッション発行
        String sessionId = sessionService.createSession(sub);

        log.debug("Session ID:" + sessionId);

        var responseBody = new ApiResponse<Void>(Severity.SUCCESS, "OK");
        return ResponseEntity.ok(responseBody);
    }
}

/**
 * IDTokenを検証する。
 * 環境（SpringProfilesActive）により実装を切り替えるため、interface.
 * TokenVerifier
 */
interface TokenVerifier {

    public Map<String, Object> verify(String idToken);

}

@Component
@Profile("aws")
class CognitoTokenVerifier implements TokenVerifier {

    @Autowired
    private CognitoProperties cognitoProperties;

    private final JwtDecoder jwtDecoder;

    public CognitoTokenVerifier() {
        this.jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(cognitoProperties.url())
                .build();
    }

    @Override
    public Map<String, Object> verify(String idToken) {

        Jwt jwt = jwtDecoder.decode(idToken);

        // iss, aud(clientid)の検証をここで行う。
        if (cognitoProperties.audience().equals(jwt.getAudience().getFirst())) {
            return null;
            // throw new RuntimeException("Invalid token");
        }

        if (cognitoProperties.issuer().equals(jwt.getIssuer().toString())) {
            return null;
            // throw new RuntimeException("Invalid token");
        }

        return jwt.getClaims();
    }
}

@Component
@Profile("local")
class LocalTokenVerifier implements TokenVerifier {

    @Autowired
    private CognitoProperties cognitoProperties;

    @Override
    public Map<String, Object> verify(String idToken) {
        try {
            // 署名検証なしで JWT をパース
            SignedJWT jwt = SignedJWT.parse(idToken);

            // ここで iss,audを検証する必要はないが、サンプルとして。
            if (cognitoProperties.audience().equals(jwt.getJWTClaimsSet().getAudience().getFirst())) {
                return null;
                // throw new RuntimeException("Invalid local token");
            }

            if (cognitoProperties.issuer().equals(jwt.getJWTClaimsSet().getIssuer())) {
                return null;
                // throw new RuntimeException("Invalid local token");
            }

            return jwt.getJWTClaimsSet().getClaims();
        } catch (Exception e) {
            return null;
            // throw new RuntimeException("Invalid local token", e);

        }
    }
}
