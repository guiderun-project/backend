package com.guide.run.event.entity;

import com.guide.run.event.entity.type.EventCategory;
import com.guide.run.global.entity.BaseEntity;
import com.guide.run.user.entity.type.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(indexes = {
    @Index(name = "idx_event_form_event_id", columnList = "eventId"),
    @Index(name = "idx_event_form_private_id", columnList = "privateId"),
    @Index(name = "idx_event_form_event_private", columnList = "eventId, privateId")
})
@Schema(description = "이벤트 신청 폼 엔티티")
public class EventForm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "신청서 ID", example = "45")
    private Long id; //신청서 id
    @Schema(description = "신청자 개인 ID", example = "user_123")
    private String privateId; // 신청한 사람 id
    @Schema(description = "신청 대상 이벤트 ID", example = "1023")
    private Long eventId;//신청한 이벤트 id
    @Schema(description = "신청자 타입", example = "VI")
    private UserType type; // 신청자 타입 임시로 String으로 해둠
    @Schema(description = "신청자 나이", example = "28")
    private int age;
    @Schema(description = "신청자 성별", example = "M")
    private String gender;
    @Schema(description = "희망 훈련 팀", example = "A팀")
    private String hopeTeam; //훈련 희망 팀
    @Schema(description = "희망 파트너 이름", example = "김가이드")
    private String hopePartner; //희망 파트너 이름
    @Schema(description = "운영진 참고 내용", example = "부상 이력은 없습니다")
    private String referContent; // 운영진 참고내용
    @Schema(description = "매칭 대기 상태", example = "false")
    private boolean isMatching; //대기 매칭 상태
    @Enumerated(EnumType.STRING)
    @Schema(description = "이벤트 카테고리", example = "GROUP")
    private EventCategory eventCategory; // 이벤트 유형, 팀 구분하는 방식 그룹별,팀별,디폴트 분류

    public void setform(String hopeTeam,String hopePartner,String referContent,EventCategory eventCategory) {
        this.hopeTeam = hopeTeam;
        this.hopePartner = hopePartner;
        this.referContent = referContent;
        this.eventCategory = eventCategory;
    }

}
