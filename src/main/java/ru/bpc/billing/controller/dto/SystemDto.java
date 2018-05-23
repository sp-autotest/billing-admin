package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.*;

/**
 * User: Krainov
 * Date: 19.09.2014
 * Time: 17:40
 */
public class SystemDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean success;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String text;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String name;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String value;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String oldValue;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<SystemDto> children;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<SystemDto> getChildren() {
        return children;
    }

    public void setChildren(List<SystemDto> children) {
        this.children = children;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
}
