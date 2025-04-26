package com.epam.finaltask.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.util.Date;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.warn("Entity not found exception", ex);
        return buildErrorModelAndView(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        log.error("Illegal state: {}", ex.getMessage());
        return buildErrorModelAndView(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorModelAndView(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(TemplateProcessingException.class)
    public ModelAndView handleTemplateProcessingException(TemplateProcessingException ex, WebRequest request) {
        log.error("Template processing error: {}", ex.getMessage(), ex);
        return buildErrorModelAndView(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    private ModelAndView buildErrorModelAndView(Exception ex, HttpStatus status, WebRequest request) {
        ModelAndView modelAndView = new ModelAndView("error/" + status.value());
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );
        modelAndView.addObject("error", errorDetails);
        modelAndView.addObject("status", status.value());
        return modelAndView;
    }
}
