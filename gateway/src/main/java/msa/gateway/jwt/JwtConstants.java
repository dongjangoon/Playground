package msa.gateway.jwt;

public class JwtConstants {
    public static final String key = "DG3K2NG9lK3T2FLfnO283HO1NFLAy9FGJ23UM9Rv923YRV923HT";
    public static final int ACCESS_EXP_TIME = 10;   // 10분
    public static final int REFRESH_EXP_TIME = 60 * 24;   // 24시간

    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_TYPE = "Bearer ";
    public static final String[] WHITELIST = {
            "/api/comments",
            "/api/comments/**",
            "/api/images",
            "/api/images/**",
            "/api/posts",
            "/api/posts/**",
            "/api/reviews",
            "/api/reviews/**",
            "/api/auth",
            "/api/auth/**",
            "/api/places",
            "/api/places/**",
            "/signUp",
            "/login",
            "/refresh",
            "/",
            "/index.html",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/webjars/**"
    };
}
