package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long id = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) {
        id++;
        film.setId(id);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм ID {}", film.getId());
        return film;
    }

    @Override
    public long delete(long id) {
        if (!films.containsKey(id)) {
            log.warn("Удаление фильма с несуществующим ID {}", id);
            throw new IncorrectIdException("Использован не существующий ID");
        }

        log.debug("Удален фильм ID: {}", id);
        films.remove(id);
        return id;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Обновление фильма с несуществующим ID {}", film.getId());
            throw new IncorrectIdException("Использован не существующий ID");
        }

        films.put(film.getId(), film);
        log.debug("Обновлен фильм ID {}", film.getId());
        return film;
    }

    @Override
    public Film get(long id) {
        if (!films.containsKey(id)) {
            log.warn("Запрос фильма с несуществующим ID {}", id);
            throw new IncorrectIdException("Использован не существующий ID");
        }

        log.debug("Запрос фильма ID {}", id);
        return films.get(id);
    }

    @Override
    public List<Film> getAll() {
        log.debug("Запрос полного списка фильмов");
        return new ArrayList<>(films.values());
    }
}
