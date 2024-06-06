package com.guide.run.admin.service;

import com.guide.run.admin.dto.response.partner.AdminPartnerResponse;
import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPartnerService {
    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;

    public List<AdminPartnerResponse> getPartnerList(String userId, String kind, int start, int limit){

        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        List<AdminPartnerResponse> response = partnerRepository.getAdminPartner(user.getPrivateId(), user.getType(), kind, limit, start);
        return response;
    }

    public Count getPartnerCount(String userId, String kind){
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);

        return Count.builder()
                .count(partnerRepository.countAdminPartner(user.getPrivateId(), user.getType(), kind))
                .build();
    }

    public List<AdminPartnerResponse> searchPartnerList(String userId, String text, int start, int limit){
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);

        return partnerRepository.searchAdminPartner(user.getPrivateId(), user.getType(), text, limit, start);
    }

    public Count searchPartnerCount(String userId, String text){
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        return Count.builder()
                .count(partnerRepository.searchAdminPartnerCount(user.getPrivateId(), user.getType(), text))
                .build();

    }
}
