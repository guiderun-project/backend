package com.guide.run.temp.member.service;

import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.entity.Member;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.temp.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TmpService {
    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
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
    public void updateMember(String phone, String privateId){
        Long memberId = getMember(phone);
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
                attendanceRepository.delete(att);
                attendanceRepository.save(updateAtt);
            }
        }

    }
}
