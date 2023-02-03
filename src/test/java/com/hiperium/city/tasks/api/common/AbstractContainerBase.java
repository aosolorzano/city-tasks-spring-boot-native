package com.hiperium.city.tasks.api.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractContainerBase {

    private static final PostgreSQLContainer POSTGRES_CONTAINER;

    protected static final LocalStackContainer LOCAL_STACK_CONTAINER;

    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse("localstack/localstack:latest");

    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:14.4")
                .withUsername("postgres")
                .withPassword("postgres123")
                .withDatabaseName("HiperiumCityTasksDB");
        LOCAL_STACK_CONTAINER = new LocalStackContainer(DOCKER_IMAGE_NAME)
                .withServices(LocalStackContainer.Service.DYNAMODB);
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry){
        Startables.deepStart(POSTGRES_CONTAINER, LOCAL_STACK_CONTAINER).join();
        // SPRING DATA JDBC
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        // AWS DYNAMODB
        registry.add("aws.region", LOCAL_STACK_CONTAINER::getRegion);
        registry.add("aws.dynamodb.endpoint-override", () -> LOCAL_STACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString());
        registry.add("aws.access-key-id", LOCAL_STACK_CONTAINER::getAccessKey);
        registry.add("aws.secret-access-key", LOCAL_STACK_CONTAINER::getSecretKey);
    }
}
