package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Film getNewFilm() {
        Film film = new Film();
        film.setName("Зеленая Миля");
        film.setDescription("В тюрьме для смертников появляется заключенный с божественным даром. Мистическая драма по роману Стивена Кинга");
        film.setReleaseDate(LocalDate.of(1999, 12, 6));
        film.setDuration(189);
        return film;
    }

    @Test
    void shouldPassValidationWithValidFilm() {
        Film film = getNewFilm();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldNotPassValidationIfFilmTitleIsBlank() {
        Film film = getNewFilm();
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", errorMessage.get(0));
    }

    @Test
    void shouldNotPassValidationIfDescriptionIsMoreThan200Symbols() {
        Film film = getNewFilm();

        film.setDescription("!".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertEquals(1, violations.size());
        assertEquals("Описание не должно быть больше 200 символов", errorMessage.get(0));
    }

    @Test
    void shouldNotPassValidationIfDateIsLessThanFilmIndustryStarted() {
        LocalDate filmIndustryStart = LocalDate.of(1895, 12, 28);

        Film film = getNewFilm();

        film.setReleaseDate(LocalDate.of(1895, 11, 28));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        List<String> errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertEquals(1, violations.size());
        assertEquals("Дата не может быть раньше, чем 28.12.1985", errorMessage.get(0));
    }
}