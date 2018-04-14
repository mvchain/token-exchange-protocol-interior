package com.mvc.sell.console.config;

import com.mvc.common.exception.auth.TokenErrorException;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.security.auth.login.LoginException;

/**
 * @author qyc
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(com.mvc.common.handler.GlobalExceptionHandler.class);

    @ExceptionHandler(LoginException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result loginExceptionException() {
        return ResultGenerator.genFailResult(MessageConstants.TOKEN_ERROR_CODE, MessageConstants.getMsg("TOKEN_EXPIRE"));
    }

    @ExceptionHandler(TokenErrorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result tokenErrorExceptionException() {
        return ResultGenerator.genFailResult(MessageConstants.TOKEN_EXPIRE_CODE, MessageConstants.getMsg("TOKEN_EXPIRE"));
    }

    @ExceptionHandler(IllegalAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result illegalAccessExceptionException(IllegalAccessException e) {
        return ResultGenerator.genFailResult(e.getMessage());
    }
}
