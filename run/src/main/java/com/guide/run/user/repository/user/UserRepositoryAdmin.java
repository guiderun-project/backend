package com.guide.run.user.repository.user;

import com.guide.run.admin.dto.condition.UserSortCond;
import com.guide.run.admin.dto.response.NewUserResponse;
import com.guide.run.admin.dto.response.UserItem;

import java.util.List;

public interface UserRepositoryAdmin {
    List<UserItem> sortAdminUser(int start, int limit, UserSortCond cond);
    long sortAdminUserCount();
    List<UserItem> searchAdminUser(int start, int limit, UserSortCond cond, String text);
    long searchAdminUserCount(String text);
    List<NewUserResponse> findNewUser(int start, int limit);
}
