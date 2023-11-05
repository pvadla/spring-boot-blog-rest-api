package com.springboot.blog.config;

import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.hibernate.annotations.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class GeneralInterceptorAspect {

    @Pointcut("execution(* com.springboot.blog.controller.*.*(..))")
    public void loggingPointCut(){

    }

    @Before("loggingPointCut()")
    public void before(JoinPoint joinPoint){
        log.info("Before method invoked: "+ joinPoint.getSignature());
    }

//    @After("loggingPointCut()")
//    public void after(JoinPoint joinPoint){
//        log.info("After method invoked: "+ joinPoint.getSignature());
//    }

    @AfterReturning(value = "loggingPointCut()", returning = "post")
    public void afterReturning(JoinPoint joinPoint, PostResponse post){
        log.info("After Returning method invoked: "+ joinPoint.getSignature());
    }

    @AfterThrowing(value = "loggingPointCut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e){
        log.info("After Throwing method invoked: "+ joinPoint.getSignature());
    }

    @Around(value = "loggingPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around method invoked: "+ joinPoint.getSignature());
        Object object = joinPoint.proceed();

        if(object instanceof PostResponse){
            log.info("Around method invoked: "+ joinPoint.getSignature());
        }

        return object;

    }
}
