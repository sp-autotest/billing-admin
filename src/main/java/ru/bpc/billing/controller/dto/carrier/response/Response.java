package ru.bpc.billing.controller.dto.carrier.response;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Smirnov_Y on 01.04.2016.
 */
public class Response {
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Response buildSuccessful(){
        Response response = new Response();
        response.setSuccess(true);
        return response;
    }

    public static Response buildUnsuccessful(String message){
        Response response = new Response();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
