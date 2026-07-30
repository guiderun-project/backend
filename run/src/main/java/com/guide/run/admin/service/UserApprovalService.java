package com.guide.run.admin.service;

import com.guide.run.admin.dto.response.UserApprovalResult;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.sms.cool.CoolSmsService;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApprovalService {

    private final UserRepository userRepository;
    private final CoolSmsService coolSmsService;

    @Transactional
    public UserApprovalResult processUserApproval(
            String userId,
            Role targetRole,
            String recordDegree,
            String requestedBy
    ) {
        validateRequest(userId, targetRole);

        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        Role previousRole = user.getRole();
        String resolvedRecordDegree = StringUtils.hasText(recordDegree) ? recordDegree : user.getRecordDegree();

        if (previousRole == targetRole && valuesEqual(user.getRecordDegree(), resolvedRecordDegree)) {
            log.info("Skip user approval because user already has requested values. userId={}, role={}, requestedBy={}",
                    userId, targetRole, requestedBy);
            return toResult(user, false);
        }

        user.approveUser(targetRole, resolvedRecordDegree);
        userRepository.save(user);

        if (shouldSendApprovalMessage(previousRole, targetRole)) {
            sendApprovalMessage(user);
        }

        log.info("Processed user approval. userId={}, previousRole={}, targetRole={}, requestedBy={}",
                userId, previousRole, targetRole, requestedBy);
        return toResult(user, true);
    }

    private void validateRequest(String userId, Role targetRole) {
        if (!StringUtils.hasText(userId) || targetRole == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId and role are required.");
        }
    }

    private boolean shouldSendApprovalMessage(Role previousRole, Role targetRole) {
        return previousRole == Role.ROLE_WAIT
                && targetRole != Role.ROLE_WAIT
                && targetRole != Role.ROLE_REJECT;
    }

    private boolean valuesEqual(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }

    private void sendApprovalMessage(User user) {
        if (UserType.GUIDE.equals(user.getType())) {
            coolSmsService.sendToNewUser(user.getPhoneNumber(), user.getName(), "가이드러너", user.getRecordDegree());
        } else if (UserType.VI.equals(user.getType())) {
            coolSmsService.sendToNewUser(user.getPhoneNumber(), user.getName(), "시각장애러너", user.getRecordDegree());
        }
    }

    private UserApprovalResult toResult(User user, boolean changed) {
        return new UserApprovalResult(
                user.getUserId(),
                user.getRole(),
                user.getRecordDegree(),
                changed
        );
    }
}
