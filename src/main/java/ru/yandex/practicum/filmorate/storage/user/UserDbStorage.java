package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    @Qualifier
    private final FriendsStorage friendsStorage;

    @Override
    public User add(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("USER_EMAIL", user.getEmail());
        values.put("USER_LOGIN", user.getLogin());
        values.put("USER_NAME", user.getName());
        values.put("BIRTHDAY", user.getBirthday());

        Long userId = (Long) insert.executeAndReturnKey(values);

        user.setId(userId);

        return user;
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM USERS WHERE USER_ID = ?";

        if (isUserExists(id)) {
            jdbcTemplate.update(sql, id);
        } else {
            throw new IncorrectIdException("Некоректный идентификатор пользователя. ID в запросе " + id);
        }
    }

    @Override
    public User update(User user) {
        if (isUserExists(user.getId())) {
            jdbcTemplate.queryForObject("SELECT USER_ID FROM USERS WHERE USER_ID = ?", Long.class, user.getId());
        } else {
            log.error("Некоректный идентификатор пользователя. ID в запросе {}", user.getId());
            throw new IncorrectIdException("Некоректный идентификатор пользователя. ID в запросе " + user.getId());
        }

        String updateQuery = "UPDATE USERS SET USER_EMAIL = ?, USER_LOGIN = ?, " +
                "USER_NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";

        jdbcTemplate.update(updateQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public User get(long id) {
        String sql = "SELECT " +
                "USER_ID, " +
                "USER_EMAIL, " +
                "USER_LOGIN, " +
                "USER_NAME, " +
                "BIRTHDAY " +
                "FROM USERS WHERE USER_ID = ?";

        if (isUserExists(id)){
            return jdbcTemplate.queryForObject(sql, new UserMapper(), id);
        } else {
            log.debug("Некоректный идентификатор пользователя в запросе");
            throw new IncorrectIdException("Некорректный идентификатор пользователя в запросе. ID запроса = " + id);
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public boolean isUserExists(long id) {

        String sql = "SELECT COUNT (USER_ID) FROM USERS WHERE USER_ID = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, id);

        if (count == 1 ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<User> getUserFriends(Set<Long> friendsId) {

        return friendsId.stream().map(this::get).collect(Collectors.toList());
    }

    private class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("USER_ID"));
            user.setEmail(rs.getString("USER_EMAIL"));
            user.setLogin(rs.getString("USER_LOGIN"));
            user.setName(rs.getString("USER_NAME"));
            user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());
            user.getFriendsId().addAll(friendsStorage.getFriendsIds(user.getId()));

            return user;
        }
    }
}
