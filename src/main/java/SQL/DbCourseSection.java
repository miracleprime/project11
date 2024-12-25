package SQL;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = "course_sections")
public class DbCourseSection {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private DbCourse course;
    @DatabaseField
    private String title;
    @DatabaseField
    private String tasks; // Сохраняем задачи как одну строку

    public DbCourseSection() {
        // ORMLite требует конструктор по умолчанию
    }

    public DbCourseSection(String title, List<String> tasks) {
        this.title = title;
        this.tasks = String.join(",", tasks);
    }
    public void setCourse(DbCourse course){
        this.course = course;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTasks() {
        return List.of(tasks.split(","));
    }
    public DbCourse getCourse(){return course;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = String.join(",", tasks);
    }
}
