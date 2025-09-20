package io.fptu.ClubSpiral.service;

import io.fptu.ClubSpiral.model.entity.AuditLog;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AuditLogService {

    @Transactional
    void logAction(List<AuditLog> logs);

}
