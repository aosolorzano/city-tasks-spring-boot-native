package com.hiperium.city.tasks.api.repository;

import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import com.hiperium.city.tasks.api.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest extends AbstractContainerBase {

    private static final String DEVICE_ID = "1";

    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @BeforeEach
    public void setup() {
        this.task = Task.builder()
                .id(1L)
                .jobId(UUID.randomUUID().toString().substring(0, 30))
                .name("Test class")
                .description("Task description.")
                .hour(12)
                .minute(0)
                .executionDays("MON,WED,SUN")
                .executionCommand("java -jar test.jar")
                .deviceId(DEVICE_ID)
                .deviceAction("ACTIVATE")
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Create Task")
    void givenTaskObject_whenSave_thenReturnSavedTask() {
        Task savedTask = this.taskRepository.save(this.task);
        assertThat(savedTask.getId()).isPositive();
    }

    @Test
    @DisplayName("Find Task by Job ID")
    void givenJobId_whenFindByJobId_thenReturnTaskObject() {
        Task savedTask = this.taskRepository.save(this.task);
        Task requiredTask = this.taskRepository.findByJobId(savedTask.getJobId());
        assertThat(requiredTask).isNotNull();
    }

    @Test
    @DisplayName("Update Task name")
    void givenTaskObject_whenUpdate_thenReturnUpdatedTask() {
        Task initialTask = this.taskRepository.save(this.task);
        initialTask.setName("Updated task");
        Task updatedTask = this.taskRepository.save(initialTask);
        assertThat(updatedTask.getName()).isEqualTo("Updated task");
    }

    @Test
    @DisplayName("Delete Task")
    void givenTaskId_whenDelete_thenDeleteTaskObject() {
        Task savedTask = this.taskRepository.save(this.task);
        this.taskRepository.deleteById(savedTask.getId());
        Optional<Task> taskOptional = this.taskRepository.findById(savedTask.getId());
        assertThat(taskOptional).isEmpty();
    }
}
