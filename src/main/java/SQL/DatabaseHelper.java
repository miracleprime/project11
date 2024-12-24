package SQL;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private static final String DATABASE_URL = "jdbc:sqlite:final411123!fin.db";

    private ConnectionSource connectionSource;
    private Dao<DbCourse, Integer> courseDao;
    private Dao<DbGrade, Integer> gradeDao;
    private Dao<DbStudent, Integer> studentDao;
    private Dao<DbUser, Integer> userDao;
    private Dao<DbCourseSection, Integer> sectionDao;


    public DatabaseHelper() throws SQLException {
        connectionSource = new JdbcConnectionSource(DATABASE_URL);
        setupDatabase();
        courseDao = DaoManager.createDao(connectionSource, DbCourse.class);
        gradeDao = DaoManager.createDao(connectionSource, DbGrade.class);
        studentDao = DaoManager.createDao(connectionSource, DbStudent.class);
        userDao = DaoManager.createDao(connectionSource,DbUser.class);
        sectionDao = DaoManager.createDao(connectionSource, DbCourseSection.class);

    }

    private void setupDatabase() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DbCourse.class);
        TableUtils.createTableIfNotExists(connectionSource, DbGrade.class);
        TableUtils.createTableIfNotExists(connectionSource, DbStudent.class);
        TableUtils.createTableIfNotExists(connectionSource, DbUser.class);
        TableUtils.createTableIfNotExists(connectionSource, DbCourseSection.class);
    }
    public void saveCoursesList(List<String> courseNames) throws SQLException {
        System.out.println("Saving courses list: " + courseNames);
        for (String courseName : courseNames) {
            DbCourse dbCourse = new DbCourse(courseName);
            try {
                DbCourse existingCourse = courseDao.queryForMatching(dbCourse).stream().findFirst().orElse(null);
                if(existingCourse == null) {
                    courseDao.create(dbCourse);
                    System.out.println("Course created with ID: " + dbCourse.getId() + ", name: " + dbCourse.getName());
                }
                else {
                    System.out.println("Course exists with ID: " + existingCourse.getId() + ", name: " + existingCourse.getName());
                }
            }
            catch (SQLException e) {
                System.err.println("Error saving course data: " + e.getMessage());
            }
        }
        System.out.println("Finished saving courses.");
    }
    public void saveCourses(Map<String, List<DbCourseSection>> courseData) throws SQLException {
        System.out.println("Saving courses with data: " + courseData);
        for (Map.Entry<String, List<DbCourseSection>> entry : courseData.entrySet()) {
            String courseName = entry.getKey();
            List<DbCourseSection> sections = entry.getValue();
            DbCourse dbCourse = new DbCourse(courseName);
            try {
                DbCourse existingCourse = courseDao.queryForMatching(dbCourse).stream().findFirst().orElse(null);
                if(existingCourse == null) {
                    courseDao.create(dbCourse);
                    existingCourse = courseDao.queryForMatching(dbCourse).stream().findFirst().orElse(null);
                    System.out.println("Course created with ID: " + existingCourse.getId() + ", name: " + existingCourse.getName());
                }
                else {
                    System.out.println("Course exists with ID: " + existingCourse.getId() + ", name: " + existingCourse.getName());
                }
                if (existingCourse != null) {
                    for (DbCourseSection section : sections) {
                        if (section != null) {
                            section.setCourse(existingCourse);
                            try {
                                sectionDao.createOrUpdate(section);
                                System.out.println("   Section saved with ID: " + section.getId() + ", Title: " + section.getTitle() + ", for course ID: " + existingCourse.getId());
                            } catch (SQLException e) {
                                System.err.println("Error saving course section: " + e.getMessage());
                            }
                        } else {
                            System.err.println("Error: section is null");
                        }
                    }
                }
                else {
                    System.err.println("Error: Could not get created course");
                }
            }
            catch (SQLException e) {
                System.err.println("Error saving course data: " + e.getMessage());
            }
        }
        System.out.println("Finished saving courses and sections.");
    }
    public void saveStudentGrades(Map<String, Map<String, Integer>> studentGrades) throws SQLException {
        for (Map.Entry<String, Map<String, Integer>> entry : studentGrades.entrySet()) {
            String studentName = entry.getKey();
            DbStudent dbStudent = new DbStudent(studentName);
            try {
                DbStudent existingStudent = studentDao.queryForMatching(dbStudent).stream().findFirst().orElse(null);
                if (existingStudent == null) {
                    studentDao.create(dbStudent);
                    existingStudent = studentDao.queryForMatching(dbStudent).stream().findFirst().orElse(null);
                    System.out.println("Student created with ID: " + existingStudent.getId() + ", name:" + existingStudent.getName());
                } else{
                    System.out.println("Student exists with ID: " + existingStudent.getId() + ", name:" + existingStudent.getName());
                }
                if (existingStudent != null) {
                    Map<String, Integer> grades = entry.getValue();
                    for (Map.Entry<String, Integer> gradeEntry : grades.entrySet()) {
                        String subject = gradeEntry.getKey();
                        int grade = gradeEntry.getValue();
                        DbGrade dbGrade = new DbGrade(existingStudent, subject, grade);
                        try {
                            gradeDao.createOrUpdate(dbGrade);
                            System.out.println("Grade saved for student ID " + existingStudent.getId() + ": " + subject + " - " + grade + " with grade ID: " + dbGrade.getId());
                        } catch (SQLException e) {
                            System.err.println("Error saving grade: " + e.getMessage());
                        }
                    }
                }
                else {
                    System.err.println("Error: Could not get existing student");
                }
            } catch (SQLException e) {
                System.err.println("Error saving student or grades: " + e.getMessage());
            }
        }
    }

    public void saveUser(DbUser user) throws SQLException{
        if(user == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        try {
            userDao.createOrUpdate(user);
        }
        catch (SQLException e){
            System.err.println("Error saving user" + e.getMessage());
        }

    }

    public void saveUsers(List<DbUser> users) throws SQLException{
        if(users == null || users.isEmpty()){
            throw new IllegalArgumentException("Users cannot be null or empty");
        }
        try{
            for(DbUser user: users){
                userDao.createOrUpdate(user);
            }
        } catch (SQLException e){
            System.err.println("Error saving users" + e.getMessage());
        }
    }

    public List<DbCourse> getAllCourses() throws SQLException {
        return courseDao.queryForAll();
    }
    public List<DbCourseSection> getAllCourseSections() throws SQLException{
        return sectionDao.queryForAll();
    }

    public List<DbGrade> getAllGrades() throws SQLException {
        return gradeDao.queryForAll();
    }

    public List<DbStudent> getAllStudents() throws SQLException {
        return studentDao.queryForAll();
    }
    public List<DbUser> getAllUsers() throws SQLException {
        return userDao.queryForAll();
    }

    public void close() throws Exception {
        if (connectionSource != null) {
            connectionSource.close();
        }
    }
}

