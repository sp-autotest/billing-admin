package ru.bpc.billing.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "billing_system")
@Getter
@Setter
public class BillingSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billsysSequence")
    @SequenceGenerator(name = "billsysSequence", sequenceName = "SEQ_BILLSYS")
    private Long id;

    @Column(unique = true)
    private String name;
    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "host_address")
    private String hostAddress;
    @Column(name = "sftp_port")
    private Integer sftpPort;
    @Column(name = "path")
    private String path;
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "billing_systems_emails", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "emails", columnDefinition = "varchar(200)")
    private Set<String> emails = new HashSet<>();

    @Column(name = "mask_regexp")
    private String maskRegexp;

    @Column(name = "enabled")
    private Boolean enabled;

    @Override
    public String toString() {
        return "BillingSystem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", carrier=" + carrier.getName() +
                ", hostAddress='" + hostAddress + '\'' +
                ", login='" + login + '\'' +
                '}';
    }

    public String toStringEmail() {
        final StringBuilder sb = new StringBuilder("Billing system: ");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", carrier=").append(carrier.getName());
        sb.append(", hostAddress='").append(hostAddress).append('\'');
        sb.append(", port='").append(sftpPort).append('\'');
        sb.append(", login='").append(login).append('\'');
        return sb.toString();
    }
}