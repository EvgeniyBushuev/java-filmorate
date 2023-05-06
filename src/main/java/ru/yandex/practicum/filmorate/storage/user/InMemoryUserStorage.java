package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private long id = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь ID {}", user.getId());
        return user;
    }

    @Override
    public long delete(long id) {
        if (!users.containsKey(id)) {
            log.warn("Удаление пользователя с несуществующим ID {}", id);
            throw new IncorrectIdException("Использован не существующий ID");
        }

        log.debug("Удален пользователь ID: {}", id);
        users.remove(id);
        return id;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Обновление пользователя с несуществующим ID {}", user.getId());
            throw new IncorrectIdException("Пользователь с ID: " + id + " не найден.");
        }

        users.put(user.getId(), user);
        log.info("Обновлен пользователь ID {}", user.getId());
        return user;
    }

    @Override
    public User get(long id) {
        if (!users.containsKey(id)) {
            log.warn("Получение пользователя с несуществующим ID {}", id);
            throw new IncorrectIdException("Пользователь с ID: " + id + " не найден.");
        }

        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
