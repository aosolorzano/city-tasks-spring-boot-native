package com.hiperium.city.tasks.api.repository;

import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DeviceRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRepository.class);

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public DeviceRepository(DynamoDbAsyncClient dynamoDbAsyncClient) {
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
    }

    public Mono<Boolean> update(Device device) {
        LOGGER.debug("update(): {}", device);
        return this.findById(device.getId())
                .flatMap(deviceFound -> Mono.fromFuture(this.dynamoDbAsyncClient.putItem(this.putDeviceRequest(device))))
                .map(putItemResponse -> putItemResponse.sdkHttpResponse().isSuccessful());
    }

    public Mono<Device> findById(String id) {
        LOGGER.debug("findById(): {}", id);
        return Mono.fromFuture(this.dynamoDbAsyncClient.getItem(this.getDeviceRequest(id)))
                .filter(GetItemResponse::hasItem)
                .map(getItemResponse -> {
                    Map<String, AttributeValue> item = getItemResponse.item();
                    return Device.getFromItemValues(item);
                })
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    throw new ResourceNotFoundException("Device not found with ID: " + id);
                }));
    }

    public PutItemRequest putDeviceRequest(Device device) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(Device.DEVICE_ID_COL, AttributeValue.builder().s(device.getId()).build());
        item.put(Device.DEVICE_NAME_COL, AttributeValue.builder().s(device.getName()).build());
        item.put(Device.DEVICE_DESC_COL, AttributeValue.builder().s(device.getDescription()).build());
        item.put(Device.DEVICE_STATUS_COL, AttributeValue.builder().s(device.getStatus()).build());
        return PutItemRequest.builder()
                .tableName(Device.TABLE_NAME)
                .item(item)
                .build();
    }

    public GetItemRequest getDeviceRequest(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(Device.DEVICE_ID_COL, AttributeValue.builder().s(id).build());
        return GetItemRequest.builder()
                .tableName(Device.TABLE_NAME)
                .key(key)
                .attributesToGet(Device.DEVICE_ID_COL, Device.DEVICE_NAME_COL, Device.DEVICE_DESC_COL, Device.DEVICE_STATUS_COL)
                .build();
    }
}
