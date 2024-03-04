package com.guide.run.temp.member.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.temp.member.dto.AttDTO;
import com.guide.run.temp.member.dto.EventDTO;
import com.guide.run.temp.member.dto.MemberDTO;
import com.guide.run.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelService {

    private final EventRepository eventRepository;

    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;

    public List<MemberDTO> readMemberExcelData(MultipartFile file) throws IOException {
        List<MemberDTO> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() <2) {
                    continue;
                }
                if(row.getRowNum()>=448){
                    break;
                }

                MemberDTO dto = new MemberDTO();
                //id, name, type, gender, phoneNumber, age, datailRecord, recordDegree
                Cell idCell = row.getCell(1);
                if(idCell !=null){
                    dto.setId(getLongValue(idCell));
                }

                Cell nameCell = row.getCell(2);
                if (nameCell != null) {
                    dto.setName(getStringValue(nameCell));
                }

                Cell typeCell = row.getCell(3);
                if (typeCell != null) {
                    dto.setType(getStringValue(typeCell));
                }

                Cell genderCell = row.getCell(4);
                if (genderCell != null) {
                    dto.setGender(getStringValue(genderCell));
                }

                Cell phoneNumberCell = row.getCell(5);
                if (phoneNumberCell != null) {
                    dto.setPhoneNumber(
                            userService.extractNumber(getStringValue(phoneNumberCell))
                    );
                }

                Cell ageCell = row.getCell(6);
                if (ageCell != null) {
                    dto.setAge(Integer.parseInt(ageCell.getStringCellValue()));
                }

                Cell detailRecordCell = row.getCell(7);
                if (detailRecordCell != null) {
                    dto.setDetailRecord(getStringValue(detailRecordCell));
                }

                Cell recordDegreeCell = row.getCell(8);
                if (recordDegreeCell != null) {
                    dto.setRecordDegree(getStringValue(recordDegreeCell));
                }

                dataList.add(dto);
            }
        }

        return dataList;
    }

    public void saveMemberExcelData(List<MemberDTO> dataList) {
        final int batchSize = 500;

        for (int i = 0; i < dataList.size(); i += batchSize) {
            final List<MemberDTO> batchList = dataList.subList(i, Math.min(i + batchSize, dataList.size()));
            jdbcTemplate.batchUpdate("INSERT INTO member (id, name, type, gender, phone_number, age, detail_record, record_degree) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                            MemberDTO dto = batchList.get(i);
                            preparedStatement.setLong(1, dto.getId());
                            preparedStatement.setString(2,dto.getName());
                            preparedStatement.setString(3,dto.getType());
                            preparedStatement.setString(4,dto.getGender());
                            preparedStatement.setString(5,dto.getPhoneNumber());
                            preparedStatement.setInt(6, dto.getAge());
                            preparedStatement.setString(7, dto.getDetailRecord());
                            preparedStatement.setString(8,dto.getRecordDegree());
                        }

                        @Override
                        public int getBatchSize() {
                            return batchList.size();
                        }
                    });
        }
    }

    public List<EventDTO> readEventExcelData(MultipartFile file) throws IOException {
        List<EventDTO> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() <2) {
                    continue;
                }
                if(row.getRowNum()>=46){
                    break;
                }
                //log.info(String.valueOf(row.getRowNum()));
                    EventDTO dto = new EventDTO();
                    //id, name, type, startTime, endTime, viCnt, guideCnt, place, content
                    Cell idCell = row.getCell(1);
                    if (idCell != null) {
                        dto.setId(getLongValue(idCell));
                    }

                    Cell nameCell = row.getCell(2);
                    if (nameCell != null) {
                        dto.setName(getStringValue(nameCell));
                    }

                    Cell typeCell = row.getCell(3);
                    if (typeCell != null) {
                        dto.setType(getEventType(typeCell));
                    }

                    Cell startTimeCell = row.getCell(5);
                    if (startTimeCell != null) {
                        dto.setStartTime(getDateTime(startTimeCell));
                    }

                    Cell endTimeCell = row.getCell(6);
                    if (endTimeCell != null) {
                        dto.setEndTime(getDateTime(endTimeCell));
                    }

                    Cell viCntCell = row.getCell(7);
                    if (viCntCell != null) {
                        dto.setViCnt(Integer.parseInt(viCntCell.getStringCellValue()));
                    }

                    Cell guideCntCell = row.getCell(8);
                    if (guideCntCell != null) {
                        dto.setGuideCnt(Integer.parseInt(guideCntCell.getStringCellValue()));
                    }

                    Cell placeCell = row.getCell(9);
                    if (placeCell != null) {
                        dto.setPlace(getStringValue(placeCell));
                    }

                    Cell contentCell = row.getCell(10);
                    if (contentCell != null) {
                        dto.setContent(getStringValue(contentCell));
                    }

                    dataList.add(dto);
            }
        }

        return dataList;
    }


    public void saveEventExcelData(List<EventDTO> dataList) {
        EventRecruitStatus recruitStatus = EventRecruitStatus.END;
        for (EventDTO data : dataList) {
            if(data.getId()>=44){
                recruitStatus = EventRecruitStatus.UPCOMING;
            }
            Event event = Event.builder()
                    .id(data.getId())
                    .organizer(data.getOrganizer())
                    .name(data.getName())
                    .recruitStatus(recruitStatus)
                    .isApprove(data.isApprove())
                    .startTime(data.getStartTime())
                    .endTime(data.getEndTime())
                    .place(data.getPlace())
                    .viCnt(data.getViCnt())
                    .guideCnt(data.getGuideCnt())
                    .type(data.getType())
                    .maxNumG(30)
                    .maxNumV(30)
                    .content(data.getContent())
                    .build();

            eventRepository.save(event);
        }
    }

    public List<AttDTO> readAttData(MultipartFile file) throws IOException {
        List<AttDTO> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() <2) {
                    continue;
                }
                if(row.getRowNum()>=1971){
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
                    if(row.getRowNum()>=39){
                        dto.setDate(getDateTime(dateCell));
                    }else{
                        dto.setDate(getDateTime(dateCell));
                    }

                }

                dataList.add(dto);
            }
        }

        return dataList;
    }

    public void saveAttData(List<AttDTO> dataList) {
        final int batchSize = 500;

        for (int i = 0; i < dataList.size(); i += batchSize) {
            final List<AttDTO> batchList = dataList.subList(i, Math.min(i + batchSize, dataList.size()));
            jdbcTemplate.batchUpdate("INSERT INTO attendance (private_id, event_id, is_attend, date) " +
                            "VALUES (?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                            AttDTO dto = batchList.get(i);
                            preparedStatement.setString(1, String.valueOf(dto.getPrivateId()));
                            preparedStatement.setLong(2,dto.getEventId());
                            preparedStatement.setBoolean(3,true);
                            preparedStatement.setObject(4, dto.getDate());
                        }

                        @Override
                        public int getBatchSize() {
                            return batchList.size();
                        }
                    });
        }
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

    private long getLongValue(Cell cell) {
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        }
        return 0;
    }

    private LocalDateTime getDateTime(Cell cell){
        //log.info(cell.getStringCellValue());
        return LocalDateTime.parse(cell.getStringCellValue());
    }
    private EventType getEventType(Cell cell){
        if(cell.getStringCellValue().equals("TRAINING")){
            return EventType.TRAINING;
        }else{
            return EventType.COMPETITION;
        }
    }
}
