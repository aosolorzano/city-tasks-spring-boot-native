package com.hiperium.city.tasks.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class TaskScheduleException extends RuntimeException {

    public TaskScheduleException(String message) {
        super(message);
    }
}
