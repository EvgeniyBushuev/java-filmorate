package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseFilmValidator implements ConstraintValidator<FilmRelease, LocalDate> {

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        final LocalDate FILM_INDUSTRY_START = LocalDate.of(1895, 12, 28);
        return (localDate.isAfter(FILM_INDUSTRY_START));
    }
}
