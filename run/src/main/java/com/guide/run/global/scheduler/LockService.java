package com.guide.run.global.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LockService {
    private final JdbcTemplate jdbcTemplate;
    //lock 등록
    public boolean acquireLock(String lockName, LocalDateTime lockUntil) {
        try {
            int updatedRows = jdbcTemplate.update(
                    "INSERT INTO scheduler_lock (lock_name, lock_until) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE lock_until = ?",
                    lockName, lockUntil, lockUntil
            );
            return updatedRows > 0;
        } catch (Exception e) {
            return false;
        }
    }

    //lock 삭제
    public void releaseLock(String lockName) {
        jdbcTemplate.update("DELETE FROM scheduler_lock WHERE lock_name = ?", lockName);
    }

    //lock 확인
    public boolean isLockActive(String lockName) {
        String sql = "SELECT lock_until FROM scheduler_lock WHERE lock_name = ?";
        List<LocalDateTime> results = jdbcTemplate.query(sql, new Object[]{lockName}, (rs, rowNum) -> rs.getTimestamp("lock_until").toLocalDateTime());

        if (results.isEmpty()) {
            return false;
        }

        LocalDateTime lockUntil = results.get(0);
        return lockUntil.isAfter(LocalDateTime.now());
    }

}
