package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.RemoveFriendException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Qualifier("friendsDbStorage")
    private final FriendsStorage friendsStorage;

    public List<User> getUsers() {
        return userStorage.getAll();
    }

    public User getUser(long id) {
        return userStorage.get(id);
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя ID {}, установленно автоматически", user.getId());
        }

        return userStorage.add(user);
    }

    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя ID {}, установленно автоматически", user.getId());
        }

        return userStorage.update(user);
    }

    public void deleteUser(long id) {
        userStorage.delete(id);
    }

    public void addFriend(long id1, long id2) {
        if ((getUsers().stream().noneMatch(user -> user.getId() == id2))) {
            log.debug("Пользователь ID {}, не найден", id2);
            throw new IncorrectIdException("Пользователь с ID " + id2);
        }
        friendsStorage.addToFriends(id1, id2);
        log.info("Пользователи с ID {} и {} теперь друзья", id1, id2);
    }

    public void deleteFriend(long id1, long id2) {

        if ((getUsers().stream().noneMatch(user -> user.getId() == id1))
                || (getUsers().stream().noneMatch(user -> user.getId() == id2))) {
            log.warn("Попытка удалиться из друзей. Пользователи ID {} и {} не друзья", id1, id2);
            throw new RemoveFriendException("Пользователи с ID " + id1
                    + ", " + id2 + " не друзья.");
        }

        friendsStorage.deleteFromFriends(id1, id2);
        log.info("Пользователи с ID {} и {} теперь не друзья", id1, id2);
    }

    public List<User> getCommonFriends(long id1, long id2) {

        return friendsStorage.getCommonFriendsIds(id1, id2)
                .stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    public List<User> getFriends(long id) {
        Set<Long> friendsId = new TreeSet<>(friendsStorage.getFriendsIds(id));

        return userStorage.getUserFriends(friendsId);
    }
}
