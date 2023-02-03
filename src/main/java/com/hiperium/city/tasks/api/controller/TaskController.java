package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.service.TaskService;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(TasksUtil.TASKS_PATH)
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Task> create(@RequestBody Mono<Task> task) {
        LOGGER.debug("create(): {}", task);
        return this.taskService.create(task);
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Task>> findById(@PathVariable("id") Long taskId) {
        LOGGER.debug("findById(): {}", taskId);
        return this.taskService.findById(taskId)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Task> getAll() {
        LOGGER.debug("getAll() - START");
        return this.taskService.findAll();
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Task>> update(@PathVariable("id") long taskId,
                                             @RequestBody Mono<Task> modifiedTask) {
        LOGGER.debug("update(): {}", taskId);
        return this.taskService.update(taskId, modifiedTask)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable("id") Long taskId) {
        LOGGER.debug("delete(): {}", taskId);
        return this.taskService.delete(taskId);
    }

    @GetMapping("/template")
    public Task getTaskTemplate() {
        return Task.builder()
                .name("Task name")
                .description("Task description")
                .hour(12)
                .minute(0)
                .executionDays("MON,WED,FRI")
                .executionCommand("java -jar /home/pi/hiperium-city-1.0.0.jar")
                .deviceId("1234567890")
                .deviceAction("ACTIVATE")
                .build();
    }
}
