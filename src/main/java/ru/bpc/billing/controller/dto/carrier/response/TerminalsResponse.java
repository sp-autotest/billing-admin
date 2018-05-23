package ru.bpc.billing.controller.dto.carrier.response;

import ru.bpc.billing.controller.dto.carrier.dto.TerminalDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smirnov_Y on 01.04.2016.
 */
public class TerminalsResponse extends Response {
    private List<TerminalDto> data = new ArrayList<>();

    public List<TerminalDto> getData() {
        return data;
    }

    public void setData(List<TerminalDto> data) {
        this.data = data;
    }
}
