package SQL;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.List;

@DatabaseTable(tableName = "courses")
public class DbCourse {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;

    public DbCourse() {
        // ORMLite требует конструктор по умолчанию
    }

    public DbCourse(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

