package com.hiperium.city.tasks.api.service;

import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Task;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskServiceTest extends AbstractContainerBase {

    private static final String DEVICE_ID = "1";

    private static Task task;

    @Autowired
    private TaskService taskService;

    @BeforeAll
    public static void init() {
        task = Task.builder()
                .name("Test class")
                .description("Task description.")
                .hour(12)
                .minute(0)
                .executionDays("MON,WED,SUN")
                .executionCommand("java -jar test.jar")
                .deviceId(DEVICE_ID)
                .deviceAction("ACTIVATE")
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Create Task and Schedule a Job")
    void givenTaskObject_whenSave_thenReturnSavedTask() {
        Mono<Task> taskMono = Mono.just(task);
        Mono<Task> taskMonoResult = this.taskService.create(taskMono);
        StepVerifier.create(taskMonoResult)
                .assertNext(savedTask -> {
                    Assertions.assertThat(savedTask.getId()).isPositive();
                    Assertions.assertThat(savedTask.getName()).isEqualTo("Test class");
                    Assertions.assertThat(savedTask.getDescription()).isEqualTo("Task description.");
                    Assertions.assertThat(savedTask.getHour()).isEqualTo(12);
                    Assertions.assertThat(savedTask.getMinute()).isZero();
                    Assertions.assertThat(savedTask.getExecutionDays()).isEqualTo("MON,WED,SUN");
                    Assertions.assertThat(savedTask.getExecutionCommand()).isEqualTo("java -jar test.jar");
                    Assertions.assertThat(savedTask.getDeviceId()).isEqualTo(DEVICE_ID);
                    Assertions.assertThat(savedTask.getDeviceAction()).isEqualTo("ACTIVATE");
                    BeanUtils.copyProperties(savedTask, task);
                })
                .verifyComplete();
    }

    @Test
    @Order(2)
    @DisplayName("Find Task by ID")
    void givenTaskObject_whenFindById_thenReturnTask() {
        Mono<Task> taskMonoResult = this.taskService.findById(task.getId());
        StepVerifier.create(taskMonoResult)
                .assertNext(returnedTask -> {
                    Assertions.assertThat(returnedTask).isNotNull();
                    Assertions.assertThat(returnedTask.getId()).isEqualTo(task.getId());
                })
                .verifyComplete();
    }

    @Test
    @Order(3)
    @DisplayName("Find Task with not existing ID")
    void givenTaskObject_whenFindById_thenReturnException() {
        Mono<Task> taskMonoResult = this.taskService.findById(100L);
        StepVerifier.create(taskMonoResult)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @Order(4)
    @DisplayName("Find all Tasks")
    void givenTaskList_whenFindAll_thenReturnAllTasks() {
        Flux<Task> taskFluxResult = this.taskService.findAll();
        StepVerifier.create(taskFluxResult)
                .expectNextCount(1L)
                .verifyComplete();
    }


    @Test
    @Order(5)
    @DisplayName("Update Task by ID")
    void givenTaskObject_whenUpdate_thenReturnUpdatedTask() {
        task.setName("Test class updated");
        task.setDescription("Task description updated.");
        task.setHour(13);
        task.setMinute(30);
        task.setExecutionDays("TUE,THU,SAT");
        task.setDeviceAction("DEACTIVATE");
        Mono<Task> updatedTaskMono = Mono.just(task);
        Mono<Task> taskMonoResult = this.taskService.update(task.getId(), updatedTaskMono);
        StepVerifier.create(taskMonoResult)
                .assertNext(updatedTask -> {
                    Assertions.assertThat(updatedTask.getId()).isEqualTo(task.getId());
                    Assertions.assertThat(updatedTask.getName()).isEqualTo("Test class updated");
                    Assertions.assertThat(updatedTask.getDescription()).isEqualTo("Task description updated.");
                    Assertions.assertThat(updatedTask.getHour()).isEqualTo(13);
                    Assertions.assertThat(updatedTask.getMinute()).isEqualTo(30);
                    Assertions.assertThat(updatedTask.getExecutionDays()).isEqualTo("TUE,THU,SAT");
                    Assertions.assertThat(updatedTask.getDeviceAction()).isEqualTo("DEACTIVATE");
                })
                .verifyComplete();
    }

    @Test
    @Order(6)
    @DisplayName("Update Task that does not exist")
    void givenTaskObject_whenUpdate_thenReturnTaskException() {
        task.setName("Test class updated");
        task.setDescription("Task description updated.");
        task.setHour(13);
        task.setMinute(30);
        task.setExecutionDays("TUE,THU,SAT");
        task.setDeviceAction("DEACTIVATE");
        Mono<Task> updatedTaskMono = Mono.just(task);
        Mono<Task> taskMonoResult = this.taskService.update(100L, updatedTaskMono);
        StepVerifier.create(taskMonoResult)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }


    @Test
    @Order(7)
    @DisplayName("Delete Task by ID")
    void givenTaskId_whenDelete_thenDeleteTaskObject() {
        Mono<Void> taskMonoResult = this.taskService.delete(task.getId());
        StepVerifier.create(taskMonoResult)
                .verifyComplete();
    }

    @Test
    @Order(8)
    @DisplayName("Delete Task that does not exist")
    void givenNotExistingTask_whenDelete_thenReturnException() {
        Mono<Void> taskMonoResult = this.taskService.delete(100L);
        StepVerifier.create(taskMonoResult)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
