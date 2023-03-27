import jobToSQL.TGroupSelected;
import jobToSQL.TStudents;

public class Main {
    public static void main(String[] args) {
        System.out.println("Заполнение данных по таблице T_Students:");
        TStudents tStudents = new TStudents();
        tStudents.initialisation(); // Создание и заполнение таблицы T_Students

        System.out.println("Создание таблицы T_GroupSelected");
        TGroupSelected tGroupSelected = new TGroupSelected();
        tGroupSelected.initialisation();

        // Копирование из T_Students в T_GroupSelected с использованием запроса INSERT-SELECT
        tGroupSelected.addByConditionInsertSelect("СПб-1", 2);
    }
}