package com.hiperium.city.tasks.api.job;

import com.hiperium.city.tasks.api.exception.TaskScheduleException;
import com.hiperium.city.tasks.api.repository.DeviceRepository;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.utils.JobsUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TaskJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskJob.class);

    private final TaskRepository taskRepository;
    private final DeviceRepository deviceRepository;

    public TaskJob(TaskRepository taskRepository, DeviceRepository deviceRepository) {
        this.taskRepository = taskRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void execute(JobExecutionContext context) {
        LOGGER.debug("execute() - START");
        final String jobId = context.getJobDetail().getJobDataMap().getString(JobsUtil.TASK_JOB_ID_DATA_KEY);
        Mono.just(jobId)
                .map(this.taskRepository::findByJobId)
                .flatMap(this.deviceRepository::updateStatusByTask)
                .map(this::validateDeviceUpdate)
                .subscribe(
                        result -> LOGGER.debug("execute() - Job executed successfully: {}", jobId),
                        error -> LOGGER.error("execute() - Error: {}", error.getMessage())
                );
    }

    private Mono<Void> validateDeviceUpdate(boolean result) {
        if (result) return Mono.empty();
        else return Mono.error(new TaskScheduleException("Device Status was not updated."));
    }
}

