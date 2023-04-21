package com.example.Exception;

import com.example.POJO.R;
import com.example.common.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理器
//默认为全部
@RestControllerAdvice
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(CustomException.class)
    public R<String> CustomExceptionHandler(CustomException ce){
        log.info("捕获业务异常");
        return R.error("业务失败");

    }
    //捕获哪些异常
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public R<String> exceptionHandler(Exception ex){
        //日志
        log.error(ex.getMessage());
        //输出报错信息
        return R.error("失败");
    }
}
