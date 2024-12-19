package msa.gateway.mock;

import msa.gateway.common.error.BusinessException;
import msa.gateway.common.error.CustomException;
import msa.gateway.common.error.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockTestController {

    @GetMapping("/error-test")
    public String throwBusinessException() {
        throw new CustomException("Custom business error occurred", ErrorCode.FRAMEWORK_INTERNAL_ERROR);
    }
}
