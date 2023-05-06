package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    long delete(long id);

    Film update(Film film);

    Film get(long id);

    List<Film> getAll();
}
