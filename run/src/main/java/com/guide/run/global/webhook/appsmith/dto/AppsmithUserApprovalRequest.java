package com.guide.run.global.webhook.appsmith.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppsmithUserApprovalRequest {
    private String secret;
    private String userId;
    private String role;
    private String recordDegree;
    private String runningGroup;
    private String requestedBy;

    public String getResolvedRecordDegree() {
        if (recordDegree != null && !recordDegree.isBlank()) {
            return recordDegree;
        }
        return runningGroup;
    }
}
