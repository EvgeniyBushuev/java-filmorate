package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Set;

public interface UserStorage {

    User add(User user);

    void delete(long id);

    User update(User user);

    User get(long id);

    List<User> getAll();

    boolean isUserExists(long id);

    List<User> getUserFriends(Set<Long> friendsId);
}
