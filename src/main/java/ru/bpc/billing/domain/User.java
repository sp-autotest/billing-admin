package ru.bpc.billing.domain;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

/**
 * User: krainov
 * Date: 19.08.14
 * Time: 15:41
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSequence")
    @SequenceGenerator(name = "userSequence", sequenceName = "SEQ_USER", allocationSize = 1)
    private Long id;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "credentials_expired_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date credentialsExpiredAt;
    @Column(name = "is_locked")
    private boolean isLocked;
    @Column(name = "is_account_expired")
    private boolean isAccountExpired;
    @Column(name = "is_enabled")
    private boolean isEnabled;
    @Column(name = "password_history")
    private String passwordHistory;
    @Column(name = "roles")
    private String roles;
    @Transient
    private List auth = Arrays.asList(new SimpleGrantedAuthority("ADMIN"));

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        if (StringUtils.isNotBlank(roles) ) {
            for (String role : roles.split(";")) {
                list.add(new SimpleGrantedAuthority(role.toUpperCase()));
            }
            return list;
        }
        else return auth;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isAccountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return null != credentialsExpiredAt ? new Date().before(credentialsExpiredAt) : true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPasswordHistory() {
        return passwordHistory;
    }

    public void setPasswordHistory(String passwordHistory) {
        this.passwordHistory = passwordHistory;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Date getCredentialsExpiredAt() {
        return credentialsExpiredAt;
    }

    public void setCredentialsExpiredAt(Date credentialsExpiredAt) {
        this.credentialsExpiredAt = credentialsExpiredAt;
    }

    public boolean isAccountExpired() {
        return isAccountExpired;
    }

    public void setAccountExpired(boolean isAccountExpired) {
        this.isAccountExpired = isAccountExpired;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
