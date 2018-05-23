package ru.bpc.billing.controller.automate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerParametersDto {
    private String address;
    private Integer port;
    private String path;
    private String login;
    private String password;

    @Override
    public String toString() {
        return "ServerParametersDto{" +
                "address='" + address + '\'' +
                "port='" + port + '\'' +
                ", path='" + path + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
