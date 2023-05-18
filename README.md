# FilmorateApplication
_____________
![DB ER Diagram](DB_ERDiagram.PNG)
_____________
## Описание диаграммы

### User - пользователь <br>
ID - **PK**, Bigint, Autoincrement, NotNull <br>
Email - Varchar(50) <br>
Login - Varchar(50) <br>
Name - Varchar(50) <br>
Birthday - Date <br>

### Status - взимоотношение двух пользователей <br>
*Status - в значении заявки на дружбу с другим пользователем (request \ friend) <br>

ID - **PK**, Smallint, NotNull <br>
Status - Varchar(50) <br>


### Film - фильм<br>
ID - **PK**, Bigint, Autoincrement, NotNull <br>
Name - Varchar(50), NotNull <br>
Description - Varchar(200) <br>
Release Date - Date <br>
Duration - Smallint <br>
Genre - **FK**, Varchar(50) <br>
Rating - **FK**, Varchar(50) <br>

### Genre - жанр фильма <br>
ID - **PK**, Smallint, NotNull <br>
Genre - Varchar(50), ENUM <br>

### Rating (MPA) - возрастной рейтинг <br>
ID - **PK**, Smallint, NotNull <br>
Rating - Varchar(50), ENUM <br>

### UserFriendship - взаимоотношение пользователей в формате заявки на добавление в друзья
From User ID - **PK1**, составной ключ, инициатор запроса <br>
To User ID - **PK2**, составной ключ <br>
Status ID - **FK**, статус запроса (wait \ friend) <br>

### Film_Genre - соотношение фильма и его жанра/ов
Film ID - **PK** <br>
Genre ID - **FK** <br>

### User_Film_Likes - соотношение лайков от пользователей к фильмам
User ID - **PK1**, составной ключ <br>
Film ID - **PK2**, составной ключ <br>



