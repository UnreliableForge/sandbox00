package com.unreliableforge.sandbox00.backend.controller;

import java.util.Map;

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
import com.unreliableforge.sandbox00.backend.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private TokenVerifier tokenVerifier;

    @PostMapping("/v1/session")
    public ResponseEntity<?> createSession(HttpServletRequest request, @RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");

        // 環境に応じて切り替わる
        Map<?, ?> decoded = tokenVerifier.verify(idToken);

        // sub を取り出す
        String sub = (String) decoded.get("sub");

        // セッション発行
        String sessionId = sessionService.createSession(sub);

        return ResponseEntity.ok().build();
    }
}

interface TokenVerifier {

    public Map<String, Object> verify(String idToken);

}

@Component
@Profile("aws")
class CognitoTokenVerifier implements TokenVerifier {

    private final JwtDecoder jwtDecoder;

    public CognitoTokenVerifier() {
        this.jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri("https://cognito-idp.ap-northeast-1.amazonaws.com/<USER_POOL_ID>/.well-known/jwks.json")
                .build();
    }

    // iss, subの検証をここで行う。

    @Override
    public Map<String, Object> verify(String idToken) {
        Jwt jwt = jwtDecoder.decode(idToken);
        return Map.of("sub", jwt.getClaim("sub"));
    }
}

@Component
@Profile("local")
class LocalTokenVerifier implements TokenVerifier {

    @Override
    public Map<String, Object> verify(String idToken) {
        try {
            // 署名検証なしで JWT をパース
            SignedJWT jwt = SignedJWT.parse(idToken);
            return jwt.getJWTClaimsSet().getClaims();
        } catch (Exception e) {
            throw new RuntimeException("Invalid local token", e);
        }
    }
}
