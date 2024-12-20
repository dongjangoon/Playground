package msa.gateway.mock;

import msa.gateway.common.error.CustomException;
import msa.gateway.common.error.ErrorType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class MockTestController {

    @GetMapping("/error-test")
    public String throwBusinessException() {
        throw new CustomException(ErrorType.FRAMEWORK_INTERNAL_ERROR);
    }

    @GetMapping("/jwt-test")
    public String jwtTester() {
        return "JWT 토큰이 유효합니다.";
    }
}

