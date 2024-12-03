package msa.gateway.jwt;

public class CustomJwtException extends RuntimeException {
    public CustomJwtException(String msg) {
        super(msg);
    }
}
