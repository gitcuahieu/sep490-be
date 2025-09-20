package io.fptu.ClubSpiral.common.exception.handler;

import io.fptu.ClubSpiral.common.exception.CustomBusinessException;
import io.fptu.ClubSpiral.common.utils.LocalizedTextUtils;
import io.fptu.ClubSpiral.common.utils.ResponseUtils;
import io.fptu.ClubSpiral.model.dto.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class CustomControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> exceptionHandler(HttpServletRequest request, Exception e) {
        log.error(e.getMessage(), e);
        if (e instanceof BindException bindException) {
            var message = bindException.getBindingResult().getFieldErrors().stream()
                    .map(error -> LocalizedTextUtils.getLocalizedText(error.getDefaultMessage()))
                    .collect(Collectors.joining(", "));

            var response = ResponseUtils.error(
                    String.valueOf(HttpStatus.BAD_REQUEST), message);
            return ResponseEntity.ok(response);
        }
        if (e instanceof HandlerMethodValidationException validationException) {
            var message = validationException.getValueResults().stream()
                    .flatMap(result -> result.getResolvableErrors().stream())
                    .map(err -> LocalizedTextUtils.getLocalizedText(err.getDefaultMessage()))
                    .collect(Collectors.joining(", "));

            var response = ResponseUtils.error(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), message);
            return ResponseEntity.ok(response);
        }
        var response = ResponseUtils.error(
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
                LocalizedTextUtils.getLocalizedText("internal.server.error")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<?> handleCustomBusinessException(CustomBusinessException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(e.getErrorCode(), e.getMessage())
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(
                        String.valueOf(HttpStatus.BAD_REQUEST),
                        LocalizedTextUtils.getLocalizedText("auth.authenticate.fail")
                )
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(
                        String.valueOf(HttpStatus.FORBIDDEN), e.getMessage()
                )
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleDisabledException(DisabledException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(String.valueOf(HttpStatus.FORBIDDEN), e.getMessage()));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<?> handleLockedException(LockedException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(String.valueOf(HttpStatus.FORBIDDEN), e.getMessage()));
    }

}