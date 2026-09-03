import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.unreliableforge.sandbox00.backend.domain.generated.mapper.SessionsMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionValidationFilter extends OncePerRequestFilter {

    @Autowired
    private final SessionsMapper sessionsMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();

            // DB照会: セッションが有効 かつ ユーザーが存在するか
            boolen isValid = sessionsMapper.selectByPrimaryKey(sessionId).isEmpty();

            boolean isValid = userSessionRepository.findBySessionIdAndExpiresAtAfter(sessionId, LocalDateTime.now())
                    .map(userSession -> userRepository.existsBySub(userSession.getSub()))
                    .orElse(false);

            if (isValid) {
                // 検証OKなら SecurityContext に認証情報をセット
                // (これで後続の Controller や SecurityConfig の hasRole 等が機能する)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("user",
                        null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}