package com.hiperium.city.tasks.api.repository;

import com.hiperium.city.tasks.api.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findByJobId(String jobId);

}
