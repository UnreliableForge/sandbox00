package com.unreliableforge.sandbox00.backend.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.unreliableforge.sandbox00.backend.domain.extension.entity.UserSessionEntity;
import com.unreliableforge.sandbox00.backend.domain.extension.repository.UserSessionRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionValidationFilter extends OncePerRequestFilter {

    @Autowired
    public UserSessionRepository userSessionRepository;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    List<String> notFilterPath = List.of("/api/v1/login", "/api/v1/health", "/api/v1/session");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return notFilterPath.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            // filterから例外をthrowする場合、このようにしないとGlobalHandlerでハンドルされないらしい
            resolver.resolveException(request, response, null,
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "認証されていません。"));
            return;
        }

        String sessionId = session.getId();

        Optional<UserSessionEntity> userSession = userSessionRepository.validSession(sessionId, LocalDateTime.now());

        if (userSession == null) {
            resolver.resolveException(request, response, null,
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "認証されていません。"));
            return;
        }

        // セッションIDとsubで検索。レコードが存在しない場合、無効なセッションとする。
        if (userSession.isEmpty()) {
            resolver.resolveException(request, response, null,
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "認証されていません。"));
            return;
        }

        UserSessionEntity entity = userSession.get();

        // 検証OKなら SecurityContext に認証情報をセット。
        // これで後続の Controller や SecurityConfig の hasRole等が機能する。
        // 現在は仮の値を設定している。
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
                entity.getSub(),
                null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        session.setAttribute(
                org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        filterChain.doFilter(request, response);
    }
}
