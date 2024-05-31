package com.guide.run.partner.service;

import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.partner.PartnerLike;
import com.guide.run.partner.entity.partner.repository.PartnerLikeRepository;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final PartnerLikeRepository partnerLikeRepository;
    private final UserRepository userRepository;

    public void partnerLike(String userId, String privateId){
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        User partner = userRepository.findUserByUserId(userId).orElseThrow(RuntimeException::new); //todo : 존재하지 않는 파트너 에러 추가 필요
        PartnerLike partnerLike = partnerLikeRepository.findById(partner.getPrivateId()).orElse(null);
        //좋아요를 이미 한 사용자인데 좋아요를 눌렀을 때. -> 좋아요 취소
        //안한 사용자인데 좋아요를 눌렀을 때 -> 좋아요
        //사용자가 좋아요 내역이 없을 때. -> 새 리스트 생성 후 좋아요 추가

        if(partnerLike == null){ //처음 받는 좋아요
            List<String> newLike = new ArrayList<>();
            newLike.add(user.getPrivateId());

            partnerLike = PartnerLike.builder()
                    .recId(partner.getPrivateId())
                    .sendIds(newLike)
                    .build();
            partnerLikeRepository.save(partnerLike);
        }else if(partnerLike.getSendIds().contains(privateId)){//이미 받은 적 있는 좋아요 = 취소
            partnerLike.deleteLike(user.getPrivateId());
        }else{
            partnerLike.addLike(user.getPrivateId());
        }

    }
}
