package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addToFriends(long userId, long friendId) {
        String sql = "INSERT INTO FRIENDSHIP (FROM_USER, TO_USER) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFromFriends(long userId, long friendId) {
        String sql = "DELETE FROM FRIENDSHIP WHERE FROM_USER = ? AND TO_USER = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }


    @Override
    public Collection<Long> getFriendsIds(long userId) {
        String sql = "SELECT TO_USER FROM FRIENDSHIP WHERE FROM_USER = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }

    @Override
    public Collection<Long> getCommonFriendsIds(long userId, long friendId) {
        String sql = "SELECT TO_USER FROM FRIENDSHIP WHERE FROM_USER = ? AND TO_USER IN " +
                "(SELECT TO_USER FROM FRIENDSHIP WHERE FROM_USER = ?)";
        return jdbcTemplate.queryForList(sql, Long.class, userId, friendId);
    }
}