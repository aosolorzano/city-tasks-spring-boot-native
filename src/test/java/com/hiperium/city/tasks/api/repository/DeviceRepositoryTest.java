package com.hiperium.city.tasks.api.repository;

import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Device;
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

    private static DynamoDbClient ddb;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @BeforeAll
    public static void init() {
        ddb = DynamoDbClient.builder()
                .endpointOverride(URI.create(LOCAL_STACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(LOCAL_STACK_CONTAINER.getAccessKey(), LOCAL_STACK_CONTAINER.getSecretKey())
                        )
                )
                .region(Region.of(LOCAL_STACK_CONTAINER.getRegion()))
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Create Devices Table")
    void givenDynamoDBClient_whenCreateTable_mustCreateTable() {
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

    @Test
    @Order(2)
    @DisplayName("Create Device Item")
    void givenDeviceObject_whenSave_mustSaveDeviceItem() {
        Device device = getNewDevice();
        Mono<PutItemResponse> putItemResponseMono = Mono.fromFuture(
                this.dynamoDbAsyncClient.putItem(this.deviceRepository.putDeviceRequest(device)));
        StepVerifier.create(putItemResponseMono)
                .assertNext(putItemResponse -> {
                    Assertions.assertThat(putItemResponse).isNotNull();
                    Assertions.assertThat(putItemResponse.sdkHttpResponse().isSuccessful()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    @Order(3)
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
    @Order(4)
    @DisplayName("Find not existing Device ID")
    void givenDeviceId_whenFindById_mustThrowException() {
        Mono<Device> deviceMonoResponse = this.deviceRepository.findById("100");
        StepVerifier.create(deviceMonoResponse)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @Order(5)
    @DisplayName("Update Device Item")
    void givenDeviceItem_whenUpdate_mustUpdateDeviceItem() {
        Device device = getNewDevice();
        device.setName("Device 1 Updated");
        device.setDescription("Device 1 Description Updated");
        device.setStatus("INACTIVE");
        Mono<Boolean> deviceMonoResponse = this.deviceRepository.update(device);
        StepVerifier.create(deviceMonoResponse)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @Order(6)
    @DisplayName("Verify Device Changes")
    void givenDeviceId_whenFindById_mustVerifyLastDeviceChanges() {
        Mono<Device> deviceMonoResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceMonoResponse)
                .assertNext(device -> {
                    Assertions.assertThat(device).isNotNull();
                    Assertions.assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    Assertions.assertThat(device.getName()).isEqualTo("Device 1 Updated");
                    Assertions.assertThat(device.getDescription()).isEqualTo("Device 1 Description Updated");
                    Assertions.assertThat(device.getStatus()).isEqualTo("INACTIVE");
                })
                .verifyComplete();
    }

    @Test
    @Order(7)
    @DisplayName("Update not existing Device ID")
    void givenDeviceItem_whenUpdate_mustThrowException() {
        Device device = getNewDevice();
        device.setId("100");
        Mono<Boolean> deviceMonoResponse = this.deviceRepository.update(device);
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
}
