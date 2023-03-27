package jobToSQL;

import java.sql.*;
import java.util.*;

public class JobDB {
    // JDBC URL для подключения к MySQL
    private static final String BD_NAME = "test_bd";
    private static final String URL = "jdbc:mysql://localhost:3306/";
    // Пользователь
    private static final String USER = "root";
    // Пасворд
    private static final String PASSWORD = "root";

    // JDBC переменные для открытия и управления соединением
    // Строка соединения
    private static Connection con;
    //
    private static Statement stmt;
    protected static ResultSet rs;

    /**
     * Строка для создания таблицы
     * @param tableName - Имя таблицы
     * @param columns - Поля таблицы
     * @return - возвращает строку
     */
    protected static String createTableSQLString(String tableName, String[] columns){
        StringBuilder sbColumns = new StringBuilder();
        for (int i= 0; i < columns.length; i++){
            sbColumns.append(columns[i]);
            if(i < columns.length-1){
                sbColumns.append(" ,"); //Добавляем разделитель
            }
        }
        return String.format("CREATE TABLE IF NOT EXISTS `%s`.`%s` (%s) ENGINE = InnoDB CHARSET=utf8 COLLATE utf8_general_ci;\n", BD_NAME, tableName, sbColumns);
    }

    /**
     * Удаления таблицы если она существует
     * @param tableName - имя таблицы
     * @return - обработанная строка запроса
     */
    protected static String dropTableSQLString(String tableName){
        return String.format("DROP TABLE IF EXISTS `%s`.`%s`;\n", BD_NAME, tableName);
    }

    /**
     * Создание запроса INSERT добавления данных в БД
     * @param tableName - название таблицы
     * @param columns - список ячеик куда будут вставлятся данные
     * @param values - список значений для ячеек
     * @return возврашает строку запроса
     */
    protected static String createStringInsertSQL(String tableName, String[] columns, String[] values){
        StringBuilder sbColumns = new StringBuilder();
        StringBuilder sbValues = new StringBuilder();
        for (int i = 0; i < columns.length; i++){
            sbColumns.append(columns[i]);
            if(!Objects.equals(values[i], "NULL")) sbValues.append("'");
            sbValues.append(values[i]);
            if(!Objects.equals(values[i], "NULL")) sbValues.append("'");
            if(i < columns.length-1){
                sbColumns.append(" ,"); //Добавляем разделитель
                sbValues.append(" ,");
            }
        }
        return String.format("INSERT INTO `%s`.`%s` (%s) VALUES (%s);\n", BD_NAME, tableName, sbColumns, sbValues);
    }

    /**
     * Создание запроса INSERT-SELECT в БД для добавления записей из одной таблицы в другую
     * @param tableNameIn - имя таблицы куда вставляются данные
     * @param tableNameOut - имя таблицы откуда берутся данные
     * @param columnsIn - поля в которые вставляем данные
     * @param columnsOut - поля из которых вставляем данные
     * @param conditions - условия отбора
     * @return строка запроса
     */
    protected static String createStringInsertSelectSQL(String tableNameIn, String tableNameOut, String[] columnsIn, String[] columnsOut, String[] conditions){
        //INSERT INTO `GroupSelected` (`id_Student`, `firstName`, `lastName`, `id_Group`) SELECT `id_Student`, `firstName`, `lastName`, `id_Group` FROM Student;
        StringBuilder sbColumnsIn = new StringBuilder();
        StringBuilder sbColumnsOut = new StringBuilder();
        for (int i = 0; i < columnsIn.length; i++){
            sbColumnsIn.append("`").append(columnsIn[i]).append("`");
            sbColumnsOut.append("`").append(columnsOut[i]).append("`");
            if(i < columnsIn.length-1){
                sbColumnsIn.append(" ,"); //Добавляем разделитель
                sbColumnsOut.append(" ,");
            }
        }
        final String insert = String.format("INSERT INTO `%s`.`%s` (%s) \n", BD_NAME, tableNameIn, sbColumnsIn);
        final String select = String.format("SELECT %s FROM `%s` \n",sbColumnsOut, tableNameOut);

        //Обрабатываем условие
        final String where;
        if(conditions.length != 0){
            StringBuilder sbConditions = new StringBuilder();
            for (int i = 0; i < conditions.length; i++){
                sbConditions.append(conditions[i]);
                if(i < conditions.length-1){
                    sbConditions.append(" AND ");
                }
            }
            where  = String.format("WHERE %s \n",sbConditions);
        }else{
            where = "";
        }
        // Возвращаем объединенную строку
        return insert + select + where;
    }

    /**
     * Выполняет запросы DROP/CREATE/UPDATE/INSERT
     * переданные списком
     * @param querys - список запросов в БД.
     */
    protected static void updateSQL(String[] querys){
        try {
            // Подключение к БД
            con = DriverManager.getConnection(URL+BD_NAME, USER, PASSWORD);

            // getting Statement object to execute query
            stmt = con.createStatement();
            // Добавление списка запросов к БД
            for (String query: querys){
                stmt.addBatch(query);
                System.out.println(query);
            }
            // Выполение списка запросов
            stmt.executeBatch();

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //закрываем подключение к БД
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }

    /**
     * Запрос на получения данных из БД
     * @param query - строка запроса
     */
    protected static List<Map<String, String>> selectSQL(String query){
        List <Map<String, String>> result = new ArrayList<>();
        try {
            // создаем подключение к БД
            con = DriverManager.getConnection(URL+BD_NAME, USER, PASSWORD);
            stmt = con.createStatement();

            // Выполняем запрос
            rs = stmt.executeQuery(query);

            // Количество колонок в результирующем запросе
            int columns = rs.getMetaData().getColumnCount();
            // Перебор строк с данными
            while(rs.next()){
                Map<String, String> col = new HashMap<>();
                for (int i = 1; i <= columns; i++){
                    col.put(rs.getMetaData().getColumnName(i), rs.getString(i));
                }
                result.add(col);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
        return  result;
    }

    /**
     * Получаем уникальные значения из таблицы
     * @param column - колонка по которой группируем
     * @return - возвращает массив с уникальными значениями
     */
    public String[] getGroupByItem(String column, String tableName){
        //SELECT id_Group FROM `T_Student` GROUP BY id_Group;
        final String query = String.format("SELECT `%2$s` FROM `%1$s` GROUP BY `%2$s`;", tableName, column);
        List<Map<String, String>> result = selectSQL(query);
        // Массив с найдеными уникальными значениями
        String[] groupByItems = new String[result.size()];
        for (int i=0; i<result.size(); i++){
            groupByItems[i] = result.get(i).get(column);
        }
        return groupByItems;
    }
}
