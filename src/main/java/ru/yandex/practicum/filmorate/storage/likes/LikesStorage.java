package ru.yandex.practicum.filmorate.storage.likes;

import java.util.Collection;

public interface LikesStorage {
    void addLike(long userId, long filmId);

    void deleteLike(long userId, long filmId);

    Collection<Long> getTopFilmsId(long count);

    Collection<Long> getLikedUsersId(long filmId);
}
