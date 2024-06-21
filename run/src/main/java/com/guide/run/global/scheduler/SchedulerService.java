package com.guide.run.global.scheduler;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.scheduler.dto.AttendAndPartnerDto;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.temp.member.dto.CntDTO;
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;



@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT59M", defaultLockAtLeastFor = "PT59M")
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;

    //1시간마다 실행
    @Scheduled(cron = "0 0 * * * *")
    @SchedulerLock(name = "eventShedLockJob", lockAtLeastFor = "PT59M", lockAtMostFor = "PT59M")
    public void setEventStatus(){
        //이벤트 불러오기
        List<Event> eventList = eventRepository.getSchedulerEvent();
        LocalDateTime now = LocalDateTime.now();
        for(Event e : eventList){
            if(e.getStartTime().isBefore(now)||e.getStartTime().isEqual(now)){
                //시작시간과 현재 시간 비교해서 시간 같거나 지났으면 진행중/ 이벤트 모집 종료로 변경.
                e.changeRecruit(EventRecruitStatus.RECRUIT_CLOSE);
                e.changeStatus(EventStatus.EVENT_OPEN);
            }
            if(e.getEndTime().isBefore(now)||e.getEndTime().isEqual(now)){
                //종료시간과 현재 시간 비교해서 시간 같거나 지났으면 종료로 변경. + 종료 시 이벤트 모집 상태도 종료로 바꿔줌.
                e.changeStatus(EventStatus.EVENT_END);
                e.changeRecruit(EventRecruitStatus.RECRUIT_END);

            }
            eventRepository.save(e);

            // 출석 반영해서 이벤트 개수 유저에 추가.
            //출석한 사람에 한정해서 끝난 이벤트의 매칭 정보도 파트너로 등록
            List<AttendAndPartnerDto> dtoList = partnerRepository.getEndEventAttendanceAndPartner(e.getId());
            //select 로 dto 하나 파고, where로 출석 유무 체크하고, 파트너 id랑 각 리스트 가져와서 추가하고 저장.
            for(AttendAndPartnerDto dto : dtoList){
                User user = userRepository.findById(dto.getPrivateId()).orElse(null);

                if(e.getType().equals(EventType.TRAINING)){
                    if(user!=null){
                        //이벤트 횟수 반영
                        user.editUserCnt(
                                CntDTO.builder()
                                        .competitionCnt(0)
                                        .trainingCnt(1)
                                        .build()
                        );
                        userRepository.save(user);
                    }

                    //파트너 반영
                    if(dto.getUserType().equals(UserType.VI)){
                        List<Partner> partnerList = partnerRepository.findAllByViId(dto.getPartnerId());
                        for(Partner p : partnerList){
                            p.addTraining(e.getId());
                            partnerRepository.save(p);
                        }
                    }else{
                        Partner partner = partnerRepository.findByGuideId(dto.getPartnerId()).orElse(null);
                        if(partner!=null){
                            partner.addTraining(e.getId());
                            partnerRepository.save(partner);
                        }
                    }

                }else{
                    if(user!=null){
                        //이벤트 횟수 반영
                        user.editUserCnt(
                                CntDTO.builder()
                                        .competitionCnt(1)
                                        .trainingCnt(0)
                                        .build()
                        );
                        userRepository.save(user);
                    }
                    //파트너 반영
                    if(dto.getUserType().equals(UserType.VI)){
                        List<Partner> partnerList = partnerRepository.findAllByViId(dto.getPartnerId());
                        for(Partner p : partnerList){
                            p.addContest(e.getId());
                            partnerRepository.save(p);
                        }
                    }else{
                        Partner partner = partnerRepository.findByGuideId(dto.getPartnerId()).orElse(null);
                        if(partner!=null){
                            partner.addContest(e.getId());
                            partnerRepository.save(partner);
                        }
                    }
                }
            }

            }



    }

    //자정마다 실행
    @Scheduled(cron="0 0 0 * * *")
    @SchedulerLock(name = "recruitShedLockJob", lockAtLeastFor = "PT59M", lockAtMostFor = "PT59M")
    public void setEventRecruitStatus(){
        //모집 종료/이벤트 종료 빼고 이벤트 불러오기.
        List<Event> eventList = eventRepository.getSchedulerRecruit();
        LocalDate today = LocalDate.now();

        for(Event e : eventList){
            if(e.getRecruitStartDate().isBefore(today)||e.getRecruitStartDate().isEqual(today)){
                //모집 시작일과 현재 일자 비교해서 시간 같거나 지났으면 진행중으로 변경.
                e.changeRecruit(EventRecruitStatus.RECRUIT_OPEN);
            }

            if(e.getRecruitEndDate().isBefore(today)){
                //모집 종료일과 현재 일자 비교해서 시간이 지났으면 종료로 변경.
                e.changeRecruit(EventRecruitStatus.RECRUIT_CLOSE);
            }

            eventRepository.save(e);

        }

    }

}
