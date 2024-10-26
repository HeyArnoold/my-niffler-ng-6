package guru.qa.niffler.page.enums;

import lombok.Getter;

@Getter
public enum PeriodWeb {
    ALL_TIME("All time"),
    TODAY("Today"),
    WEEK("Last week"),
    MONTH("Last month");

    private final String title;

    PeriodWeb(String title) {
        this.title = title;
    }
}
