package com.guide.run.global.webhook.appsmith.controller;

import com.guide.run.admin.dto.response.UserApprovalResult;
import com.guide.run.admin.service.UserApprovalService;
import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.webhook.appsmith.dto.AppsmithUserApprovalRequest;
import com.guide.run.user.entity.type.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

@Slf4j
@RestController
@CrossOrigin(origins = "https://grp.appsmith.com")
public class AppsmithUserApprovalWebhookController {

    private static final String SECRET_HEADER = "X-Appsmith-Webhook-Secret";

    private final UserApprovalService userApprovalService;
    private final String webhookSecret;

    public AppsmithUserApprovalWebhookController(
            UserApprovalService userApprovalService,
            @Value("${spring.appsmith.webhook-secret}") String webhookSecret
    ) {
        this.userApprovalService = userApprovalService;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/webhook/appsmith/user-approval")
    public ResponseEntity<?> approveUser(
            @RequestHeader(value = SECRET_HEADER, required = false) String headerSecret,
            @RequestBody(required = false) AppsmithUserApprovalRequest request
    ) {
        if (!isValidSecret(headerSecret)) {
            return fail(HttpStatus.FORBIDDEN, "FORBIDDEN", "Invalid AppSmith webhook secret.");
        }

        if (request == null || !StringUtils.hasText(request.getUserId())) {
            return fail(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "userId is required.");
        }

        Role targetRole = parseRole(request.getRole());
        if (targetRole == null) {
            return fail(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "role is required and must be valid.");
        }

        String recordDegree = request.getResolvedRecordDegree();
        if (!StringUtils.hasText(recordDegree)) {
            return fail(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "recordDegree is required.");
        }

        UserApprovalResult response = userApprovalService.processUserApproval(
                request.getUserId(),
                targetRole,
                recordDegree,
                request.getRequestedBy()
        );
        return ResponseEntity.ok(response);
    }

    private boolean isValidSecret(String headerSecret) {
        if (!StringUtils.hasText(webhookSecret) || !StringUtils.hasText(headerSecret)) {
            log.warn("AppSmith webhook secret missing or not configured.");
            return false;
        }

        boolean matches = MessageDigest.isEqual(
                webhookSecret.getBytes(StandardCharsets.UTF_8),
                headerSecret.getBytes(StandardCharsets.UTF_8)
        );

        if (!matches) {
            log.warn("AppSmith webhook secret mismatch.");
        }
        return matches;
    }

    private ResponseEntity<FailResult> fail(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new FailResult(code, message));
    }

    private Role parseRole(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        normalized = switch (normalized) {
            case "대기중", "WAIT" -> "ROLE_WAIT";
            case "거절", "반려", "REJECT" -> "ROLE_REJECT";
            case "승인", "회원", "일반회원", "USER" -> "ROLE_USER";
            case "코치", "COACH" -> "ROLE_COACH";
            case "관리자", "ADMIN" -> "ROLE_ADMIN";
            default -> normalized;
        };

        try {
            Role role = Role.valueOf(normalized);
            return isAppsmithManagedRole(role) ? role : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private boolean isAppsmithManagedRole(Role role) {
        return role == Role.ROLE_WAIT
                || role == Role.ROLE_REJECT
                || role == Role.ROLE_USER
                || role == Role.ROLE_COACH
                || role == Role.ROLE_ADMIN;
    }
}
