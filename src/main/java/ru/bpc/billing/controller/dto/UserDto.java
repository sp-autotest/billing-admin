package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.domain.User;

import java.util.Date;
import java.util.List;

/**
 * User: Krainov
 * Date: 24.09.2014
 * Time: 14:25
 */
public class UserDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean success;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String text;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<UserDto> children;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Long id;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String username;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = JsonDateSerializer.class)
    private Date updatedAt;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = JsonDateSerializer.class)
    private Date credentialsExpiredAt;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean isLocked;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean isAccountExpired;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean isEnabled;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String roles;

    public UserDto(){}
    public UserDto(User user){
        setUser(user);
    }

    public UserDto setUser(User user) {
        if ( null == user ) return this;
        success = true;
        text = ".";
        id = user.getId();
        username = user.getUsername();
        updatedAt = user.getUpdatedAt();
        credentialsExpiredAt = user.getCredentialsExpiredAt();
        isLocked = user.isLocked();
        isAccountExpired = user.isAccountExpired();
        isEnabled = user.isEnabled();
        roles = user.getRoles();
        return this;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCredentialsExpiredAt() {
        return credentialsExpiredAt;
    }

    public void setCredentialsExpiredAt(Date credentialsExpiredAt) {
        this.credentialsExpiredAt = credentialsExpiredAt;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Boolean getIsAccountExpired() {
        return isAccountExpired;
    }

    public void setIsAccountExpired(Boolean isAccountExpired) {
        this.isAccountExpired = isAccountExpired;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public List<UserDto> getChildren() {
        return children;
    }

    public void setChildren(List<UserDto> children) {
        this.children = children;
    }
}
