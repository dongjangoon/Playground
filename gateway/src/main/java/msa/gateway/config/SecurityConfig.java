package msa.gateway.config;

import msa.gateway.jwt.JwtAuthenticationFilter;
import msa.gateway.jwt.JwtConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean  // 스프링 컨테이너에 의해 관리되는 빈 객체를 생성
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)  // CSRF(Cross-Site Request Forgery) 보호 기능을 비활성화
                .formLogin(AbstractHttpConfigurer::disable)  // 스프링 시큐리티의 기본 로그인 페이지를 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)  // HTTP 기본 인증 비활성화

                // HTTP 요청에 대한 보안 규칙을 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(JwtConstants.WHITELIST).permitAll()  // 지정된 경로들은 인증 없이 접근 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // "/admin/**" 경로는 'ADMIN' 역할을 가진 사용자만 접근 가능
                        .anyRequest().authenticated())  // 그 외 모든 요청은 인증을 요구

                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 세션 관리 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션을 생성하지 않고, 상태를 유지하지 않는 정책 설정

                .build();  // HttpSecurity 객체를 사용하여 SecurityFilterChain을 생성
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // TODO : 주소 변경 필요
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://220.149.232.224:8080", "https://accounts.kakao.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
