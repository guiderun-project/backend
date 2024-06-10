package com.guide.run.admin.service;

import com.guide.run.admin.dto.condition.WithdrawalSortCond;
import com.guide.run.admin.dto.response.user.WithdrawalItem;
import com.guide.run.event.entity.dto.response.get.Count;

import com.guide.run.user.repository.withdrawal.WithdrawalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminWithdrawalService {
    private final WithdrawalRepository withdrawalRepsoitory;

    public List<WithdrawalItem> getWithdrawalList(int start, int limit, WithdrawalSortCond cond){
        return withdrawalRepsoitory.sortWithdrawal(start, limit, cond);
    }

    public Count getWithdrawalCount(){
        return Count.builder()
                .count(withdrawalRepsoitory.sortWithdrawalCount())
                .build();
    }

    public List<WithdrawalItem> searchWithdrawal(String text, int start, int limit, WithdrawalSortCond cond){
        return withdrawalRepsoitory.searchWithdrawal(text, start, limit, cond);
    }

    public Count searchWithdrawalCount(String text){
        return Count.builder()
                .count(withdrawalRepsoitory.searchWithdrawalCount(text))
                .build();
    }
    
}
