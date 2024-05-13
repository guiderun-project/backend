package com.guide.run.event.entity;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventForm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //신청서 id
    private String privateId; // 신청한 사람 id
    private Long eventId;//신청한 이벤트 id
    private String type; // 신청자 타입 임시로 String으로 해둠
    private int age;
    private String gender;
    private String hopeTeam; //훈련 희망 팀
    private String hopePartner; //희망 파트너 이름
    private String referContent; // 운영진 참고내용
    private boolean isMatching; //대기 매칭 상태
}
