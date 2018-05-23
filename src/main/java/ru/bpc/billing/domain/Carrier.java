package ru.bpc.billing.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
@Entity
@Table(name = "carrier")
public class Carrier {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carrierSequence")
    @SequenceGenerator(name = "carrierSequence", sequenceName = "SEQ_CARRIER")
    private Long id;
    @Column
    private String name;
    @Column(name = "iata_code", unique = true)
    private String iataCode;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "mcc")
    private String mcc;

    @OneToMany(mappedBy = "carrier", fetch = FetchType.EAGER)
    private Set<BillingSystem> bsList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public Set<BillingSystem> getBsList() {
        return bsList;
    }

    public void setBsList(Set<BillingSystem> bsList) {
        this.bsList = bsList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Carrier{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", iataCode='").append(iataCode).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", mcc='").append(mcc).append('\'');
        sb.append(", bsList='").append(bsList.stream().map(BillingSystem :: getName).collect(Collectors.toList())).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
