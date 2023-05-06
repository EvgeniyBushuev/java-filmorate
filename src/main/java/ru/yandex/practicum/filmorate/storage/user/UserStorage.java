package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserStorage {
    User add(User user);
    long delete(long id);
    User update(User user);
    User get(long id);
    List<User> getAll();
}
