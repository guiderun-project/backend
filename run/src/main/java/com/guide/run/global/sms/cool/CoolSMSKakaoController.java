package com.guide.run.global.sms.cool;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.model.MessageType;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/kakao")
public class CoolSMSKakaoController {
    @Value("${spring.coolsms.senderNumber}")
    private String senderNumber;

    @Value("${spring.coolsms.apiKey}")
    private String apiKey;

    @Value("${spring.coolsms.apiSecret}")
    private String apiSecret;

    @Value("${spring.coolsms.kakaoChId}")
    private String kakaoChId; //카카오 채널 id
    @Value("${spring.coolsms.signupCompletionMsgId}")
    private String signupCompletionMsgId; //회원가입 완료 메세지 id
    @Value("${spring.coolsms.signupApprovalMsgId}")
    private String signupApprovalMsgId; //회원가입 승인 완료 메세지 id

    private DefaultMessageService messageService;

    @PostConstruct
    private void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    //신규 회원 가입 시 관리자에게 알림
    @PostMapping("/send-one-ata")
    public SingleMessageSentResponse sendATA(Message message) {

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        return response;
    }

    //신규 회원 승인 후 알림
    public void sendToNewUser(String to, String name, String status, String team) {
        Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태
        message.setFrom(senderNumber);
        message.setTo(to);
        message.setType(MessageType.ATA);
        KakaoOption kakaoOption = new KakaoOption();

        //채널 아이디
        kakaoOption.setPfId(kakaoChId);
        //템플릿 아이디
        kakaoOption.setTemplateId(signupApprovalMsgId);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{username_new}", name);
        variables.put("#{disability_status}", status);
        variables.put("#{assigned_team}", team);

        kakaoOption.setVariables(variables);

        message.setKakaoOptions(kakaoOption);

        sendATA(message);
    }

    public void sendToAdmin(String to, String status, String name) {
        Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태
        message.setFrom(senderNumber);
        message.setTo(to);
        message.setType(MessageType.ATA);

        KakaoOption kakaoOption = new KakaoOption();

        //채널 아이디
        kakaoOption.setPfId(kakaoChId);
        //템플릿 아이디
        kakaoOption.setTemplateId(signupCompletionMsgId);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{disability_status}", status);
        variables.put("#{username_new}", name);
        kakaoOption.setVariables(variables);

        message.setKakaoOptions(kakaoOption);

        sendATA(message);

    }
}
