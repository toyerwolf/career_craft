package com.example.careercraft.exception;

import org.springframework.http.HttpStatus;

public class ChatException extends  AppException{
    public ChatException( String message) {
        super(HttpStatus.I_AM_A_TEAPOT, message);
    }
}
