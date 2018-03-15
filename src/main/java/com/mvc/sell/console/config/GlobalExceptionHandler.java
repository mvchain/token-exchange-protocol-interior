package com.mvc.sell.console.config;

import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qyc
 */
@ControllerAdvice("com.mvc")
@ResponseBody
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(com.mvc.common.handler.GlobalExceptionHandler.class);

    @ExceptionHandler(LoginException.class)
    public Result loginExceptionException(HttpServletResponse response, LoginException ex) {
        response.setStatus(403);
        return ResultGenerator.genFailResult(ex.getMessage());
    }

}
