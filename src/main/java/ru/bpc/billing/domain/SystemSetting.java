package ru.bpc.billing.domain;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ResourceBundleMessageSource;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "system_settings")
public class SystemSetting {
    private static ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    static {
        messageSource.setBasename("i18n/gui/common");
    }

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "systemSettingSequence")
    @SequenceGenerator(name = "systemSettingSequence", sequenceName = "SEQ_OPTIONS", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String value;

    @Column(name = "modifier")
    private String modifier;

    @Column(name = "modify_date")
    private Date modifyDate;

    @Column(name = "encoder")
    private String encoder;

    @Column
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SystemSetting() {
    }

    public SystemSetting(String name, String value) {
        this.name = name;
        this.value = value;
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

    public String getMaskedValue(){
        if (Visibility.MASKED.equals(visibility) && !StringUtils.isBlank(value)){
            return maskValue(value);
        }
        return value;
    }

    public static String maskValue(String value) {
        return StringUtils.rightPad("", value.length(), "*");
    }

    public int getValueAsInt() {
        return Integer.valueOf(value);
    }



    public void setValue(String value) {
        this.value = value;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getEncoder() {
        return encoder;
    }

    public void setEncoder(String encoder) {
        this.encoder = encoder;
    }


    public enum Visibility{
        VISIBLE, MASKED, HIDDEN
    }

    public String getMessageCode() {
        return name + ".caption";
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("id:[").append(id).append("], ");
        sb.append("name:[").append(name).append("], ");
        sb.append("value:[").append(value).append("], ");
        sb.append("visibility:[").append(visibility).append("]");

        return sb.toString();
    }
}