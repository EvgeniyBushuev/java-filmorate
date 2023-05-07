package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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
        log.info("Добавлен новый пользователь ID {}", user.getId());
        return user;
    }

    @Override
    public long delete(long id) {

        if (isUserExists(id)) {
            log.debug("Удален пользователь ID: {}", id);
            users.remove(id);
        }
        return id;
    }

    @Override
    public User update(User user) {

        if (isUserExists(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь ID {}", user.getId());
        }
        return user;
    }

    @Override
    public User get(long id) {

        if (isUserExists(id)) {
            return users.get(id);
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean isUserExists(long id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь ID {}, не найден", id);
            throw new IncorrectIdException("Пользователь с ID: " + id + " не найден.");
        }
        return true;
    }

    @Override
    public List<User> getUserFriends(Set<Long> friendsId) {
        return friendsId.stream()
                .map(this::get)
                .collect(Collectors.toList());
    }
}
