package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

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

    public long deleteUser(long id) {
        return userStorage.delete(id);
    }

    public void addFriend(long id1, long id2) {
        User user1 = userStorage.get(id1);
        User user2 = userStorage.get(id2);

        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());

        log.info("Пользователи с ID {} и {} теперь друзья", user1.getId(), user2.getId());
    }

    public void deleteFriend(long id1, long id2) {

        User user1 = userStorage.get(id1);
        User user2 = userStorage.get(id2);

        if(!user1.getFriends().contains(user2.getId())
                || !user2.getFriends().contains(user1.getId())) {
            log.debug("Попытка удалиться из друзей. Пользователи ID {} и {} не друзья", user1.getId(), user2.getId());
            throw new IncorrectIdException("Пользователи с ID " + user1.getId() + ", " + user2.getId() + " не друзья.");
        }

        user1.getFriends().remove(id2);
        user2.getFriends().remove(id1);
        log.info("Пользователи с ID {} и {} теперь не друзья", user1.getId(), user2.getId());
    }

    public List<User> getCommonFriends(long id1, long id2) {

        List<User> friendsId1 = getFriends(id1);

        List<User> friendsId2 = getFriends(id2);

        friendsId1.retainAll(friendsId2);

        return friendsId1;
    }

    public List<User> getFriends(long id) {
        User user = userStorage.get(id);

        return user.getFriends()
                .stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }
}
