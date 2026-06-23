package com.guide.run.user.dto.response;

import com.guide.run.event.entity.Event;
import com.guide.run.user.entity.type.UserType;
import lombok.Builder;
import lombok.Getter;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Getter
@Builder
public class MyActivityPartnersResponse {
    private List<Item> items;
    private Pagination pagination;

    @Getter
    @Builder
    public static class Item {
        private String partnerId;
        private String name;
        private UserType type;
        private int eventCount;
        private List<EventItem> events;
    }

    @Getter
    public static class EventItem {
        private Long id;
        private String name;
        private String date;
        private String dateText;

        public EventItem(Event event) {
            this.id = event.getId();
            this.name = event.getName();
            this.date = String.format("%d.%02d.%02d",
                    event.getStartTime().getYear(),
                    event.getStartTime().getMonthValue(),
                    event.getStartTime().getDayOfMonth());
            this.dateText = event.getStartTime().getMonthValue() + "월 "
                    + event.getStartTime().getDayOfMonth() + "일 ("
                    + event.getStartTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN) + ")";
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
