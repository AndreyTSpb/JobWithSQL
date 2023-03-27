package jobToSQL;

public class TGroupSelected extends JobDB{
    private static final String TABLE_NAME = "T_GroupSelected";
    static final String[] COLUMNS = {"id_Student", "firstName", "lastName", "id_Group"}; //список полей таблицы
    /**
     * Создание таблицы T_GroupSelected
     */
    public static void createTableTGroupSelected(){
        // Строка запроса на удаление таблицы
        String queryDropTable = dropTableSQLString(TABLE_NAME);
        // Поля таблицы
        String[] columns = {
                "`" +COLUMNS[0]+ "` INT NOT NULL",
                "`" +COLUMNS[1]+ "` VARCHAR(255) NULL",
                "`" +COLUMNS[2]+ "` VARCHAR(255) NULL",
                "`" +COLUMNS[3]+ "` VARCHAR(255) NULL"
        };
        // Строка запроса на создание таблицы
        String queryCreateTable = createTableSQLString(TABLE_NAME, columns);
        //Очистка и создание таблицы
        updateSQL(new String[]{queryDropTable, queryCreateTable});
    }
    /**
     * Инициализация
     */
    public void initialisation(){
        createTableTGroupSelected();
    }

    public void addByConditionInsertSelect(String idGroup, int dolgCount){
        //Список условий по которуму отбирать строки на добавление
        String[] conditions = {
                String.format("`id_Group` = '%s'", idGroup),
                String.format("`dolgCount` > %d", dolgCount)
        };
        //Чтобы не повторять значения получим айди тех студенто,
        // которые уже есть в таблице
        String[] idStudents = getGroupByItem(COLUMNS[0],TABLE_NAME);
        if(idStudents.length != 0){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < idStudents.length; i++){
                sb.append(idStudents[i]);
                if(i < idStudents.length-1){
                    sb.append(" ,");
                }
            }
            conditions[idStudents.length] = String.format("`id_Student` NOT IN (%s)", sb);
        }
        //так как поля одни и теже в таблицах используем одно и тоже COLUMNS
        final String query = createStringInsertSelectSQL(TABLE_NAME, TStudents.TABLE_NAME, COLUMNS, COLUMNS, conditions);
        updateSQL(new String[]{query});
    }

}
