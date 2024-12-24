
import SQL.*;
import Visualization.GradeDistributionData;
import Visualization.GraphicalDisplay;
import Visualization.StudentVkData;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import javax.swing.SwingUtilities;




public class Main {

    public static void main(String[] args) {
        try {
            String dbFilePath = "D:\\Javaprog\\project11\\final411123!fin.db";
            File dbFile = new File(dbFilePath);
            if (dbFile.exists()) {
                dbFile.delete();
                System.out.println("Deleted old database file");
            }
            DatabaseHelper dbHelper = new DatabaseHelper();
            renderUI(dbHelper);
            dbHelper.close();
        } catch (Exception e) {
            logError("Error during execution", e);
        }
    }
    private static void renderUI(DatabaseHelper dbHelper) throws SQLException {
        List<DbStudent> students = dbHelper.getAllStudents();
        List<DbUser> users = dbHelper.getAllUsers();
        List<DbGrade> grades = dbHelper.getAllGrades();
        List<DbCourse> courses = dbHelper.getAllCourses();

        List<StudentVkData> studentVkDataList = new ArrayList<>();
        for (DbStudent student : students) {
            boolean vkUserFound = users.stream().anyMatch(user -> user.getId() == student.getId());
            StudentVkData studentVkData = new StudentVkData(student.getName(), vkUserFound);
            studentVkDataList.add(studentVkData);
        }
        List<GradeDistributionData> gradeDataList = new ArrayList<>();
        for (DbCourse course : courses) {
            Map<Integer, Integer> gradeCounts = new HashMap<>();
            for (DbGrade grade : grades) {
                if (grade.getSubject().contains(course.getName())) {
                    gradeCounts.put(grade.getGrade(), gradeCounts.getOrDefault(grade.getGrade(), 0) + 1);
                }
            }
            GradeDistributionData gradeData = new GradeDistributionData(course.getName(), gradeCounts);
            gradeDataList.add(gradeData);
        }
        Map<String, Map<String, Integer>> studentGrades = new HashMap<>();
        for (DbStudent student : students) {
            Map<String, Integer> gradesPerStudent = new HashMap<>();
            for (DbGrade grade : grades) {
                if (grade.getStudent().getId() == student.getId()) {
                    gradesPerStudent.put(grade.getSubject(), grade.getGrade());
                }
            }
            studentGrades.put(student.getName(), gradesPerStudent);
        }
        Map<String, Integer> taskCounts = new HashMap<>();
        for (DbGrade grade : grades) {
            String subject = grade.getSubject();
            if (subject.startsWith("УПР")) {
                taskCounts.put("УПР", taskCounts.getOrDefault("УПР", 0) + 1);
            } else if (subject.startsWith("ДЗ")) {
                taskCounts.put("ДЗ", taskCounts.getOrDefault("ДЗ", 0) + 1);
            } else if (subject.startsWith("КВ")) {
                taskCounts.put("КВ", taskCounts.getOrDefault("КВ", 0) + 1);
            }
        }

        Map<String, Double> avgGradePerSubject = new HashMap<>();
        Map<String, Integer> subjectCounts = new HashMap<>();
        for (DbGrade grade : grades) {
            String subject = grade.getSubject();
            avgGradePerSubject.put(subject, avgGradePerSubject.getOrDefault(subject, 0.0) + grade.getGrade());
            subjectCounts.put(subject, subjectCounts.getOrDefault(subject, 0) + 1);
        }
        for (Map.Entry<String, Double> entry : avgGradePerSubject.entrySet()) {
            String subject = entry.getKey();
            avgGradePerSubject.put(subject, entry.getValue() / subjectCounts.get(subject));
        }
        SwingUtilities.invokeLater(() -> new GraphicalDisplay(gradeDataList, studentGrades, taskCounts, avgGradePerSubject));
    }

    private static void logError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}
