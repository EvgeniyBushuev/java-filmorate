package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    @Qualifier
    private final MpaStorage mpaStorage;
    @Qualifier
    private final GenreStorage genreStorage;
    @Qualifier
    private final LikesStorage likesStorage;

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILM")
                .usingGeneratedKeyColumns("FILM_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("FILM_NAME", film.getName());
        values.put("FILM_DESCRIPTION", film.getDescription());
        values.put("FILM_RELEASE", film.getReleaseDate());
        values.put("FILM_DURATION", film.getDuration());
        values.put("MPA_ID", film.getMpa().getId());

        Long filmId = (Long) insert.executeAndReturnKey(values);

        film.setId(filmId);

        updateGenresFilmTable(film);

        return film;
    }

    @Override
    public long delete(long id) {
        String sql = "DELETE FROM FILM WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    @Override
    public Film update(Film film) {
        try {
            String sql = "SELECT FILM_ID FROM FILM WHERE FILM_ID = ?";
            jdbcTemplate.queryForObject(sql, Long.class, film.getId());
        } catch (EmptyResultDataAccessException e) {
            log.debug("Некоретный идентификатор фильма. ID запроса {}", film.getId());
            throw new IncorrectIdException("Некоретный идентификатор фильма. ID запроса " + film.getId());
        }

        String sql = "UPDATE FILM SET FILM_NAME = ?, " +
                "FILM_DESCRIPTION = ?, FILM_RELEASE = ?, FILM_DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateGenresFilmTable(film);

        return film;
    }

    @Override
    public Film get(long id) {
        String sql = "SELECT FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE, FILM_DURATION, MPA_ID" +
                " FROM FILM WHERE FILM_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new FilmMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Некоректный идентификатор фильма. ID в запросе {}.", id);
            throw new IncorrectIdException("Некоректный идентификатор фильма. ID в запросе {}. " + id);
        }
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE, FILM_DURATION, MPA_ID" +
                " FROM FILM";
        return jdbcTemplate.query(sql, new FilmMapper());
    }

    private class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getLong("FILM_ID"));
            film.setName(rs.getString("FILM_NAME"));
            film.setDescription(rs.getString("FILM_DESCRIPTION"));
            film.setReleaseDate(rs.getDate("FILM_RELEASE").toLocalDate());
            film.setDuration(rs.getInt("FILM_DURATION"));
            film.setMpa(mpaStorage.get(rs.getLong("MPA_ID")));
            film.getLikes().addAll(likesStorage.getLikedUsersId(rs.getLong("FILM_ID")));

            for (Long genreId : getFilmGenresId(rs.getLong("FILM_ID"))) {
                film.getGenres().add(genreStorage.get(genreId));
            }

            return film;
        }
    }

    public List<Long> getFilmGenresId(long filmId) {
        String sql = "SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?";

        return jdbcTemplate.queryForList(sql, Long.class, filmId);
    }

    public void updateGenresFilmTable(Film film) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());

        String insert = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(insert, film.getId(), genre.getId());
        }
    }
}
