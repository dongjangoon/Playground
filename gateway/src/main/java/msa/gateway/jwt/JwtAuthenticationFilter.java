package msa.gateway.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
//    private RedisUtil redisUtil;

    private static void checkAuthorizationHeader(String header) {
        if (header == null) {
//            throw new CustomJwtException("토큰이 전달되지 않았습니다");
        } else if (!header.startsWith(JwtConstants.JWT_TYPE)) {
//            throw new CustomJwtException("BEARER 로 시작하지 않는 올바르지 않은 토큰 형식입니다");
        }
    }

    // 필터를 거치지 않을 URL 및 메서드를 설정하고, true 를 return 하면 현재 필터를 건너뛰고 다음 필터로 이동
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        // 특정 경로에 대해 GET 요청과 POST, PUT, DELETE 요청을 모두 필터링하도록 설정
        if (PatternMatchUtils.simpleMatch("/api/users/**", requestURI)) {
            return false;
        }

        // 인증 code 로직은 필터링 하지 않음
        if (PatternMatchUtils.simpleMatch("/api/auth/**", requestURI)) {
            return true;
        }

        // POST, PUT, DELETE 요청이 아닌 경우 = GET 요청인 경우
        if (!httpMethod.equalsIgnoreCase("POST") && !httpMethod.equalsIgnoreCase("PUT") && !httpMethod.equalsIgnoreCase("DELETE")) {
            for (String pattern : JwtConstants.WHITELIST) {
                if (PatternMatchUtils.simpleMatch(pattern, requestURI)) {
                    return true; // 화이트리스트에 포함된 경우 필터를 거치지 않음 : Get & 화이트리스트
                }
            }
            return false; // 화이트리스트에 포함되지 않은 경우 필터를 거침 : Get이지만 화이트리스트 아님
        }

        // POST, PUT, DELETE 요청인 경우
        for (String pattern : JwtConstants.WHITELIST) {
            if (PatternMatchUtils.simpleMatch(pattern, requestURI)) {
                return false; // 화이트리스트에 포함되더라도 필터를 거침
            }
        }

        return false; // POST, PUT, DELETE 요청이면서 화이트리스트에 포함되지 않은 경우
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(JwtConstants.JWT_HEADER);
            checkAuthorizationHeader(authHeader); // header 형식 검사
            String token = JwtUtil.getTokenFromHeader(authHeader);

//            if (redisUtil.isTokenBlacklisted(token)) { // 토큰 블랙리스트 검사
//                throw new CustomJwtException("로그인 세션이 만료되었습니다.");
//            }

            Authentication authentication = JwtUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handleAuthenticationError(response, e);
        }
    }

    private void handleAuthenticationError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("{\"error\":\"" + e.getMessage() + "\"}");
        writer.close();
    }
}
