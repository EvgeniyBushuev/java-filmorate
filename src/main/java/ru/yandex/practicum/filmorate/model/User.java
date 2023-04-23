package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validation.NoSpace;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode
public class User {
    private int id;
    @Email(message = "Email должен быть корректный xxx@example.com")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @NoSpace(message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем времени")
    private LocalDate birthday;
}
