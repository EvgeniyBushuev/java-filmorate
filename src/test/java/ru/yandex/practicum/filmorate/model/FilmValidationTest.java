package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

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
        assertEquals(1, violations.size());
    }

    @Test
    void shouldNotPassValidationIfDescriptionIsMoreThan200Symbols() {
        Film film = getNewFilm();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("!".repeat(201));
        film.setDescription(stringBuilder.toString());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());

    }

    @Test
    void shouldNotPassValidationIfDateIsLessThan28december1985() {
        Film film = getNewFilm();

        film.setReleaseDate(LocalDate.of(1895, 11, 28));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());

    }
}