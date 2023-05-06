package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

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
        User user = userStorage.get(userId);

        film.getLikes().add(user.getId());
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.get(filmId);
        User user = userStorage.get(userId);

        film.getLikes().remove(user.getId());
    }

    public List<Film> getTopFilms (long count) {
        return filmStorage.getAll()
                .stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}

