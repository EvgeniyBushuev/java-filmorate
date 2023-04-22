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
    @Email
    private String email;
    @NotBlank
    @NoSpace
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
