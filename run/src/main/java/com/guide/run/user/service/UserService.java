package com.guide.run.user.service;

import com.guide.run.event.entity.Comment;
import com.guide.run.event.entity.repository.CommentLikeRepository;
import com.guide.run.event.entity.repository.EventCommentRepository;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventLikeRepository;
import com.guide.run.global.exception.auth.authorize.NotValidAccountIdException;
import com.guide.run.global.exception.auth.authorize.NotValidPasswordException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.partner.entity.partner.repository.PartnerLikeRepository;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.user.dto.request.WithdrawalRequest;
import com.guide.run.user.entity.SignUpInfo;
import com.guide.run.user.entity.Withdrawal;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.repository.*;
import com.guide.run.user.repository.user.UserRepository;
import com.guide.run.user.repository.withdrawal.WithdrawalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final SignUpInfoRepository signUpInfoRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final ArchiveDataRepository archiveDataRepository;

    private final PartnerRepository partnerRepository;
    private final ViRepository viRepository;
    private final GuideRepository guideRepository;
    private final PartnerLikeRepository partnerLikeRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final MatchingRepository matchingRepository;
    private final UnMatchingRepository unMatchingRepository;
    private final EventCommentRepository eventCommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final EventLikeRepository eventLikeRepository;
    private final EventFormRepository eventFormRepository;

    private final AttendanceRepository attendanceRepository;


    @Transactional
    public boolean getUserStatus(String privateId){
        User user = userRepository.findById(privateId).orElse(null);

        if(user != null){
            if(user.getPhoneNumber()==null) {
                return false;
            }
            else{
                return true;
            }
        }else{
                //신규 가입자의 경우 인증을 위해 임시 유저 생성
                //가입이 완료되면 새 토큰 다시 줘야함
                userRepository.save(User.builder()
                        .privateId(privateId)
                        .role(Role.ROLE_NEW)
                        .userId(getUUID())
                        .build());
                return false;
        }
    }

    //일반 로그인
    public String generalLogin(String accountId, String password){
        SignUpInfo info = signUpInfoRepository.findByAccountId(accountId).orElseThrow(NotValidAccountIdException::new);


        if(info.checkPassword(password, bCryptPasswordEncoder)){
            return info.getPrivateId();
        }else{
            throw new NotValidPasswordException();
        }
    }

    public String getUUID(){
        String id = UUID.randomUUID().toString();
        return id;
    }


    //userId = privateId로 변경, uuid -> userId 로 변경
    //기가입자 및 이미 정보를 입력한 회원은 재할당 하지 않음
    private String reAssignSocialId(String privateId) {
        if(privateId.startsWith("kakao_")){
            return privateId;
        }else if(privateId.startsWith("kakao")){
            return "kakao_"+privateId.substring(6);
        }
        else{
            return "Error"; //todo : 이 부분 에러코드 추가해야 합니다.
        }
    }

    public String reAssignReturn(String privateId){
        return reAssignSocialId(privateId);
    }


    public String extractNumber(String phoneNum){
        return phoneNum.replaceAll("[^0-9]", "");
    }

    public boolean isAccountIdExist(String accountId) {
        Optional<SignUpInfo> byAccountId = signUpInfoRepository.findByAccountId(accountId);
        return !byAccountId.isEmpty();
    }

    //todo : 탈퇴 후 정보 전체 탈퇴한 회원으로 변경
    @Transactional
    public void withDrawal(WithdrawalRequest request, String privateId){
        //기존 유저 정보 전부 지우거나 newId로 변경해줘야 함.

        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        archiveDataRepository.deleteById(privateId); //아카이브 데이터 - 삭제
        signUpInfoRepository.deleteById(privateId); //회원가입 정보 - 삭제

        String newId = createWithdrawalId();
        Withdrawal check = withdrawalRepository.findById(newId).orElse(null);
        while(check!=null){
            newId = createWithdrawalId();
            check = withdrawalRepository.findById(newId).orElse(null);
        }
        //미매칭 - 삭제
        unMatchingRepository.deleteAllByPrivateId(privateId);

        //댓글 좋아요 - 삭제
        List<Comment> commentList = eventCommentRepository.findAllByPrivateId(privateId);

        for(Comment c : commentList){
            commentLikeRepository.deleteAllByCommentId(c.getCommentId());
        }
        //댓글 - 삭제
        eventCommentRepository.deleteAllByPrivateId(privateId);
        //이벤트 신청서 - 삭제
        eventFormRepository.deleteAllByPrivateId(privateId);
        //출석 - newId로 변경
        List<Attendance> attendanceList = attendanceRepository.findAllByPrivateId(privateId);

        for(Attendance a : attendanceList){
            Attendance attendance = Attendance.builder()
                    .date(a.getDate())
                    .isAttend(a.isAttend())
                    .eventId(a.getEventId())
                    .privateId(newId)
                    .build();

            attendanceRepository.delete(a);
            attendanceRepository.save(attendance);
        }

        //파트너 좋아요(받은 기록) - 삭제
        partnerLikeRepository.deleteAllByRecId(privateId); //파트너 좋아요 받은 기록
        //파트너 좋아요(준 기록) - 삭제
        partnerLikeRepository.deleteAllBySendId(privateId); //파트너 좋아요 준 기록 삭제

        //이벤트 좋아요 - 삭제
        eventLikeRepository.deleteAllByPrivateId(privateId);

        if(user.getType().equals(UserType.VI)){
            viRepository.deleteById(privateId); //vi 정보 - 삭제
            //매칭 정보 - 변경
            List<Matching> matchingList = matchingRepository.findAllByViId(privateId);
            for(Matching m : matchingList){
                Matching matching = Matching.builder()
                        .eventId(m.getEventId())
                        .guideId(m.getGuideId())
                        .viId(newId)
                        .viRecord(m.getViRecord())
                        .guideRecord(m.getGuideRecord())
                        .build();
                matchingRepository.delete(m);
                matchingRepository.save(matching);
            }

            //파트너 정보 변경
            List<Partner> partnerList = partnerRepository.findAllByViId(privateId);

            for(Partner p : partnerList){
                Partner partner = Partner.builder()
                        .contestIds(p.getContestIds())
                        .guideId(p.getGuideId())
                        .trainingIds(p.getTrainingIds())
                        .viId(newId)
                        .build();

                partnerRepository.delete(p);
                partnerRepository.save(partner);
            }
        }else if(user.getType().equals(UserType.GUIDE)){
            guideRepository.deleteById(privateId); //guide 정보 - 삭제

            //매칭 정보 변경
            List<Matching> matchingList = matchingRepository.findAllByGuideId(privateId);
            for (Matching m : matchingList) {
                Matching matching = Matching.builder()
                        .eventId(m.getEventId())
                        .guideId(newId)
                        .viId(m.getViId())
                        .viRecord(m.getViRecord())
                        .guideRecord(m.getGuideRecord())
                        .build();
                matchingRepository.delete(m);
                matchingRepository.save(matching);
            }

            //파트너 정보 변경
            List<Partner> partnerList = partnerRepository.findAllByGuideId(privateId);

            for(Partner p : partnerList){
                Partner partner = Partner.builder()
                        .contestIds(p.getContestIds())
                        .guideId(newId)
                        .trainingIds(p.getTrainingIds())
                        .viId(p.getViId())
                        .build();

                partnerRepository.delete(p);
                partnerRepository.save(partner);
            }
        }

        Withdrawal withdrawal = Withdrawal.builder()
                .userId(user.getUserId())
                .privateId(newId)
                .type(user.getType())
                .name(user.getName())
                .gender(user.getGender())
                .recordDegree(user.getRecordDegree())
                .role(Role.ROLE_DELETE)
                .deleteReasons(request.getReasons())
                .build();
        withdrawalRepository.save(withdrawal);
        String userId= user.getUserId();
        UserType userType =user.getType();
        userRepository.deleteById(user.getPrivateId());
        userRepository.flush();

        User newUser = User.builder()
                .privateId(newId)
                .userId(userId)
                .name("탈퇴한 회원")
                .recordDegree(null)
                .gender(null)
                .type(userType)
                .phoneNumber(null)
                .age(0)
                .isOpenNumber(false)
                .detailRecord(null)
                .recordDegree(null)
                .role(Role.ROLE_DELETE)
                .snsId(null)
                .isOpenSns(false)
                .trainingCnt(0)
                .competitionCnt(0)
                .img(null)
                .build();
        userRepository.save(newUser);

    }

    public String createWithdrawalId() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 15; i++) { // 탈퇴 회원 id 15자리
            key.append((rnd.nextInt(10)));
        }

        return key.toString();
    }


}
