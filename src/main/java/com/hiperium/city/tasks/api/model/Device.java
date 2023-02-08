package com.hiperium.city.tasks.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
