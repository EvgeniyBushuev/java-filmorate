package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private User getNewUser() {
        User user = new User();
        user.setEmail("example@yandex.ru");
        user.setLogin("ЮзерЛогин");
        user.setName("Евгений Бушуев");
        user.setBirthday(LocalDate.of(1994, 8, 30));
        return user;
    }

    @Test
    void shouldPassValidationWithValidUser() {
        User user = getNewUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldNotPassValidationIfUserEmailIsIncorrect() {
        User user = getNewUser();
        user.setEmail("wrongEmail.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

    }

    @Test
    void shouldNotPassValidationIfLoginIsBlancOrContainsSpaces() {
        User user = getNewUser();
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        user.setLogin("User Login");

        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldNotPassValidationIfUserBirthDateIsAfterThanCurrentTime() {
        User user = getNewUser();
        user.setBirthday(LocalDate.MAX);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

}