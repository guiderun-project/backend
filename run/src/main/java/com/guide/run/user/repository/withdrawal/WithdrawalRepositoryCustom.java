package com.guide.run.user.repository.withdrawal;

import com.guide.run.admin.dto.condition.WithdrawalSortCond;
import com.guide.run.admin.dto.response.user.WithdrawalItem;

import java.util.List;

public interface WithdrawalRepositoryCustom {

    List<WithdrawalItem> sortWithdrawal(int start, int limit, WithdrawalSortCond cond);
    long sortWithdrawalCount();
    List<WithdrawalItem> searchWithdrawal(String text, int start, int limit, WithdrawalSortCond cond);
    long searchWithdrawalCount(String text);
}
