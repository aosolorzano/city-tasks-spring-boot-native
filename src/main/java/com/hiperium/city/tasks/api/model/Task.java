package com.hiperium.city.tasks.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HIP_CTY_TASKS")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HIP_CTY_TASKS_SEQ")
    @SequenceGenerator(name = "HIP_CTY_TASKS_SEQ", sequenceName = "HIP_CTY_TASKS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "job_id", length = 30, nullable = false)
    private String jobId;

    @Column(name = "task_hour", nullable = false)
    private int hour;

    @Column(name = "task_minute", nullable = false)
    private int minute;

    @Column(name = "execution_days", length = 30, nullable = false)
    private String executionDays;

    @Column(name = "execution_command", nullable = false)
    private String executionCommand;

    @Column(name = "execute_until")
    private ZonedDateTime executeUntil;

    @Column(name = "device_id", length = 30, nullable = false)
    private String deviceId;

    @Column(name = "device_action", length = 30, nullable = false)
    private String deviceAction;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}
