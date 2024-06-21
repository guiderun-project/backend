package com.guide.run.partner.service;

import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.partner.PartnerLike;
import com.guide.run.partner.entity.partner.repository.PartnerLikeRepository;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final PartnerLikeRepository partnerLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void partnerLike(String userId, String privateId){
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        User partner = userRepository.findUserByUserId(userId).orElseThrow(RuntimeException::new); //todo : 존재하지 않는 파트너 에러 추가 필요
        PartnerLike partnerLike = partnerLikeRepository.findByRecIdAndSendId(partner.getPrivateId(),privateId).orElse(null);

        if(partnerLike!=null){ //이미 있으면 취소
            partnerLikeRepository.delete(partnerLike);
        }else{//없을 시 생성
            partnerLikeRepository.save(PartnerLike.builder()
                    .recId(partner.getPrivateId())
                    .sendId(privateId)
                    .build());
        }

    }
}
