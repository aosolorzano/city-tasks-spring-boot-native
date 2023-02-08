package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DevicesUtil {
    private DevicesUtil() {
        // Private constructor.
    }

    public static Device getFromItemResponse(GetItemResponse itemResponse) {
        Map<String, AttributeValue> item = itemResponse.item();
        Device device = new Device();
        if (Objects.nonNull(item) && !item.isEmpty()) {
            device.setId(item.get(Device.DEVICE_ID_COL).s());
            device.setName(item.get(Device.DEVICE_NAME_COL).s());
            device.setDescription(item.get(Device.DEVICE_DESC_COL).s());
            device.setStatus(item.get(Device.DEVICE_STATUS_COL).s());
        }
        return device;
    }

    public static Device changeDeviceStatus(Device device, Task task) {
        if ("ACTIVATE".equals(task.getDeviceAction())) {
            device.setStatus("ON");
        } else {
            device.setStatus("OFF");
        }
        return device;
    }

    public static PutItemRequest putDeviceRequest(Device device) {
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

    public static GetItemRequest getDeviceRequest(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(Device.DEVICE_ID_COL, AttributeValue.builder().s(id).build());
        return GetItemRequest.builder()
                .tableName(Device.TABLE_NAME)
                .key(key)
                .attributesToGet(Device.DEVICE_ID_COL, Device.DEVICE_NAME_COL, Device.DEVICE_DESC_COL, Device.DEVICE_STATUS_COL)
                .build();
    }
}
