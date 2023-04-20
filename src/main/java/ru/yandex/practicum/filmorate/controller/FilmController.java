package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private int id = 0;
    private Map<Integer, Film> films = new HashMap<>();
    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        id++;
        film.setId(id);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм ID {}", film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Обновление фильма с несуществующим ID {}", film.getId());
            throw new ValidationException();
        }
        films.put(film.getId(), film);
        log.debug("Обновлен фильм ID {}", film.getId());
        return film;
    }
}
