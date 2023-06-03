package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getALL() {
        String sql = "SELECT MPA_ID, MPA_NAME" +
                " FROM MPA";
        return jdbcTemplate.query(sql, new RatingMapper());
    }

    @Override
    public Mpa get(long id) {
        String sql = "SELECT MPA_ID, MPA_NAME" +
                " FROM MPA WHERE MPA_ID = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new RatingMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Некореткный идентификатор рейтига. ID в запросе {}", id);
            throw new IncorrectIdException("Некореткный идентификатор рейтига. ID в запросе " + id);
        }
    }

    private class RatingMapper implements RowMapper<Mpa> {
        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getLong("MPA_ID"));
            mpa.setName(rs.getString("MPA_NAME"));

            return mpa;
        }
    }
}
