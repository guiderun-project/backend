package com.guide.run.admin.service;

import com.guide.run.admin.dto.condition.WithdrawalSortCond;
import com.guide.run.admin.dto.response.user.WithdrawalItem;
import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminWithdrawalService {
    private final UserRepository userRepository;

    public List<WithdrawalItem> getWithdrawalList(int start, int limit, WithdrawalSortCond cond){
        return userRepository.sortWithdrawal(start, limit, cond);
    }

    public Count getWithdrawalCount(){
        return Count.builder()
                .count(userRepository.sortWithdrawalCount())
                .build();
    }

    public List<WithdrawalItem> searchWithdrawal(String text, int start, int limit, WithdrawalSortCond cond){
        return userRepository.searchWithdrawal(text, start, limit, cond);
    }

    public Count searchWithdrawalCount(String text){
        return Count.builder()
                .count(userRepository.searchWithdrawalCount(text))
                .build();
    }
    
}
