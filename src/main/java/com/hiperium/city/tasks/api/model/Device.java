package com.hiperium.city.tasks.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    public static final String TABLE_NAME = "Devices";
    public static final String DEVICE_ID_COL = "id";
    public static final String DEVICE_NAME_COL = "name";
    public static final String DEVICE_DESC_COL = "description";
    public static final String DEVICE_STATUS_COL = "status";

    private String id;
    private String name;
    private String description;
    private String status;

    public static Device getFromItemValues(Map<String, AttributeValue> item) {
        Device device = new Device();
        if (Objects.nonNull(item) && !item.isEmpty()) {
            device.setId(item.get(DEVICE_ID_COL).s());
            device.setName(item.get(DEVICE_NAME_COL).s());
            device.setDescription(item.get(DEVICE_DESC_COL).s());
            device.setStatus(item.get(DEVICE_STATUS_COL).s());
        }
        return device;
    }
}
