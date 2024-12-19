package msa.gateway.mock;

import msa.gateway.common.error.CustomException;
import msa.gateway.common.error.ErrorType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockTestController {

    @GetMapping("/error-test")
    public String throwBusinessException() {
        throw new CustomException(ErrorType.FRAMEWORK_INTERNAL_ERROR);
    }
}
