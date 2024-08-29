package com.example.careercraft.exception;

import org.springframework.http.HttpStatus;

public class IncompleteAnswersException extends  AppException{
    public IncompleteAnswersException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
