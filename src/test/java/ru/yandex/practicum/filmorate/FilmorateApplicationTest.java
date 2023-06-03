package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/UserData.sql", "/FilmData.sql", "/MpaData.sql", "/GenreData.sql" })
class FilmorateApplicationTest {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private final FriendsDbStorage friendsStorage;

    @Test
    public void shouldReturnUserById1() {
        assertThat(userStorage.get(1)).hasFieldOrPropertyWithValue("id", 1L);

    }

    @Test
    public void shouldReturnFilmById1() {
        assertThat(filmStorage.get(1)).hasFieldOrPropertyWithValue("id", 1L);

    }

    @Test
    public void shouldAddNewUserWithId4() {
        User user = new User();
        user.setEmail("user4@imail");
        user.setLogin("userLogin4");
        user.setName("John");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        userStorage.add(user);

        assertThat(userStorage.get(4)).hasFieldOrPropertyWithValue("id", 4L);
    }

    @Test
    public void shouldAddNewFilmWitId4() {
        Film film = new Film();
        film.setName("Фильм4");
        film.setDescription("Описание фильма4");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000,5,29));
        film.setMpa(mpaStorage.get(1));

        filmStorage.add(film);

        assertThat(filmStorage.get(4)).hasFieldOrPropertyWithValue("id", 4L);
    }

    @Test
    public void shouldUpdateUserName() {
        User user = userStorage.get(1);

        user.setEmail("user4@imail");
        user.setLogin("userLogin4");
        user.setName("John");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        userStorage.update(user);

        assertThat(userStorage.get(1)).hasFieldOrPropertyWithValue("name", "John");
    }

    @Test
    public void shouldUpdateFilmDescription() {
        Film film = filmStorage.get(1);

        film.setName("Фильм4");
        film.setDescription("Новое описание фильма4");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000,5,29));
        film.setMpa(mpaStorage.get(1));

        filmStorage.update(film);

        assertThat(filmStorage.get(1)).hasFieldOrPropertyWithValue("description", "Новое описание фильма4");
    }

    @Test
    public void shouldAddFriend() {

        friendsStorage.addToFriends(1,2);

        assertThat(friendsStorage.getFriendsIds(1)).asList().hasSize(1);

    }

}