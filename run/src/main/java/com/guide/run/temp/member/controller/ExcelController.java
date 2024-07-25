package com.guide.run.temp.member.controller;


import com.guide.run.temp.member.dto.*;
import com.guide.run.temp.member.service.ExcelService;
import com.guide.run.temp.member.service.ExcelService2;
import com.guide.run.temp.member.service.MoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@Slf4j
@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
        maxAge = 3600)
@RestController
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;
    private final ExcelService2 excelService2;
    private final MoveService moveService;
    @GetMapping("/healty")
    public ResponseEntity healthCheck(){
        return ResponseEntity.ok().body("");}
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
            List<EventTMPDTO> dataList = excelService.readEventExcelData(file);
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


    @GetMapping("/tmp/excel/attendance")
    public ResponseEntity<String> uploadAttendance2(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            return new ResponseEntity<>("파일을 올려주세요", HttpStatus.BAD_REQUEST);
        }

        try {
            List<AttDTO> dataList = excelService2.readAttData(file);
            if (dataList != null && !dataList.isEmpty()) {
                excelService2.saveAtt(dataList);
                return new ResponseEntity<>("성공", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("파일 오류", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("실패 " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/tmp/excel/partner")
    public ResponseEntity<String> uploadPartner(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            return new ResponseEntity<>("파일을 올려주세요", HttpStatus.BAD_REQUEST);
        }

        try {
            List<MatchingTmpDTO> dataList = excelService2.readMatchingData(file);
            if (dataList != null && !dataList.isEmpty()) {
                excelService2.savePartner(dataList);
                return new ResponseEntity<>("성공", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("파일 오류", HttpStatus.BAD_REQUEST);
            }

        } catch (IOException e) {
            return new ResponseEntity<>("실패 " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/tmp/moveUser")
    public void moveUer(){
        moveService.move();
    }
    @GetMapping("/tmp/missMatch")
    public void missMatch(){
        moveService.misMatch();
    }
    @GetMapping("/tmp/missA")
    public void missA(){
        moveService.misA();
        log.info("");
    }

    @GetMapping("/tmp/null")
    public ResponseEntity<String> saveNull(@RequestParam("file") MultipartFile file){
        try {
            List<MemberDTO2> dataList = excelService2.readNullData(file);
            if (dataList != null && !dataList.isEmpty()) {
                excelService2.saveNull(dataList);
                return new ResponseEntity<>("성공", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("파일 오류", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("실패 " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
