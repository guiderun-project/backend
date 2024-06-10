package com.guide.run.user.repository.user;

import com.guide.run.admin.dto.condition.UserSortCond;
import com.guide.run.admin.dto.condition.WithdrawalSortCond;
import com.guide.run.admin.dto.response.user.NewUserResponse;
import com.guide.run.admin.dto.response.user.UserItem;
import com.guide.run.admin.dto.response.user.WithdrawalItem;

import java.util.List;

public interface UserRepositoryAdmin {
    List<UserItem> sortAdminUser(int start, int limit, UserSortCond cond);
    long sortAdminUserCount();
    List<UserItem> searchAdminUser(int start, int limit, UserSortCond cond, String text);
    long searchAdminUserCount(String text);
    List<NewUserResponse> findNewUser(int start, int limit, String privateId);

    List<WithdrawalItem> sortWithdrawal(int start, int limit, WithdrawalSortCond cond);
    long sortWithdrawalCount();
    List<WithdrawalItem> searchWithdrawal(String text, int start, int limit, WithdrawalSortCond cond);
    long searchWithdrawalCount(String text);
}
