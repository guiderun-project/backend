package com.guide.run.user.service;

import com.guide.run.global.exception.auth.authorize.NotValidAccountIdException;
import com.guide.run.global.exception.auth.authorize.NotValidPasswordException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.temp.member.dto.CntDTO;
import com.guide.run.temp.member.service.TmpService;
import com.guide.run.user.dto.request.WithdrawalRequest;
import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.SignUpInfo;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.repository.*;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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
    private final TmpService tmpService;

    @Transactional
    public boolean getUserStatus(String privateId){
        User user = userRepository.findById(privateId).orElse(null);
        if(user != null){
            if(user.getPhoneNumber()==null) {
                return false;
            }
            else{
                CntDTO cntDTO = tmpService.updateMember(user.getPhoneNumber(), user.getPrivateId());
                user.editUserCnt(cntDTO);
                userRepository.save(user);
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

    //todo : 탈퇴 후 정보 삭제 의견 공유 필요
    @Transactional
    public void withDrawal(WithdrawalRequest request, String privateId){
        //좋아요 기록 삭제
        //가입 정보(아이디 비밀번호) 삭제
        //파트너, 매칭 정보 삭제, 이벤트 생성자일 경우 탈퇴한 회원이라고 표시 필요.
        //댓글 삭제, 출석 기록...어쩌지
        //남겨야 하는 정보 : 이름, 장애여부, 성별, role(탈퇴 상태), 탈퇴 사유, 러닝등급
        //== 테이블을 하나 파서 그것만 남기고 user 정보에는 탈퇴한 회원이라고만 남기는 게 어떨까?

        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        ArchiveData archiveData = archiveDataRepository.findById(privateId).orElseThrow(NotExistUserException::new);

        user = User.builder()
                .phoneNumber(null)
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
        userRepository.save(user);
        archiveData.deleteArchive(request.getReasons());
        archiveDataRepository.save(archiveData);

    }



}
