package io.fptu.ClubSpiral.common.aop;


import io.fptu.ClubSpiral.model.entity.AuditLog;
import io.fptu.ClubSpiral.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object logAction(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
    
        var endpoint = request.getRequestURI();
        var actionType = request.getMethod();

        var auditLog = AuditLog.builder()
                .actionType(actionType)
                .endPoint(endpoint)
                .log("SYSTEM" + " called " + endpoint)
                .status("PENDING")
                .build();

        log.info("[PENDING] {} called {}", "SYSTEM", endpoint);

        auditLogService.logAction(List.of(auditLog));

        try {
            var result = joinPoint.proceed();
            auditLog.setStatus("SUCCESS");
            auditLog.setLog("SYSTEM" + " successfully called " + endpoint);

            log.info("[SUCCESS] {} successfully called {}", "SYSTEM", endpoint);

            auditLogService.logAction(List.of(auditLog));
            return result;
        } catch (Throwable ex) {
            auditLog.setStatus("FAIL");
            auditLog.setLog("SYSTEM" + " exception: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());

            log.error("[FAIL] {} called {} -> {}: {}", "SYSTEM", endpoint,
                    ex.getClass().getSimpleName(), ex.getMessage(), ex);

            auditLogService.logAction(List.of(auditLog));
            throw ex;
        }
    }
}

