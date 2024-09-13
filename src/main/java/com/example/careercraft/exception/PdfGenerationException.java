package com.example.careercraft.exception;

import org.springframework.http.HttpStatus;

public class PdfGenerationException extends  AppException{
    public PdfGenerationException( String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
