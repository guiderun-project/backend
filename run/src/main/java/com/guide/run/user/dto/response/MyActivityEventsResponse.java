package com.guide.run.user.dto.response;

import com.guide.run.event.entity.type.EventType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Getter
@Builder
public class MyActivityEventsResponse {
    private List<Item> items;
    private Pagination pagination;

    @Getter
    public static class Item {
        private Long id;
        private String name;
        private EventType type;
        private String date;
        private String dateText;

        public Item(Long id, String name, EventType type, LocalDateTime startTime) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.date = String.format("%d.%02d.%02d", startTime.getYear(), startTime.getMonthValue(), startTime.getDayOfMonth());
            this.dateText = startTime.getMonthValue() + "월 " + startTime.getDayOfMonth() + "일 ("
                    + startTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN) + ")";
        }
    }

    @Getter
    @Builder
    public static class Pagination {
        private int page;
        private int size;
        private long totalCount;
        private int totalPages;
        private boolean hasNext;
    }
}
