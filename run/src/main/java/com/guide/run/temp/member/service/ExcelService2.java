package com.guide.run.temp.member.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
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
                    dto.setGuideId(getLongValue(guideIdCell));
                }

                Cell viIdCell = row.getCell(4);
                if (viIdCell != null) {
                    dto.setViId(getLongValue(viIdCell));
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
        for(AttDTO a : attDTOS){
            Member member = memberRepository.findById(a.getPrivateId()).orElse(null);
            if(member!=null){
                String phone = userService.extractNumber(member.getPhoneNumber());
                User user = userRepository.findUserByPhoneNumber(phone).orElse(null);
                if(user!=null){
                    Attendance attendance = Attendance.builder()
                            .eventId(a.getEventId())
                            .isAttend(true)
                            .privateId(user.getPrivateId())
                            .build();
                    attendanceRepository.save(attendance);
                }
            }
        }
    }

    //파트너 정보 반영
    @Transactional
    public void savePartner(List<MatchingTmpDTO> matchingTmpDTOS){
        //임시 vi, 임시 guide 생성하기.
        User tmpGuide = createViAndGuide(UserType.GUIDE);
        User tmpVi = createViAndGuide(UserType.VI);

        User viUser = null;
        User guideUser = null;
        for(MatchingTmpDTO dto : matchingTmpDTOS) {

            Event e = eventRepository.findById(dto.getEventId()).orElse(null);

            if (e != null) {
                Member vi = memberRepository.findById(dto.getViId()).orElse(null);
                if (vi != null) {
                    String viPhone = userService.extractNumber(vi.getPhoneNumber());
                    viUser = userRepository.findUserByPhoneNumber(viPhone).orElse(null);
                }

                Member guide = memberRepository.findById(dto.getGuideId()).orElse(null);
                if (guide != null) {
                    String guidePhone = userService.extractNumber(guide.getPhoneNumber());
                    guideUser = userRepository.findUserByPhoneNumber(guidePhone).orElse(null);
                }

                if ((vi == null || viUser == null) && (guide == null || guideUser == null)) {
                    continue;
                } else if (vi == null || viUser == null) {
                    Matching matching = Matching.builder()
                            .eventId(dto.getEventId())
                            .viId(tmpVi.getPrivateId())
                            .viRecord(tmpVi.getRecordDegree())
                            .guideRecord(dto.getGuideRecord())
                            .guideId(guideUser.getPrivateId())
                            .build();
                    matchingRepository.save(matching);

                    Partner partner = partnerRepository.findByViIdAndGuideId(tmpVi.getPrivateId(), guideUser.getPrivateId()).orElse(null);

                    //파트너 신규 생성 시
                    if (partner == null) {
                        partner = Partner.builder()
                                .viId(tmpVi.getPrivateId())
                                .guideId(guideUser.getPrivateId())
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

                } else if (guide == null || guideUser == null) {
                    Matching matching = Matching.builder()
                            .eventId(dto.getEventId())
                            .viId(viUser.getPrivateId())
                            .viRecord(dto.getViRecord())
                            .guideId(tmpGuide.getPrivateId())
                            .guideRecord(tmpGuide.getRecordDegree())
                            .build();
                    matchingRepository.save(matching);

                    Partner partner = partnerRepository.findByViIdAndGuideId(viUser.getPrivateId(), tmpGuide.getPrivateId()).orElse(null);

                    if (partner == null) {
                        partner = Partner.builder()
                                .viId(tmpVi.getPrivateId())
                                .guideId(guideUser.getPrivateId())
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

                } else {
                    Matching matching = Matching.builder()
                            .eventId(dto.getEventId())
                            .viId(viUser.getPrivateId())
                            .viRecord(dto.getViRecord())
                            .guideId(guideUser.getPrivateId())
                            .guideRecord(dto.getGuideRecord())
                            .build();
                    matchingRepository.save(matching);

                    Partner partner = partnerRepository.findByViIdAndGuideId(viUser.getPrivateId(), guideUser.getPrivateId()).orElse(null);

                    if (partner == null) {
                        partner = Partner.builder()
                                .viId(viUser.getPrivateId())
                                .guideId(guideUser.getPrivateId())
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
                return String.valueOf(cell.getNumericCellValue());
            }
        }
        return null;
    }

    private LocalDateTime getDateTime(Cell cell){
        //log.info(cell.getStringCellValue());
        return LocalDateTime.parse(cell.getStringCellValue());
    }

    private User createViAndGuide(UserType type){
        String viId = "tmpVi1234";
        String guideId = "tmpGuide1234";
        if(type.equals(UserType.VI)){

            User user = User.builder()
                    .userId(userService.getUUID())
                    .privateId(viId)
                    .name("미가입 VI")
                    .phoneNumber("01022222222")
                    .recordDegree("E")
                    .type(UserType.VI)
                    .role(Role.ROLE_USER)
                    .competitionCnt(0)
                    .trainingCnt(0)
                    .build();
            userRepository.save(user);
            ArchiveData archiveData = ArchiveData.builder()
                    .privateId(viId)
                    .hopePrefs("")
                    .portraitRights(false)
                    .privacy(false)
                    .runningPlace("")
                    .motive("")
                    .build();
            archiveDataRepository.save(archiveData);
            SignUpInfo signUpInfo = SignUpInfo.builder()
                    .password("password")
                    .accountId("tempVi1234")
                    .privateId(viId)
                    .build();
            signUpInfoRepository.save(signUpInfo);
            Vi vi = Vi.builder()
                    .guideName("")
                    .privateId(viId)
                    .isRunningExp(false)
                    .build();
            viRepository.save(vi);

            return user;

        }else{
            User user = User.builder()
                    .userId(userService.getUUID())
                    .privateId(guideId)
                    .name("미가입 GUIDE")
                    .phoneNumber("01011111111")
                    .type(UserType.GUIDE)
                    .recordDegree("E")
                    .role(Role.ROLE_USER)
                    .competitionCnt(0)
                    .trainingCnt(0)
                    .build();
            userRepository.save(user);

            ArchiveData archiveData = ArchiveData.builder()
                    .privateId(guideId)
                    .hopePrefs("")
                    .portraitRights(false)
                    .privacy(false)
                    .runningPlace("")
                    .motive("")
                    .build();
            archiveDataRepository.save(archiveData);

            SignUpInfo signUpInfo = SignUpInfo.builder()
                    .password("password")
                    .accountId("tempGuide1234")
                    .privateId(guideId)
                    .build();
            signUpInfoRepository.save(signUpInfo);

            Guide guide = Guide.builder()
                    .guidingPace("")
                    .isGuideExp(false)
                    .viName("")
                    .viCount("")
                    .viRecord("")
                    .privateId(guideId)
                    .build();
            guideRepository.save(guide);

            return user;
        }
    }

}
