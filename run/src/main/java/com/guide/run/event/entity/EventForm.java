package com.guide.run.event.entity;

import com.guide.run.event.entity.type.EventCategory;
import com.guide.run.global.entity.BaseEntity;
import com.guide.run.user.entity.type.UserType;
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
public class EventForm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //신청서 id
    private String privateId; // 신청한 사람 id
    private Long eventId;//신청한 이벤트 id
    private UserType type; // 신청자 타입 임시로 String으로 해둠
    private int age;
    private String gender;
    private String hopeTeam; //훈련 희망 팀
    private String hopePartner; //희망 파트너 이름
    private String referContent; // 운영진 참고내용
    private boolean isMatching; //대기 매칭 상태
    @Enumerated(EnumType.STRING)
    private EventCategory eventCategory; // 이벤트 유형, 팀 구분하는 방식 그룹별,팀별,디폴트 분류

    public void setform(String hopeTeam,String hopePartner,String referContent,EventCategory eventCategory) {
        this.hopeTeam = hopeTeam;
        this.hopePartner = hopePartner;
        this.referContent = referContent;
        this.eventCategory = eventCategory;
    }

}
