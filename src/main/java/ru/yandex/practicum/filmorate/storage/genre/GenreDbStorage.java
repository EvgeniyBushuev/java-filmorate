package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAll() {
        String sql = "SELECT GENRE_ID, GENRE_NAME FROM GENRES";
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    @Override
    public Genre get(long id) {
        String sql = "SELECT GENRE_ID, GENRE_NAME FROM GENRES WHERE GENRE_ID = ?";

        List<Genre> genre = jdbcTemplate.query(sql, new GenreMapper(), id);

        if (!genre.isEmpty()) {
            return genre.get(0);
        } else {
            log.debug("Некореткный идентификатор жанра. ID в запросе {}", id);
            throw new IncorrectIdException("Некореткный идентификатор жанра. ID в запросе " + id);
        }
    }

    private static class GenreMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre genre = new Genre();
            genre.setId(rs.getLong("GENRE_ID"));
            genre.setName(rs.getString("GENRE_NAME"));

            return genre;
        }
    }
}
