package SQL;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "students")
public class DbStudent {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    public DbStudent() {
        // ORMLite требует конструктор по умолчанию
    }

    public DbStudent(String name) {
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
