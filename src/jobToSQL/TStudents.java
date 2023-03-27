package jobToSQL;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TStudents extends JobDB{
    public static final String TABLE_NAME = "T_Student";
    static final String[] COLUMNS = {"id_Student", "firstName", "lastName", "id_Group", "dolgCount"}; //список полей таблицы
    /**
     * Создание таблицы T_Student
     */
    private static void createTableTStudent(){
        //Drop Table
        String queryDropTable = dropTableSQLString(TABLE_NAME);
        //Create Table
        String[] columns = {
                "`" +COLUMNS[0]+ "` INT NOT NULL AUTO_INCREMENT",
                "`" +COLUMNS[1]+ "` VARCHAR(255) NULL",
                "`" +COLUMNS[2]+ "` VARCHAR(255) NULL",
                "`" +COLUMNS[3]+ "` VARCHAR(255) NULL",
                "`" +COLUMNS[4]+ "` INT NULL",
                "PRIMARY KEY (`"+COLUMNS[0]+"`)"
        };
        String queryCreateTable = createTableSQLString(TABLE_NAME, columns);
        //Очистка и создание таблицы
        updateSQL(new String[]{queryDropTable,queryCreateTable});
    }

    /**
     * Заполение таблици исходными данными
     */
    private static void insertStartStringInTable(){
        //Создается массив строк
        String[] rows = {
                createStringInsertSQL(TABLE_NAME, COLUMNS, new String[]{"NULL", "Василий", "Пупкин", "СПб-1", "1"}),
                createStringInsertSQL(TABLE_NAME, COLUMNS, new String[]{"NULL", "Петр", "Васильев", "СПб-1", "3"}),
                createStringInsertSQL(TABLE_NAME, COLUMNS, new String[]{"NULL", "Иван", "Петров", "СПб-2", "3"}),
                createStringInsertSQL(TABLE_NAME, COLUMNS, new String[]{"NULL", "Степан", "Андреев", "СПб-1", "2"}),
                createStringInsertSQL(TABLE_NAME, COLUMNS, new String[]{"NULL", "Андрей", "Степанов", "СПб-2", "2"}),
                createStringInsertSQL(TABLE_NAME, COLUMNS, new String[]{"NULL", "Василий", "Степанов", "СПб-2", "3"})
        };
        updateSQL(rows); //отправляются строки в БД

    }

    /**
     * Инициализация
     */
    public void initialisation(){
        createTableTStudent();
        insertStartStringInTable();
    }
}
