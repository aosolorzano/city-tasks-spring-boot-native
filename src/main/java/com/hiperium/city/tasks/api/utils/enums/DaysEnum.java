package com.hiperium.city.tasks.api.utils.enums;

import java.util.Arrays;

public enum DaysEnum {
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT,
    SUN;

    public static DaysEnum getEnumFromString(String dayOfWeek) {
        return Arrays.stream(DaysEnum.values())
                .filter(daysEnum -> daysEnum.name().equals(dayOfWeek))
                .findFirst()
                .orElse(null);
    }
}
