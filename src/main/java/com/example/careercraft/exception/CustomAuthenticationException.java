package com.example.careercraft.exception;

import org.springframework.http.HttpStatus;

public class CustomAuthenticationException extends AppException{
    public CustomAuthenticationException( String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
