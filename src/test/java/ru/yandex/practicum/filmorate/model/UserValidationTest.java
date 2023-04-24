package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<String> errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertEquals(1, violations.size());
        assertEquals("Email должен быть корректный xxx@example.com", errorMessage.get(0));
    }

    @Test
    void shouldNotPassValidationIfLoginIsBlanc() {
        User user = getNewUser();
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> errorMessage1 = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertEquals(1, violations.size());
        assertEquals("Логин не может быть пустым", errorMessage1.get(0));

    }

    @Test
    void shouldNotPassValidationIfLoginContainsSpaces() {
        User user = getNewUser();

        user.setLogin("User Login");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertEquals("Логин не может содержать пробелы", errorMessage.get(0));
        assertEquals(1, violations.size());
    }

    @Test
    void shouldNotPassValidationIfUserBirthDateIsAfterThanCurrentTime() {
        User user = getNewUser();
        user.setBirthday(LocalDate.MAX);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем времени", errorMessage.get(0));
    }

}