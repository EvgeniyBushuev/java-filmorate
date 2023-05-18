# FilmorateApplication
_____________
![DB ER Diagram](DB_ERDiagram.PNG)
_____________
### Описание диаграммы

#### User <br>
ID - **PK**, Bigint, Autoincrement, NotNull <br>
Email - Varchar(50) <br>
Login - Varchar(50) <br>
Name - Varchar(50) <br>
Birthday - Date <br>

#### Status <br>
*Status - в значении заявки на дружбу с другим пользователем (request \ friend) <br>

ID - **PK**, Smallint, NotNull <br>
Status - Varchar(50) <br>


#### Film <br>
ID - **PK**, Bigint, Autoincrement, NotNull <br>
Name - Varchar(50), NotNull <br>
Description - Varchar(200) <br>
Release Date - Date <br>
Duration - Smallint <br>
Genre - **FK**, Varchar(50) <br>
Rating - **FK**, Varchar(50) <br>

#### Genre <br>
ID - **PK**, Smallint, NotNull <br>
Genre - Varchar(50), ENUM <br>

#### Rating (MPA) <br>
ID - **PK**, Smallint, NotNull <br>
Rating - Varchar(50), ENUM <br>

