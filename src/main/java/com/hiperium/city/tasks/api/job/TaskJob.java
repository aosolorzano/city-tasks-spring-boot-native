package com.hiperium.city.tasks.api.job;

import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
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
                .map(task -> this.deviceRepository.findById(task.getDeviceId())
                            .map(device -> this.changeDeviceStatus(device, task))
                            .flatMap(this.deviceRepository::update)
                )
                .subscribe(
                        result -> LOGGER.debug("execute() - Task Job executed successfully: {}", jobId),
                        error -> LOGGER.error("execute() - Error: {}", error.getMessage())
                );
    }

    private Device changeDeviceStatus(Device device, Task task) {
        if ("ACTIVATE".equals(task.getDeviceAction())) {
            device.setStatus("ON");
        } else {
            device.setStatus("OFF");
        }
        return device;
    }
}

