package com.hiperium.city.tasks.api.utils.enums;

public enum DaysEnum {
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT,
    SUN;

    public static DaysEnum getEnumFromString(String dayOfWeek) {
        DaysEnum result = null;
        for (DaysEnum daysEnum : DaysEnum.values()) {
            if (daysEnum.name().equals(dayOfWeek)) {
                result = daysEnum;
                break;
            }
        }
        return result;
    }
}
