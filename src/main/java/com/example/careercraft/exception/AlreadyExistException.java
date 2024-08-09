package com.example.careercraft.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistException extends AppException {
    public AlreadyExistException(String message) {
        super(message, HttpStatus.CONFLICT); // 409 Conflict for already exists
    }

}
