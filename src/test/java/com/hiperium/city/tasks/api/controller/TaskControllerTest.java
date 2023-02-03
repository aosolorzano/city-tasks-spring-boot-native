package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest extends AbstractContainerBase {

    private static final String DEVICE_ID = "1";

    @Autowired
    private WebTestClient webTestClient;

    private static Task task;

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
    @DisplayName("Create Task")
    void givenTaskObject_whenSaveTask_thenReturnSavedTask() {
        this.webTestClient
                .post()
                .uri(TasksUtil.TASKS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(task)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Task.class)
                .value(savedTask -> {
                    Assertions.assertThat(savedTask.getId()).isNotNull().isPositive();
                    Assertions.assertThat(savedTask.getName()).isEqualTo(task.getName());
                    Assertions.assertThat(savedTask.getDescription()).isEqualTo(task.getDescription());
                    Assertions.assertThat(savedTask.getHour()).isEqualTo(task.getHour());
                    Assertions.assertThat(savedTask.getMinute()).isEqualTo(task.getMinute());
                    Assertions.assertThat(savedTask.getExecutionDays()).isEqualTo(task.getExecutionDays());
                    Assertions.assertThat(savedTask.getExecutionCommand()).isEqualTo(task.getExecutionCommand());
                    Assertions.assertThat(savedTask.getDeviceId()).isEqualTo(task.getDeviceId());
                    Assertions.assertThat(savedTask.getDeviceAction()).isEqualTo(task.getDeviceAction());
                    BeanUtils.copyProperties(savedTask, task);
                });
    }

    @Test
    @Order(2)
    @DisplayName("Find Task by ID")
    void givenTaskId_whenFindTaskById_thenReturnTaskFound() {
        this.webTestClient
                .get()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), task.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Task.class)
                .value(foundTask -> {
                    Assertions.assertThat(foundTask.getId()).isEqualTo(task.getId());
                    Assertions.assertThat(foundTask.getName()).isEqualTo(task.getName());
                    Assertions.assertThat(foundTask.getDescription()).isEqualTo(task.getDescription());
                    Assertions.assertThat(foundTask.getJobId()).isEqualTo(task.getJobId());
                    Assertions.assertThat(foundTask.getHour()).isEqualTo(task.getHour());
                    Assertions.assertThat(foundTask.getMinute()).isEqualTo(task.getMinute());
                    Assertions.assertThat(foundTask.getExecutionDays()).isEqualTo(task.getExecutionDays());
                    Assertions.assertThat(foundTask.getExecutionCommand()).isEqualTo(task.getExecutionCommand());
                    Assertions.assertThat(foundTask.getDeviceId()).isEqualTo(task.getDeviceId());
                    Assertions.assertThat(foundTask.getDeviceAction()).isEqualTo(task.getDeviceAction());
                });
    }

    @Test
    @Order(3)
    @DisplayName("Find Tasks that does not exist")
    void givenNotExistingTasksId_whenFindTaskById_thenReturnError404() {
        this.webTestClient
                .get()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), 100L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    @Order(4)
    @DisplayName("Find all Tasks")
    void givenTasksList_whenFindAllTasks_thenReturnTasksList() {
        this.webTestClient
                .get()
                .uri(TasksUtil.TASKS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Task.class)
                .value(taskList -> {
                    Assertions.assertThat(taskList).isNotEmpty();
                    Assertions.assertThat(taskList).hasSize(1);
                });
    }

    @Test
    @Order(5)
    @DisplayName("Update Task")
    void givenModifiedTask_whenUpdateTask_thenReturnUpdatedTask() {
        task.setName("Test class updated");
        task.setDescription("Task description updated.");
        task.setHour(13);
        task.setMinute(30);
        task.setExecutionDays("MON,TUE,WED,THU,FRI,SAT,SUN");
        task.setDeviceAction("DEACTIVATE");

        this.webTestClient
                .put()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), task.getId())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(task)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Task.class)
                .value(updatedTask -> {
                    Assertions.assertThat(updatedTask.getId()).isEqualTo(task.getId());
                    Assertions.assertThat(updatedTask.getName()).isEqualTo(task.getName());
                    Assertions.assertThat(updatedTask.getDescription()).isEqualTo(task.getDescription());
                    Assertions.assertThat(updatedTask.getHour()).isEqualTo(task.getHour());
                    Assertions.assertThat(updatedTask.getMinute()).isEqualTo(task.getMinute());
                    Assertions.assertThat(updatedTask.getExecutionDays()).isEqualTo(task.getExecutionDays());
                    Assertions.assertThat(updatedTask.getDeviceAction()).isEqualTo(task.getDeviceAction());
                });
    }

    @Test
    @Order(6)
    @DisplayName("Update Tasks that does not exist")
    void givenNotExistingTasksId_whenUpdateTask_thenReturnError404() {
        this.webTestClient
                .put()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), 100L)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(task)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(7)
    @DisplayName("Delete Task")
    void givenTaskId_whenDeleteTask_thenReturnResponse200() {
        this.webTestClient
                .delete()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), task.getId())
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    @Order(8)
    @DisplayName("Delete not existing Task")
    void givenTaskId_whenDeleteTaskById_thenReturnError404() {
        this.webTestClient
                .delete()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), task.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

}
