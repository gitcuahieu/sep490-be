package io.fptu.ClubSpiral.config.security.oauth2;

import io.fptu.ClubSpiral.common.utils.JsonUtils;
import io.fptu.ClubSpiral.common.utils.LocalizedTextUtils;
import io.fptu.ClubSpiral.common.utils.ResponseUtils;
import io.fptu.ClubSpiral.model.dto.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");

        String message = exception.getMessage() != null ? exception.getMessage()
                : LocalizedTextUtils.getLocalizedText("oauth2.authentication.failed");

        BaseResponse<Object> baseResponse = ResponseUtils.error(
                String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                message
        );

        response.getWriter().write(JsonUtils.marshal(baseResponse));
        response.getWriter().flush();
    }
}
