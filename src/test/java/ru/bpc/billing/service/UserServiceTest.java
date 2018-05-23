package ru.bpc.billing.service;

import org.junit.Test;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import ru.bpc.billing.AbstractTest;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.repository.UserRepository;

import javax.annotation.Resource;

/**
 * User: Krainov
 * Date: 22.09.2014
 * Time: 14:34
 */
public class UserServiceTest extends AbstractTest {

    @Resource
    private UserService userService;
    @Resource
    private UserRepository userRepository;
    @Resource
    private StandardPasswordEncoder passwordEncoder;

    @Test
    public void testCreatePassowrd() {
        String p1 = passwordEncoder.encode("test");
        String p2 = passwordEncoder.encode("test");

        System.out.println("p1 = " + p1);
        System.out.println("p2 = " + p2);
        System.out.println(p1.equals(p2));

        boolean b1 = passwordEncoder.matches("test1",p1);
        boolean b2 = passwordEncoder.matches("test",p2);

        System.out.println("b1 = " + b1);
        System.out.println("b2 = " + b2);
    }

    @Test
    public void testCreate() {


        User user = userService.create("esupport", "alfa2015!");
        assertNotNull(user);
        System.out.println(user.getPassword());
    }

    @Test
    public void testCreateUsers() {
        String[] logins = {
//                "akopytov",
//                "pnikiforov",
//                "esupport",
//                "asmirnov",
//                "emelnikov",
//                "rbssupport"
                "nzaytseva"
        };
        String password = "alfa2015!";
        for (String login : logins) {
            createSql(login,password);
        }
    }

    private void createSql(String login,String password) {
        if ( null == userRepository.findByUsername(login) ) {
            User user = userService.create(login, password);
            assertNotNull(user);
            String sql = "insert into users (updated_at, credentials_expired_at, is_account_expired, is_enabled, is_locked, password, password_history, username, id, roles) values (CURRENT_DATE - interval '1' month, null, '0', '1', '0', '" + user.getPassword() + "', '" + user.getPassword() + ";', '" + login + "', SEQ_USER.nextval, null);";
            System.out.println(login + ":" + sql);
            System.out.println(sql);
        }

    }

    @Test
    public void testChangePassword() {
        User user = userRepository.findByUsername("admin1");
        assertNotNull(user);
        User user1 = userService.changePassword("admin1","pivot43\\very1","1pivot43\\very1");
        assertNotNull(user1);
    }
}
