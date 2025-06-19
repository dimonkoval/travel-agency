package com.epam.finaltask.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceLogging {

    @Before("within(com.epam.finaltask.service..*)")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "within(com.epam.finaltask.service..*)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Exiting {} with result: {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "within(com.epam.finaltask.service..*)", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Exception in {}: {}", joinPoint.getSignature(), error.getMessage(), error);
    }
}
