package com.guide.run.user.service;

import com.guide.run.global.exception.auth.authorize.NotValidAccountIdException;
import com.guide.run.global.exception.auth.authorize.NotValidPasswordException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.partner.repository.PartnerLikeRepository;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.temp.member.dto.CntDTO;
import com.guide.run.temp.member.service.TmpService;
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
    private final ViRepository viRepository;
    private final GuideRepository guideRepository;
    private final PartnerLikeRepository partnerLikeRepository;
    private final WithdrawalRepository withdrawalRepository;
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

    //todo : 탈퇴 후 정보 전체 탈퇴한 회원으로 변경
    @Transactional
    public void withDrawal(WithdrawalRequest request, String privateId){
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        archiveDataRepository.deleteById(privateId);
        signUpInfoRepository.deleteById(privateId);
        partnerLikeRepository.deleteById(privateId);

        Withdrawal withdrawal = Withdrawal.builder()
                .userId(user.getUserId())
                .privateId(user.getPrivateId())
                .type(user.getType())
                .name(user.getName())
                .gender(user.getGender())
                .recordDegree(user.getRecordDegree())
                .role(Role.ROLE_DELETE)
                .deleteReasons(request.getReasons())
                .build();
        withdrawalRepository.save(withdrawal);

        if(user.getType().equals(UserType.VI)){
            viRepository.deleteById(privateId);
        }else if(user.getType().equals(UserType.GUIDE)){
            guideRepository.deleteById(privateId);
        }

        user = User.builder()
                .privateId(user.getPrivateId())
                .userId(user.getUserId())
                .name("탈퇴한 회원")
                .recordDegree(null)
                .gender(null)
                .type(null)
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
        userRepository.save(user);

    }



}
