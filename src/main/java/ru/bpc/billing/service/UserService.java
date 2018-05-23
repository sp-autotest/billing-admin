package ru.bpc.billing.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.domain.UserAction;
import ru.bpc.billing.domain.UserHistory;
import ru.bpc.billing.repository.UserHistoryRepository;
import ru.bpc.billing.repository.UserRepository;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * User: Krainov
 * Date: 19.09.2014
 * Time: 16:29
 */
@Service
public class UserService implements UserDetailsService {

    @Resource
    private UserRepository userRepository;
    @Resource
    private UserHistoryRepository userHistoryRepository;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Value("${user.passwordMask}")
    private String passwordMask="^(.*?)$";
    @Value("${user.deepPasswordHistory}")
    private Integer deepPasswordHistory = 10;
    @Value("${user.dayToExpiredPassword}")
    private Integer dayToExpiredPassword = 10;
    @Value("${user.dayToChangePassword}")
    private Integer dayToChangePassword = 1;
    @Value("${user.countInvalidCredentials}")
    private Integer countInvalidCredentials;
    public static final String PASSWORD_HISTORY_DELIMITER = ";";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Map<String, Integer> invalidPassword = new HashMap<>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        UserHistory userHistory = new UserHistory();
        userHistory.setNewValue(username);
        userHistory.setAction(UserAction.LOGON);
        if ( null != user ) {
            userHistory.setUser(user);
            userHistory.setStatus(true);
        }
        userHistory.setStatus(false);
        userHistoryRepository.save(userHistory);
        if ( null == user)
            throw new UsernameNotFoundException("Пользователь с логином '" + username + "' не найден.");
        return user;
    }

    public boolean unlockUser(User user) {
        if ( user.isLocked() ) {
            user.setLocked(false);
            userRepository.save(user);
            clearInvalidTries(user.getUsername());
            logger.debug("User with username: '{}' was successfully unlocked.");
            return true;
        }
        return false;
    }

    public boolean clearInvalidTries(String username) {
        if ( invalidPassword.containsKey(username) ) {
            invalidPassword.remove(username);
            return true;
        }
        return false;
    }

    public boolean lockUser(String username) {
        User user = userRepository.findByUsername(username);
        if ( null == user ) throw new UsernameNotFoundException(username);
        if ( user.isLocked() ) return false;
        user.setLocked(true);
        userRepository.save(user);
        return true;
    }

    public User create(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setUpdatedAt(new Date());
        String encodedPassword = checkNewPasswordAndEncode(user, password);
        user.setPassword(encodedPassword);
        addPasswordToHistory(user,encodedPassword);
        updateExpiredParams(user);
        user.setEnabled(true);

        userRepository.save(user);

        UserHistory userHistory = new UserHistory();
        userHistory.setAction(UserAction.CREATE_USER);
        userHistory.setCreatedAt(new Date());
        userHistory.setNewValue(username);
        userHistory.setUser(user);
        userHistoryRepository.save(userHistory);

        return user;
    }

    public User changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username);
        if ( null == user ) throw new UsernameNotFoundException("Неизвестный пользователь : '" + username + "'");
        Calendar allowUpdatedChangePassword = Calendar.getInstance();
        allowUpdatedChangePassword.setTime(user.getUpdatedAt());
        allowUpdatedChangePassword.add(Calendar.DAY_OF_MONTH,dayToChangePassword);
        Calendar allowDateToChangePassword = Calendar.getInstance();
        allowDateToChangePassword.add(Calendar.DAY_OF_MONTH,-dayToChangePassword);
        Calendar updatedCalendar = Calendar.getInstance();
        updatedCalendar.setTime(user.getUpdatedAt());
        updatedCalendar.add(Calendar.DAY_OF_MONTH,dayToChangePassword);
        if ( updatedCalendar.getTime().after(new Date()) ) {
            throw new AuthenticationServiceException("Пароль можно будет изменить : " + dateFormat.format(allowUpdatedChangePassword.getTime()));
        }
        String password = user.getPassword();
        boolean isOldPasswordCorrect = passwordEncoder.matches(oldPassword,password);
        if ( !isOldPasswordCorrect ) throw new BadCredentialsException("Оригинальный пароль неверный");
        String encodedPassword = checkNewPasswordAndEncode(user, newPassword);
        user.setPassword(encodedPassword);
        addPasswordToHistory(user,encodedPassword);
        updateExpiredParams(user);

        userRepository.save(user);

        UserHistory userHistory = new UserHistory();
        userHistory.setAction(UserAction.PASSWORD_CHANGE);
        userHistory.setOldValue(password);
        userHistory.setNewValue(user.getPassword());
        userHistory.setUser(user);
        userHistoryRepository.save(userHistory);

        return user;
    }

    public boolean checkForLock(String username) {
        Integer count = increment(username);
        logger.debug("Bad credentials for '{}' {} times ",username,count);
        return lockedIfMore(username);
    }

    public boolean logout(User user, boolean status) {
        UserHistory userHistory = new UserHistory();
        userHistory.setAction(UserAction.LOGOUT);
        if ( null != user ) {
            userHistory.setOldValue(user.getUsername());
            userHistory.setUser(user);
        }
        userHistory.setStatus(status);
        userHistoryRepository.save(userHistory);
        return true;
    }

    private Integer increment(String username) {
        synchronized (username.intern() ) {
            Integer count = 0;
            if ( invalidPassword.containsKey(username) ) {
                count = invalidPassword.get(username);
            }
            count++;
            invalidPassword.put(username,count);
            return count;
        }
    }


    private boolean lockedIfMore(String username) {
        synchronized (username.intern()) {
            if ( invalidPassword.get(username) >= countInvalidCredentials ) {
                return lockUser(username);
            }
        }
        return false;
    }

    protected String checkNewPasswordAndEncode(User user, String newPassword) {
        if ( !Pattern.compile(passwordMask).matcher(newPassword).matches() ) {
            throw new BadCredentialsException("Пароль неудовлетворяет условиям безопасности");
        }
        String passwords = user.getPasswordHistory();
        if (StringUtils.isNotBlank(passwords) ) {
            for (String pass : passwords.split(";")) {
                if ( passwordEncoder.matches(newPassword,pass) ) throw new BadCredentialsException("Новый пароль уже был ранее установлен. Выберите новый пароль.");
            }
        }
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        return encodedNewPassword;
    }

    protected void addPasswordToHistory(User user, String newPassword) {
        if ( StringUtils.isNotBlank(user.getPasswordHistory()) ) {
            String[] passArray = user.getPasswordHistory().split(PASSWORD_HISTORY_DELIMITER);
            List<String> passList = new ArrayList<>(Arrays.asList(passArray));
            if ( passList.size() >= deepPasswordHistory) {
                List<String> cutPassList = passList.subList(0,deepPasswordHistory-1);
                cutPassList.add(0,newPassword);
                listPasswordsToString(user,cutPassList);
            }
            else {
                passList.add(0,newPassword);
                listPasswordsToString(user,passList);
            }
        }
        else {
            user.setPasswordHistory(newPassword + PASSWORD_HISTORY_DELIMITER);
        }
    }

    protected void updateExpiredParams(User user) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH,dayToExpiredPassword);
        user.setCredentialsExpiredAt(now.getTime());
    }

    private void listPasswordsToString(User user, List<String> passwords) {
        StringBuilder sb = new StringBuilder();
        for (String password : passwords) {
            sb.append(password).append(PASSWORD_HISTORY_DELIMITER);
        }
        user.setPasswordHistory(sb.toString());
    }
}
