package com.guide.run.temp.member.controller;


import com.guide.run.temp.member.dto.AttDTO;
import com.guide.run.temp.member.dto.EventDTO;
import com.guide.run.temp.member.dto.MemberDTO;
import com.guide.run.temp.member.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/member-upload")
    public ResponseEntity<String> uploadMemberExcelFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("파일을 올려주세요", HttpStatus.BAD_REQUEST);
        }

        try {
            List<MemberDTO> dataList = excelService.readMemberExcelData(file);
            if (dataList != null && !dataList.isEmpty()) {
                excelService.saveMemberExcelData(dataList);
                return new ResponseEntity<>("성공", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("파일 오류", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("실패 " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/event-upload")
    public ResponseEntity<String> uploadEventExcelFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("파일을 올려주세요", HttpStatus.BAD_REQUEST);
        }

        try {
            List<EventDTO> dataList = excelService.readEventExcelData(file);
            if (dataList != null && !dataList.isEmpty()) {
                excelService.saveEventExcelData(dataList);
                return new ResponseEntity<>("성공", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("파일 오류", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("실패 " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/attendance-upload")
    public ResponseEntity<String> uploadAttExcelFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("파일을 올려주세요", HttpStatus.BAD_REQUEST);
        }

        try {
            List<AttDTO> dataList = excelService.readAttData(file);
            if (dataList != null && !dataList.isEmpty()) {
                excelService.saveAttData(dataList);
                return new ResponseEntity<>("성공", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("파일 오류", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("실패 " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
