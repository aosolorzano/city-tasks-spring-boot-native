package com.hiperium.city.tasks.api.repository;

import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.utils.DevicesUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.net.URI;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceRepositoryTest extends AbstractContainerBase {

    public static final String DEVICE_ID = "1";

    @Autowired
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @Autowired
    private DeviceRepository deviceRepository;

    @BeforeAll
    public static void init() {
        try (DynamoDbClient ddb = DynamoDbClient.builder()
                .endpointOverride(URI.create(LOCAL_STACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(LOCAL_STACK_CONTAINER.getAccessKey(), LOCAL_STACK_CONTAINER.getSecretKey())
                        )
                )
                .region(Region.of(LOCAL_STACK_CONTAINER.getRegion()))
                .build()) {

            DynamoDbWaiter dbWaiter = ddb.waiter();
            CreateTableRequest request = CreateTableRequest.builder()
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName("id")
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("id")
                            .keyType(KeyType.HASH)
                            .build())
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build())
                    .tableName(Device.TABLE_NAME)
                    .build();
            try {
                CreateTableResponse response = ddb.createTable(request);
                DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                        .tableName(Device.TABLE_NAME)
                        .build();
                WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
                waiterResponse.matched().response().ifPresent(System.out::println);
                String newTable = response.tableDescription().tableName();
                Assertions.assertThat(newTable).isEqualTo(Device.TABLE_NAME);
            } catch (DynamoDbException e) {
                Assertions.fail(e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    @DisplayName("Create Device Item")
    void givenDeviceObject_whenSave_mustSaveDeviceItem() {
        Device device = getNewDevice();
        Mono<PutItemResponse> putItemResponseMono = Mono.fromFuture(
                this.dynamoDbAsyncClient.putItem(DevicesUtil.putDeviceRequest(device)));
        StepVerifier.create(putItemResponseMono)
                .assertNext(putItemResponse -> {
                    Assertions.assertThat(putItemResponse).isNotNull();
                    Assertions.assertThat(putItemResponse.sdkHttpResponse().isSuccessful()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    @Order(2)
    @DisplayName("Find Device by ID")
    void givenDeviceId_whenFindById_mustReturnDeviceItem() {
        Mono<Device> deviceMonoResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceMonoResponse)
                .assertNext(device -> {
                    Assertions.assertThat(device).isNotNull();
                    Assertions.assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    Assertions.assertThat(device.getName()).isEqualTo("Device 1");
                    Assertions.assertThat(device.getDescription()).isEqualTo("Device 1 Description");
                    Assertions.assertThat(device.getStatus()).isEqualTo("ACTIVE");
                })
                .verifyComplete();
    }

    @Test
    @Order(3)
    @DisplayName("Find not existing Device ID")
    void givenDeviceId_whenFindById_mustThrowException() {
        Mono<Device> deviceMonoResponse = this.deviceRepository.findById("100");
        StepVerifier.create(deviceMonoResponse)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @Order(4)
    @DisplayName("Turn Device OFF")
    void givenDeviceItem_whenTaskTurnedOff_mustUpdateDeviceStatus() {
        Task task = getMockedTask();
        task.setDeviceAction("DEACTIVATE");
        Mono<Boolean> deviceUpdateResponse = this.deviceRepository.updateStatusByTask(task);
        StepVerifier.create(deviceUpdateResponse)
                .expectNext(true)
                .verifyComplete();

        Mono<Device> deviceResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceResponse)
                .assertNext(device -> {
                    Assertions.assertThat(device).isNotNull();
                    Assertions.assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    Assertions.assertThat(device.getName()).isEqualTo("Device 1");
                    Assertions.assertThat(device.getDescription()).isEqualTo("Device 1 Description");
                    Assertions.assertThat(device.getStatus()).isEqualTo("OFF");
                })
                .verifyComplete();
    }

    @Test
    @Order(5)
    @DisplayName("Turn Device ON")
    void givenDeviceItem_whenTaskTurnedOn_mustUpdateDeviceStatus() {
        Task task = getMockedTask();
        task.setDeviceAction("ACTIVATE");
        Mono<Boolean> deviceUpdateResponse = this.deviceRepository.updateStatusByTask(task);
        StepVerifier.create(deviceUpdateResponse)
                .expectNext(true)
                .verifyComplete();

        Mono<Device> deviceResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceResponse)
                .assertNext(device -> {
                    Assertions.assertThat(device).isNotNull();
                    Assertions.assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    Assertions.assertThat(device.getName()).isEqualTo("Device 1");
                    Assertions.assertThat(device.getDescription()).isEqualTo("Device 1 Description");
                    Assertions.assertThat(device.getStatus()).isEqualTo("ON");
                })
                .verifyComplete();
    }

    @Test
    @Order(6)
    @DisplayName("Update not existing Device ID")
    void givenDeviceItem_whenUpdate_mustThrowException() {
        Task task = getMockedTask();
        task.setDeviceId("100");
        Mono<Boolean> deviceMonoResponse = this.deviceRepository.updateStatusByTask(task);
        StepVerifier.create(deviceMonoResponse)
                .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException)
                .verify();
    }

    private static Device getNewDevice() {
        return Device.builder()
                .id(DEVICE_ID)
                .name("Device 1")
                .description("Device 1 Description")
                .status("ACTIVE")
                .build();
    }

    private static Task getMockedTask() {
        return Task.builder()
                .name("Task 1")
                .description("Task 1 Description")
                .deviceId(DEVICE_ID)
                .deviceAction("ACTIVATE")
                .build();
    }
}
