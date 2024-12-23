package SQL;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "grades")
public class DbGrade {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private DbStudent student;
    @DatabaseField
    private String subject;
    @DatabaseField
    private int grade;

    public DbGrade() {
        // ORMLite требует конструктор по умолчанию
    }

    public DbGrade(DbStudent student, String subject, int grade) {
        this.student = student;
        this.subject = subject;
        this.grade = grade;
    }
    public int getId() {
        return id;
    }
    public DbStudent getStudent() {
        return student;
    }

    public String getSubject() {
        return subject;
    }

    public int getGrade() {
        return grade;
    }

    public void setStudent(DbStudent student) {
        this.student = student;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
