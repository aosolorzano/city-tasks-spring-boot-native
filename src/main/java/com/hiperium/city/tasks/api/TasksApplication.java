package com.hiperium.city.tasks.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hiperium.city.tasks.api.config.PropertiesLoader;
import com.hiperium.city.tasks.api.config.hints.PostgresRuntimeHints;
import com.hiperium.city.tasks.api.config.hints.QuartzRuntimeHints;
import com.hiperium.city.tasks.api.job.TaskJob;
import com.hiperium.city.tasks.api.vo.AuroraPostgresSecretVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ImportRuntimeHints({PostgresRuntimeHints.class, QuartzRuntimeHints.class})
@RegisterReflectionForBinding({AuroraPostgresSecretVO.class, TaskJob.class})
public class TasksApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(TasksApplication.class);

    public static void main(String[] args) throws JsonProcessingException {
        LOGGER.info("main() - BEGIN");
        PropertiesLoader.load();
        SpringApplication.run(TasksApplication.class, args);
        LOGGER.info("main() - END");
    }
}
