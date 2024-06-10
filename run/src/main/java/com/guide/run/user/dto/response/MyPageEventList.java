package com.guide.run.user.dto.response;

import com.guide.run.event.entity.dto.response.get.MyPageEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MyPageEventList {
    private List<MyPageEvent> items;
}
