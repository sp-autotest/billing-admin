package ru.bpc.billing.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
@Entity
@Table(name = "terminal")
public class Terminal {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "terminalSequence")
    @SequenceGenerator(name = "terminalSequence", sequenceName = "SEQ_TERMINAL")
    private Long id;
    @Column
    private String name;
    @Column
    private String agrn;
    @Column
    private String terminal;
    @ManyToOne(optional = false)
    @JoinColumn(name = "carrier_id", nullable=false, updatable=false, referencedColumnName="id")
    private Carrier carrier;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "terminal_countrycurrency",
            joinColumns = @JoinColumn(name = "terminal", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "countrycurrency", referencedColumnName = "id"))
    private Set<CountryCurrency> currencies = new HashSet<>();

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

    public String getAgrn() {
        return agrn;
    }

    public void setAgrn(String agrn) {
        this.agrn = agrn;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public Set<CountryCurrency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Set<CountryCurrency> currencies) {
        this.currencies = currencies;
    }
}
