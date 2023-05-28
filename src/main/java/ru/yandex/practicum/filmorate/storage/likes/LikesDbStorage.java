package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(long userId, long filmId) {
        String sql = "INSERT INTO USER_FILM_LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void deleteLike(long userId, long filmId) {
        String sql = "DELETE FROM USER_FILM_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public Collection<Long> getTopFilmsId(long count) {
        String sql = "SELECT F.FILM_ID FROM FILM F" +
                " LEFT JOIN USER_FILM_LIKES AS T ON F.FILM_ID = T.FILM_ID" +
                " GROUP BY F.FILM_ID" +
                " ORDER BY COUNT(T.USER_ID) DESC" +
                " LIMIT ?";
        return jdbcTemplate.queryForList(sql, Long.class, count);
    }

    @Override
    public Collection<Long> getLikedUsersId(long filmId) {
        String sql = "SELECT USER_ID FROM USER_FILM_LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.queryForList(sql, Long.class, filmId);
    }
}
