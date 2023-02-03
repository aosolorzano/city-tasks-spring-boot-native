package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.job.TaskJob;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.utils.enums.DaysEnum;
import org.quartz.*;

import java.time.ZoneId;
import java.util.*;
import java.util.Calendar;

public final class JobsUtil {

    public static final String TASK_GROUP_NAME = "Task#Group";
    public static final String TASK_JOB_ID_DATA_KEY = "taskJobId";

    private JobsUtil() {
        // Empty constructor.
    }

    public static JobDetail createJobDetailFromTask(Task task) {
        return JobBuilder.newJob(TaskJob.class)
                .withIdentity(task.getJobId(), TASK_GROUP_NAME)
                .usingJobData(TASK_JOB_ID_DATA_KEY, task.getJobId())
                .build();
    }

    public static CronTrigger createCronTriggerFromTask(Task task, String zoneId) {
        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(task.getJobId(), TASK_GROUP_NAME)
                .startNow()
                .withSchedule(CronScheduleBuilder
                        .atHourAndMinuteOnGivenDaysOfWeek(
                                task.getHour(),
                                task.getMinute(),
                                getIntValuesFromExecutionDays(task.getExecutionDays()))
                        .inTimeZone(TimeZone.getTimeZone(ZoneId.of(zoneId))));
        if (Objects.nonNull(task.getExecuteUntil())) {
            java.util.Calendar executeUntilCalendar = java.util.Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of(zoneId)));
            executeUntilCalendar.set(java.util.Calendar.YEAR, task.getExecuteUntil().getYear());
            executeUntilCalendar.set(java.util.Calendar.MONTH, task.getExecuteUntil().getMonthValue() - 1);
            executeUntilCalendar.set(java.util.Calendar.DAY_OF_MONTH, task.getExecuteUntil().getDayOfMonth());
            executeUntilCalendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
            executeUntilCalendar.set(java.util.Calendar.MINUTE, 59);
            executeUntilCalendar.set(Calendar.SECOND, 59);
            // TODO: Fix the error: "java.lang.IllegalArgumentException: End time cannot be before start time"
            triggerBuilder.endAt(executeUntilCalendar.getTime());
        }
        return triggerBuilder.build();
    }

    public static Integer[] getIntValuesFromExecutionDays(String taskExecutionDays) {
        List<Integer> intsDaysOfWeek = new ArrayList<>();
        for (String dayOfWeek : taskExecutionDays.split(",")) {
            DaysEnum daysEnum = DaysEnum.getEnumFromString(dayOfWeek);
            switch (daysEnum) {
                case MON -> intsDaysOfWeek.add(DateBuilder.MONDAY);
                case TUE -> intsDaysOfWeek.add(DateBuilder.TUESDAY);
                case WED -> intsDaysOfWeek.add(DateBuilder.WEDNESDAY);
                case THU -> intsDaysOfWeek.add(DateBuilder.THURSDAY);
                case FRI -> intsDaysOfWeek.add(DateBuilder.FRIDAY);
                case SAT -> intsDaysOfWeek.add(DateBuilder.SATURDAY);
                case SUN -> intsDaysOfWeek.add(DateBuilder.SUNDAY);
                default ->
                        throw new IllegalArgumentException("The day of the week does not match with the accepted ones: " + daysEnum);
            }
        }
        return intsDaysOfWeek.toArray(Integer[]::new);
    }
}
