package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("likesDbStorage")
    private final LikesStorage likesStorage;

    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film getFilm(long id) {
        return filmStorage.get(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public long deleteFilm(long id) {
        return filmStorage.delete(id);
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.get(filmId);

        if (userStorage.isUserExists(userId)) {
            film.getLikes().add(userId);
            likesStorage.addLike(userId, filmId);
            log.debug("Пользователь ID: {}, поставил лайк фильму ID: {}", userId, filmId);
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.get(filmId);

        if (userStorage.isUserExists(userId)) {
            film.getLikes().remove(userId);
            likesStorage.deleteLike(userId, filmId);
            log.debug("Пользователь ID: {}, удалил лайк фильму ID: {}", userId, filmId);
        } else {
            throw new IncorrectIdException("Пользоваетль ID: " + userId + " не найден");
        }
    }

    public List<Film> getTopFilms(long count) {
        return likesStorage.getTopFilmsId(count).stream()
                .map(filmStorage::get)
                .collect(Collectors.toList());
    }
}

