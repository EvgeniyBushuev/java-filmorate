package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseFilmValidator implements ConstraintValidator<FilmRelease, LocalDate> {

    private final LocalDate FILM_INDUSTRY_START = LocalDate.of(1895, 12, 28);
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return (localDate.isAfter(FILM_INDUSTRY_START));
    }
}
