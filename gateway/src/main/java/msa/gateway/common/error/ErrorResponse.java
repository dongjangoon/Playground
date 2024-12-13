package msa.gateway.common.error;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private int status;
    private String code;

    public ErrorResponse(String message, int status, String code) {
        this.message = message;
        this.status = status;
        this.code = code;
    }
}
