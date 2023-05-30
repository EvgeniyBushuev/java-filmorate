package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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

        String sql = "SELECT COUNT (FILM_ID) FROM FILM WHERE FILM_ID = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, film.getId());

        if (count == 1) {
            String update = "UPDATE FILM SET FILM_NAME = ?, " +
                    "FILM_DESCRIPTION = ?, FILM_RELEASE = ?, FILM_DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";

            jdbcTemplate.update(update,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

            updateGenresFilmTable(film);
            return film;
        } else {
            log.debug("Некоретный идентификатор фильма. ID запроса {}", film.getId());
            throw new IncorrectIdException("Некоретный идентификатор фильма. ID запроса " + film.getId());
        }
    }

    @Override
    public Film get(long id) {
        String sql = "SELECT FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE, FILM_DURATION, MPA_ID" +
                " FROM FILM WHERE FILM_ID = ?";

        List<Film> film = jdbcTemplate.query(sql, new FilmMapper(), id);

        if (!film.isEmpty()) {
            return film.get(0);
        } else {
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

        String insert = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (? , ?)";

        List<Long> genres = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        jdbcTemplate.batchUpdate(insert, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, genres.get(i));
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }
}
