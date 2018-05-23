package ru.bpc.billing.controller.dto.carrier.dto;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.controller.dto.JsonDateSerializer;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class BillingSystemDto {
    private Long id;
    private String name;
    private Long carrierId;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = JsonDateSerializer.class)
    private Date createdDate;

    private String host;
    private String port;
    private String path;
    private String login;
    private String password;

    private String emailsCSV;

    private String maskRegexp;
    private boolean enabled;

}
