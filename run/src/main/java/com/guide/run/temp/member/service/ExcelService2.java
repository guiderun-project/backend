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
import com.guide.run.temp.member.dto.MemberDTO2;
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.entity.Member;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.temp.member.repository.MemberRepository;
import com.guide.run.user.entity.ArchiveData;
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
import com.guide.run.user.service.LoginInfoService;
import com.guide.run.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
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
    private final LoginInfoService loginInfoService;


    public List<AttDTO> readAttData(MultipartFile file) throws IOException {
        List<AttDTO> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() <2) {
                    continue;
                }
                if(row.getRowNum()>=2013){
                    break;
                }

                AttDTO dto = new AttDTO();
                //privateId, eventId, date
                Cell idCell = row.getCell(1);
                if(idCell !=null){
                    dto.setPrivateId(getStringValue(idCell));
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
                if(row.getRowNum()>=1363){
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

    public List<MemberDTO2> readNullData(MultipartFile file) throws IOException {
        List<MemberDTO2> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum()>=26){
                    break;
                }

                MemberDTO2 dto = new MemberDTO2();
                //privateId, eventId, date
                Cell idCell = row.getCell(0);
                if(idCell !=null){
                    dto.setId(idCell.getStringCellValue());
                }

                Cell typeCell = row.getCell(2);
                if(typeCell!=null){
                    dto.setType(typeCell.getStringCellValue().toUpperCase());
                }


                Cell nameCell = row.getCell(1);
                if (nameCell != null) {
                    dto.setName(nameCell.getStringCellValue());
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
                //Member member = memberRepository.findById(Long.parseLong(a.getPrivateId())).orElse(null);
                User user = userRepository.findById(a.getPrivateId()).orElse(null);
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
                    }/*else{
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
                    }*/
                }
        }
    }

    //파트너 정보 반영
    @Transactional
    public void savePartner(List<MatchingTmpDTO> matchingTmpDTOS){
        //member와 user는 이미 저장되어 있음. null유저까지 저장 후 바로 반영해주면 될 듯...
        //member -> user 코드 수정해주고...
        //매칭 확인해서 저장해주기! 파트너 + 매칭에 저장
        //출석에 없으면 출석도 추가.


        for(MatchingTmpDTO dto : matchingTmpDTOS) {

            User vi;
            User guide;
            Event e = eventRepository.findById(dto.getEventId()).orElse(null);

            if (e != null) {
                Member memberVi = null;
                Member memberGuide = null;
                User applyVi = userRepository.findById(dto.getViId()).orElse(null);
                User applyGuide = userRepository.findById(dto.getGuideId()).orElse(null);
                if(dto.getGuideId().equals("null99")){
                        //null99 가이드일 때.
                        User nullGuide = User.builder()
                                .userId(userService.getUUID())
                                .privateId(dto.getGuideId()+loginInfoService.createSmsKey())
                                .gender("MALE")
                                .name("미가입 Guide")
                                .recordDegree("E")
                                .type(UserType.GUIDE)
                                .role(Role.ROLE_USER)
                                .competitionCnt(0)
                                .trainingCnt(0)
                                .build();
                        userRepository.save(nullGuide);

                        ArchiveData archiveData = ArchiveData.builder()
                                .privateId(nullGuide.getPrivateId())
                                .hopePrefs("")
                                .portraitRights(false)
                                .privacy(false)
                                .runningPlace("")
                                .motive("")
                                .build();
                        archiveDataRepository.save(archiveData);


                        Guide guideData = Guide.builder()
                                .guidingPace("")
                                .isGuideExp(false)
                                .viName("")
                                .viCount("")
                                .viRecord("")
                                .privateId(nullGuide.getPrivateId())
                                .build();
                        guideRepository.save(guideData);

                        applyGuide = nullGuide;
                }

                if(applyVi==null) {
                    log.info(dto.getViId());
                    memberVi = memberRepository.findById(Long.valueOf(dto.getViId())).orElse(null);
                    if(memberVi!=null) {
                        applyVi = userRepository.findUserByPhoneNumber(memberVi.getPhoneNumber()).orElse(null);
                    }
                }

                if(applyGuide==null) {
                    log.info(dto.getGuideId());
                    memberGuide = memberRepository.findById(Long.valueOf(dto.getGuideId())).orElse(null);
                    if(memberGuide!=null) {
                        applyGuide = userRepository.findUserByPhoneNumber(memberGuide.getPhoneNumber()).orElse(null);
                    }
                }

                //applyguide랑 applyVi만 고려해서 적용하면 됨..
                if(applyVi!=null &applyGuide!=null){
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
                    }else if(e.getType().equals(EventType.TRAINING)){
                        partner.addTraining(e.getId());
                    }

                    partnerRepository.save(partner);
                }

            }
        }
    }

    //null 회원 저장
    @Transactional
    public void saveNull(List<MemberDTO2> memberDTO2List) {
        for (MemberDTO2 m : memberDTO2List) {
            //guide 저장
            if (m.getType().equals("VI")) {
                //vi 저장
                User nullVi = User.builder()
                        .userId(userService.getUUID())
                        .privateId(m.getId())
                        .name("미가입 VI")
                        .gender("MALE")
                        .recordDegree("E")
                        .type(UserType.VI)
                        .role(Role.ROLE_USER)
                        .competitionCnt(0)
                        .trainingCnt(0)
                        .build();
                userRepository.save(nullVi);

                Vi nullViData = Vi.builder()
                        .privateId(m.getId())
                        .isRunningExp(false)
                        .build();
                viRepository.save(nullViData);

                ArchiveData archiveData = ArchiveData.builder()
                        .privateId(m.getId())
                        .hopePrefs("")
                        .portraitRights(false)
                        .privacy(false)
                        .runningPlace("")
                        .motive("")
                        .build();
                archiveDataRepository.save(archiveData);

            } else if (m.getType().equals("GUIDE")) { //guide 저장
                ArchiveData archiveData = ArchiveData.builder()
                        .privateId(m.getId())
                        .hopePrefs("")
                        .portraitRights(false)
                        .privacy(false)
                        .runningPlace("")
                        .motive("")
                        .build();
                archiveDataRepository.save(archiveData);

                User nullGuide = User.builder()
                        .userId(userService.getUUID())
                        .privateId(m.getId())
                        .gender("MALE")
                        .name("미가입 Guide")
                        .recordDegree("E")
                        .type(UserType.GUIDE)
                        .role(Role.ROLE_USER)
                        .competitionCnt(0)
                        .trainingCnt(0)
                        .build();
                userRepository.save(nullGuide);
                Guide guideData = Guide.builder()
                        .guidingPace("")
                        .isGuideExp(false)
                        .viName("")
                        .viCount("")
                        .viRecord("")
                        .privateId(nullGuide.getPrivateId())
                        .build();
                guideRepository.save(guideData);

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
        return LocalDateTime.parse(cell.getStringCellValue());
    }

}