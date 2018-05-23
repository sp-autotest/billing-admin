package ru.bpc.billing.repository;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.bpc.billing.AbstractTest;
import ru.bpc.billing.domain.BillingTerminal;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * User: Krainov
 * Date: 25.09.2014
 * Time: 14:37
 */
public class BillingTerminalRepositoryTest extends AbstractTest {

    @Resource
    protected BillingTerminalRepository billingTerminalRepository;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testFindByCountry() {
        for (BillingTerminal billingTerminal : billingTerminalRepository.findAll()) {
            System.out.println(billingTerminal.getCountryCode());
        }
        System.out.println("AB === " + billingTerminalRepository.findByCountryCode("AB"));

        /*
        List<BillingTerminal> terminals = jdbcTemplate.query("select * from billing_terminal", new RowMapper<BillingTerminal>() {
            @Override
            public BillingTerminal mapRow(ResultSet resultSet, int i) throws SQLException {
                BillingTerminal billingTerminal1 = new BillingTerminal();
                billingTerminal1.setCountryCode(resultSet.getString("country_code"));
                billingTerminal1.setTerminalId(resultSet.getString("terminal_id"));
                return billingTerminal1;
            }
        });
        for (BillingTerminal terminal : terminals) {
            System.out.println(terminal.getCountryCode());
        }
        */
    }
}
