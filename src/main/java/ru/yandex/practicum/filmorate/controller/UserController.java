package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int id = 0;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUser() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя ID {}, установленно автоматически", user.getId());
        }
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь ID {}", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Обновление пользователя с несуществующим ID {}", user.getId());
            throw new ValidationException("Использован не существующий ID");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя ID {}, установленно автоматически", user.getId());
        }

        users.put(user.getId(), user);
        log.info("Обновлен пользователь ID {}", user.getId());
        return user;
    }
}

