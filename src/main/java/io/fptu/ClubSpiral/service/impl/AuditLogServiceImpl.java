package io.fptu.ClubSpiral.service.impl;

import io.fptu.ClubSpiral.model.entity.AuditLog;
import io.fptu.ClubSpiral.repository.AuditLogRepository;
import io.fptu.ClubSpiral.service.AuditLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    @Override
    public void logAction(List<AuditLog> logs) {
        auditLogRepository.saveAll(logs);
    }

}
