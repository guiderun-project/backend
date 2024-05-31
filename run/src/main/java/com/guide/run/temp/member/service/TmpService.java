package com.guide.run.temp.member.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.temp.member.dto.CntDTO;
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.entity.Member;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.temp.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TmpService {
    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;
    private final EventFormRepository eventFormRepository;

    //기존에 있던 사람인지 확인
    public Long getMember(String phone){
        Optional<Member> member = memberRepository.findByPhoneNumber(phone);
        if(member.isEmpty()){
            return 0L;
        }else{
            return member.get().getId();
        }
    }
    @Transactional
    //이벤트 출석 정보 연결
    public CntDTO updateMember(String phone, String privateId){
        Long memberId = getMember(phone);
        int trainingCnt = 0; //참여한 훈련 수
        int competitionCnt = 0; //참여한 대회 수
        //임시 출석 정보
        if(memberId != 0L){
            List<Attendance> attendances = attendanceRepository.findAllByPrivateId(String.valueOf(memberId));
            for(Attendance att : attendances){
                Attendance updateAtt = Attendance.builder()
                        .privateId(privateId)
                        .eventId(att.getEventId())
                        .isAttend(true)
                        .date(att.getDate())
                        .build();
                Event event = eventRepository.findById(att.getEventId()).orElseThrow();

                if(event.getType().equals(EventType.TRAINING)){
                    trainingCnt+=1;
                }else if(event.getType().equals(EventType.COMPETITION)) {
                    competitionCnt += 1;
                }
                EventForm eventForm = EventForm.builder()
                        .eventId(att.getEventId())
                        .privateId(privateId)
                        .isMatching(true)
                        .build();
                attendanceRepository.delete(att);
                attendanceRepository.flush();
                attendanceRepository.save(updateAtt);
                eventFormRepository.save(eventForm);
            }
        }

        CntDTO response = CntDTO.builder()
                .competitionCnt(competitionCnt)
                .trainingCnt(trainingCnt)
                .build();
        return response;

    }

    //파트너 정보 연결
    //임시 파트너 db 만들고..거기서 조회하는 프로그램 개발 필요.
    //각 아이디를 받을거고..함께 뛴 횟수 받아야 하고...어디서 같이 뛰었는지도 알아야 하고..
}
