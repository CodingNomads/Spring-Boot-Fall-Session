package com.codingnomads.demo_web.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * This class is an Aspect that provides logging for our application.
 * It uses Aspect-Oriented Programming (AOP) to "intercept" method calls without
 * needing to modify the code of those methods.
 * 
 * In this case, it tracks how long methods annotated with @Logged take to execute.
 */
@Aspect
@Component
public class LoggingAspect {

    /**
     * This 'Around' advice runs both BEFORE and AFTER the target method.
     * The pointcut expression identifies all methods or classes annotated with @Logged.
     */
    @Around("@annotation(com.codingnomads.demo_web.logging.Logged) || @within(com.codingnomads.demo_web.logging.Logged)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long tick = System.currentTimeMillis();

        // This line actually executes the original method
        Object result = joinPoint.proceed();

        long tack = System.currentTimeMillis();
        
        // After the method finishes, we calculate and print the duration
        System.out.println("AOP Log - Method: " + joinPoint.getSignature().toShortString() + ", Duration: " + (tack - tick) + " ms");

        return result;
    }

}
