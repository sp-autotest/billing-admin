package ru.bpc.billing.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.bpc.billing.controller.dto.carrier.response.Response;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MessageSource messageSource;

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleAllException(Throwable ex) {
        logger.error("Unexpected exception ", ex);
        return Response.buildUnsuccessful("Внутренняя ошибка");
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ObjectError objectError = ex.getBindingResult().getAllErrors().get(0);
        String code = messageSource.getMessage(objectError.getCode(), objectError.getArguments(), StringUtils.isNotBlank(objectError.getDefaultMessage()) ? objectError.getDefaultMessage() : objectError.getCode(), request.getLocale());
        logger.error("BAD_REQUEST error: " + code);
        Response response = Response.buildUnsuccessful(code);
        return response;
    }
}
