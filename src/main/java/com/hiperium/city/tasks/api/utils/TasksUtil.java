package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.exception.TaskScheduleException;
import com.hiperium.city.tasks.api.model.Task;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

public final class TasksUtil {

    public static final String TASKS_PATH = "/api/tasks";
    private static final char[] HEX_ARRAY = "HiperiumTasksService".toCharArray();
    private static final int JOB_ID_LENGTH = 20;

    private TasksUtil() {
        // Empty constructor.
    }

    public static String generateJobId() {
        MessageDigest salt;
        try {
            salt = MessageDigest.getInstance("SHA-256");
            salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
        String uuid = bytesToHex(salt.digest());
        return uuid.substring(0, JOB_ID_LENGTH);
    }

    public static Task validateTaskId(Task newTask) {
        if (Objects.nonNull(newTask.getId())) {
            throw new TaskScheduleException("Task already exists with ID: " + newTask.getId() + ".");
        }
        return newTask;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
