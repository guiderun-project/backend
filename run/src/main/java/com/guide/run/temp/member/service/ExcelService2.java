package com.guide.run.temp.member.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.UnMatching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.temp.member.dto.AttDTO;
import com.guide.run.temp.member.dto.MatchingTmpDTO;
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.entity.Member;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.temp.member.repository.MemberRepository;
import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.SignUpInfo;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.Guide;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
import com.guide.run.user.repository.ArchiveDataRepository;
import com.guide.run.user.repository.GuideRepository;
import com.guide.run.user.repository.SignUpInfoRepository;
import com.guide.run.user.repository.ViRepository;
import com.guide.run.user.repository.user.UserRepository;
import com.guide.run.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExcelService2 {
    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final ArchiveDataRepository archiveDataRepository;
    private final SignUpInfoRepository signUpInfoRepository;
    private final ViRepository viRepository;
    private final GuideRepository guideRepository;
    private final MatchingRepository matchingRepository;
    private final UnMatchingRepository unMatchingRepository;
    private final EventFormRepository eventFormRepository;
    private final UserService userService;


    public List<AttDTO> readAttData(MultipartFile file) throws IOException {
        List<AttDTO> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() <1971) {
                    continue;
                }
                if(row.getRowNum()>=2015){
                    break;
                }

                AttDTO dto = new AttDTO();
                //privateId, eventId, date
                Cell idCell = row.getCell(1);
                if(idCell !=null){
                    dto.setPrivateId(getLongValue(idCell));
                }

                Cell eventIdCell = row.getCell(2);
                if (eventIdCell != null) {
                    dto.setEventId(getLongValue(eventIdCell));
                }


                Cell dateCell = row.getCell(3);
                if (dateCell != null) {
                    dto.setDate(getDateTime(dateCell));

                }

                dataList.add(dto);
            }
        }

        return dataList;
    }

    public List<MatchingTmpDTO> readMatchingData(MultipartFile file) throws IOException {
        List<MatchingTmpDTO> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() <2) {
                    continue;
                }
                if(row.getRowNum()>=1348){
                    break;
                }

                MatchingTmpDTO dto = new MatchingTmpDTO();
                //eventId, guideId, viId, guideRecord, viRecord
                Cell eventIdCell = row.getCell(1);
                if(eventIdCell !=null){
                    dto.setEventId(getLongValue(eventIdCell));
                }

                Cell guideIdCell = row.getCell(2);
                if (guideIdCell != null) {
                    dto.setGuideId(getStringValue(guideIdCell));
                }

                Cell viIdCell = row.getCell(4);
                if (viIdCell != null) {
                    dto.setViId(getStringValue(viIdCell));
                }

                Cell guideRecord = row.getCell(3);
                if (guideRecord != null) {
                    dto.setGuideRecord(guideRecord.getStringCellValue());
                }

                Cell viRecord = row.getCell(5);
                if(viRecord !=null){
                    dto.setViRecord(viRecord.getStringCellValue());
                }

                dataList.add(dto);
            }
        }

        return dataList;
    }

    //출석 반영
    @Transactional
    public void saveAtt(List<AttDTO> attDTOS){
        for(AttDTO a : attDTOS) {
            Event event = eventRepository.findById(a.getEventId()).orElse(null);
            if (event != null) {
                Member member = memberRepository.findById(a.getPrivateId()).orElse(null);
                if (member != null) {
                    String phone = userService.extractNumber(member.getPhoneNumber());
                    User user = userRepository.findUserByPhoneNumber(phone).orElse(null);
                    if (user != null) {
                        Attendance attendance = Attendance.builder()
                                .eventId(a.getEventId())
                                .isAttend(true)
                                .privateId(user.getPrivateId())
                                .build();

                        EventForm eventForm = EventForm.builder()
                                .eventId(event.getId())
                                .isMatching(false)
                                .hopeTeam(user.getRecordDegree())
                                .gender(user.getGender())
                                .age(user.getAge())
                                .type(user.getType())
                                .privateId(user.getPrivateId())
                                .hopePartner("")
                                .referContent("")
                                .build();
                        attendanceRepository.save(attendance);
                        eventFormRepository.save(eventForm);

                        if(event.getType().equals(EventType.COMPETITION)){
                            user.addContestCnt(1);
                            event.setViCnt(1);
                        }else{
                            user.addTrainingCnt(1);
                            event.setGuideCnt(1);
                        }
                        eventRepository.save(event);
                        userRepository.save(user);
                    }else{
                        Attendance attendance = Attendance.builder()
                                .eventId(a.getEventId())
                                .isAttend(true)
                                .privateId(String.valueOf(member.getId()))
                                .build();
                        EventForm eventForm = EventForm.builder()
                                .eventId(event.getId())
                                .isMatching(false)
                                .hopeTeam(member.getRecordDegree())
                                .gender(member.getGender())
                                .age(member.getAge())
                                .type(UserType.valueOf(member.getType()))
                                .privateId(String.valueOf(member.getId()))
                                .hopePartner("")
                                .referContent("")
                                .build();

                        attendanceRepository.save(attendance);
                        eventFormRepository.save(eventForm);

                        if(event.getType().equals(EventType.COMPETITION)){
                            event.setViCnt(1);
                        }else{
                            event.setGuideCnt(1);
                        }
                        eventRepository.save(event);
                    }
                }
            }
        }
    }

    //파트너 정보 반영
    @Transactional
    public void savePartner(List<MatchingTmpDTO> matchingTmpDTOS){
        for(MatchingTmpDTO dto : matchingTmpDTOS) {

            User vi;
            User guide;
            Event e = eventRepository.findById(dto.getEventId()).orElse(null);

            if (e != null) {
                Member memberVi = memberRepository.findById(Long.valueOf(dto.getViId())).orElse(null);
                Member memberGuide = memberRepository.findById(Long.valueOf(dto.getGuideId())).orElse(null);
                User applyVi = userRepository.findUserByPhoneNumber(memberVi.getPhoneNumber()).orElse(null);
                User applyGuide = userRepository.findUserByPhoneNumber(memberGuide.getPhoneNumber()).orElse(null);

                //둘 다 미가입
                if(memberVi !=null && memberGuide !=null && applyVi==null && applyGuide==null) {
                    Matching matching = Matching.builder()
                            .eventId(dto.getEventId())
                            .viId(String.valueOf(memberVi.getId()))
                            .viRecord(dto.getViRecord())
                            .guideRecord(dto.getGuideRecord())
                            .guideId(String.valueOf(memberGuide.getId()))
                            .build();
                    matchingRepository.save(matching);

                    Partner partner = partnerRepository.findByViIdAndGuideId(String.valueOf(memberVi.getId()), String.valueOf(memberGuide.getId())).orElse(null);

                    //파트너 신규 생성 시
                    if (partner == null) {
                        partner = Partner.builder()
                                .viId(String.valueOf(memberVi.getId()))
                                .guideId(String.valueOf(memberGuide.getId()))
                                .trainingIds(new ArrayList<>())
                                .contestIds(new ArrayList<>())
                                .build();
                    }

                    if (e.getType().equals(EventType.COMPETITION)){
                        partner.addContest(e.getId());
                    }else{
                        partner.addTraining(e.getId());
                    }

                    partnerRepository.save(partner);


                }
                //둘 다 가입
                else if(applyVi!=null && applyGuide!=null){
                    Matching matching = Matching.builder()
                            .eventId(dto.getEventId())
                            .viId(applyVi.getPrivateId())
                            .viRecord(dto.getViRecord())
                            .guideRecord(dto.getGuideRecord())
                            .guideId(applyGuide.getPrivateId())
                            .build();
                    matchingRepository.save(matching);

                    Partner partner = partnerRepository.findByViIdAndGuideId(applyVi.getPrivateId(), applyGuide.getPrivateId()).orElse(null);

                    //파트너 신규 생성 시
                    if (partner == null) {
                        partner = Partner.builder()
                                .viId(applyVi.getPrivateId())
                                .guideId(applyGuide.getPrivateId())
                                .trainingIds(new ArrayList<>())
                                .contestIds(new ArrayList<>())
                                .build();
                    }

                    if (e.getType().equals(EventType.COMPETITION)){
                        partner.addContest(e.getId());
                    }else{
                        partner.addTraining(e.getId());
                    }

                    partnerRepository.save(partner);
                }

                //vi는 가입, guide는 미가입
                else if(applyVi!=null && memberGuide!=null){

                    Matching matching = Matching.builder()
                            .eventId(dto.getEventId())
                            .viId(applyVi.getPrivateId())
                            .viRecord(dto.getViRecord())
                            .guideRecord(dto.getGuideRecord())
                            .guideId(String.valueOf(memberGuide.getId()))
                            .build();
                    matchingRepository.save(matching);

                    Partner partner = partnerRepository.findByViIdAndGuideId(applyVi.getPrivateId(), String.valueOf(memberGuide.getId())).orElse(null);

                    //파트너 신규 생성 시
                    if (partner == null) {
                        partner = Partner.builder()
                                .viId(applyVi.getPrivateId())
                                .guideId(String.valueOf(memberGuide.getId()))
                                .trainingIds(new ArrayList<>())
                                .contestIds(new ArrayList<>())
                                .build();
                    }

                    if (e.getType().equals(EventType.COMPETITION)){
                        partner.addContest(e.getId());
                    }else{
                        partner.addTraining(e.getId());
                    }

                    partnerRepository.save(partner);
                }

                //vi는 미가입, guide는 가입
                else if(memberVi!=null&& applyGuide!=null) {

                    Matching matching = Matching.builder()
                            .eventId(dto.getEventId())
                            .viId(String.valueOf(memberVi.getId()))
                            .viRecord(dto.getViRecord())
                            .guideRecord(dto.getGuideRecord())
                            .guideId(applyGuide.getPrivateId())
                            .build();
                    matchingRepository.save(matching);

                    Partner partner = partnerRepository.findByViIdAndGuideId(String.valueOf(memberVi.getId()), applyGuide.getPrivateId()).orElse(null);

                    //파트너 신규 생성 시
                    if (partner == null) {
                        partner = Partner.builder()
                                .viId(String.valueOf(memberVi.getId()))
                                .guideId(applyGuide.getPrivateId())
                                .trainingIds(new ArrayList<>())
                                .contestIds(new ArrayList<>())
                                .build();
                    }

                    if (e.getType().equals(EventType.COMPETITION)){
                        partner.addContest(e.getId());
                    }else{
                        partner.addTraining(e.getId());
                    }

                    partnerRepository.save(partner);
                }

                //vi가 null
                else if(dto.getViId()=="null") {

                    if(applyGuide!=null) {
                        UnMatching unMatching = UnMatching.builder()
                                .eventId(dto.getEventId())
                                .privateId(applyGuide.getPrivateId())
                                .build();

                        unMatchingRepository.save(unMatching);

                    }else if (memberGuide!=null) {
                        UnMatching unMatching = UnMatching.builder()
                                .eventId(dto.getEventId())
                                .privateId(String.valueOf(memberGuide.getId()))
                                .build();

                        unMatchingRepository.save(unMatching);
                    }

                    //guide가 null
                }else if(dto.getGuideId()=="null") {
                    if(applyVi!=null) {
                        UnMatching unMatching = UnMatching.builder()
                                .eventId(dto.getEventId())
                                .privateId(applyVi.getPrivateId())
                                .build();

                        unMatchingRepository.save(unMatching);

                    }else if (memberVi!=null) {
                        UnMatching unMatching = UnMatching.builder()
                                .eventId(dto.getEventId())
                                .privateId(String.valueOf(memberVi.getId()))
                                .build();

                        unMatchingRepository.save(unMatching);
                    }
                }




            }
        }
    }


    private long getLongValue(Cell cell) {
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        }
        return 0;
    }

    private String getStringValue(Cell cell) {
        if (cell != null) {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                return String.valueOf((int)cell.getNumericCellValue());
            }
        }
        return null;
    }

    private LocalDateTime getDateTime(Cell cell){
        //log.info(cell.getStringCellValue());
        return cell.getLocalDateTimeCellValue();
    }

}
