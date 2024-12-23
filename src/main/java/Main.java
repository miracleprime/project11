import SQL.*;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.users.UserFull;
import vkApi.VkRepository;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            String dbFilePath = "final411123.db";
            File dbFile = new File(dbFilePath);
            if (dbFile.exists()) {
                dbFile.delete();
                System.out.println("Deleted old database file");
            }
            DatabaseHelper dbHelper = new DatabaseHelper();
            CSVParser csvParser = new CSVParser(); //Create instance of CSVParser
            processData(csvParser, dbHelper);
            // VK API
            try {
                processVkData(dbHelper);
            } catch (ApiException | ClientException e) {
                System.err.println("Error during VK API call: " + e.getMessage());
                e.printStackTrace();
            }
            dbHelper.close();
        } catch (Exception e) {
            logError("Error during execution", e);
        }
    }
    private static void processData(CSVParser csvParser, DatabaseHelper dbHelper) throws Exception {
        String filePath = "C:\\Users\\krubl\\OneDrive\\Рабочий стол\\ss.csv";
        String delimiter = ";";
        List<String[]> csvData = CSVParser.parseCsv(filePath, delimiter);
        System.out.println("-\nИсходные данные:\n-");
        CSVParser.printTable(csvData);

        // Parse and save student grades
        var studentGrades = CSVParser.parseStudentGrades(csvData);
        if (!studentGrades.isEmpty()) {
            System.out.println("\n-\nОценки студентов:\n-");
            studentGrades.forEach((studentName, grades) -> {
                System.out.println("Студент: " + studentName);
                grades.forEach((subject, grade) -> {
                    System.out.println("  " + subject + ": " + grade);
                });
            });
            dbHelper.saveStudentGrades(studentGrades);
        } else {
            System.out.println("Оценки студентов не найдены.");
        }

        // Parse and save course data
        var courseData = CSVParser.parseCourseData(csvData);
        System.out.println("Parsed course data: " + courseData);
        if (!courseData.isEmpty()) {
            System.out.println("\n-\nДанные курса:\n-");
            courseData.forEach((courseName, sections) -> {
                System.out.println("Курс: " + courseName);
                sections.forEach(section -> {
                    System.out.println("  Раздел: " + section.getTitle());
                    System.out.println("    Задачи: " + section.getTasks());
                });
            });
            dbHelper.saveCourses(courseData);
        } else {
            System.err.println("Error: courseData is empty!");
        }


        String coursesString =
                "1. Введение в Java\n" +
                        "2. Базовый синтаксис. Типы\n" +
                        "3. Массивы и управляющие конструкции\n" +
                        "4. ООП. Основы\n" +
                        "5. ООП. Наследование. Абстракции\n" +
                        "6. Обработка ошибок, исключения, отладка\n" +
                        "7. Ввод-вывод, доступ к файловой системе\n" +
                        "8. Ввод-вывод, продвинутые возможности\n" +
                        "9. Обобщения. Коллекции\n" +
                        "10. Функциональные интерфейсы. Stream API\n" +
                        "11. Многопоточность\n" +
                        "12. Тестирование\n";
        List<String> courses = CSVParser.parseCourses(coursesString);
        System.out.println("\nParsed courses: " + courses); // Log parsed courses
        if (!courses.isEmpty()) {
            dbHelper.saveCoursesList(courses);
        }
        System.out.println("\nДанные сохранены в БД!");

        //Read data from db
        System.out.println("\n-- Чтение данных из БД --");
        List<DbCourse> coursesFromDb = dbHelper.getAllCourses();
        if (coursesFromDb.isEmpty()) {
            System.out.println("Курсы в БД не найдены.");
        } else {
            System.out.println("Курсы:");
            for (DbCourse course : coursesFromDb) {
                System.out.println("  ID: " + course.getId() + ", Название: " + course.getName());
                List<DbCourseSection> sections = dbHelper.getAllCourseSections();
                if(sections.isEmpty()){
                    System.out.println("Разделы для курса " + course.getName() + " не найдены");
                } else {
                    for (DbCourseSection section : sections){
                        if(section.getCourse().getName().equals(course.getName())){
                            System.out.println("   Раздел: " + section.getTitle() + ", Задачи:" + section.getTasks());
                        }
                    }
                }
            }
        }
        List<DbStudent> students = dbHelper.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("Студенты в БД не найдены.");
        } else {
            System.out.println("Студенты:");
            for (DbStudent student : students) {
                System.out.println("  ID: " + student.getId() + ", Имя: " + student.getName());
                List<DbGrade> grades = dbHelper.getAllGrades();
                if (grades.isEmpty()) {
                    System.out.println("Оценки для студента " + student.getName() + " не найдены");
                } else {
                    for (DbGrade grade : grades) {
                        if (grade.getStudent().getName().equals(student.getName())) {
                            System.out.println("   Оценка по " + grade.getSubject() + ": " + grade.getGrade());
                        }
                    }
                }
            }
        }
    }
    private static void processVkData(DatabaseHelper dbHelper) throws ApiException, ClientException, SQLException {
        int APP_ID = 5; // Replace with your app ID
        String CODE = ""; // Replace with your token
        VkRepository vkRepository = new VkRepository(APP_ID, CODE);
        List<DbUser> users = new ArrayList<>();
        List<DbStudent> students = dbHelper.getAllStudents();

        if (students.isEmpty()) {
            System.err.println("No students found in the database.");
            return;
        }
        try {
            for (DbStudent student : students) {
                List<User> usersFromVk = vkRepository.getUsersByName(student.getName());
                if (!usersFromVk.isEmpty()) {
                    UserFull firstUser = (UserFull) usersFromVk.get(0);
                    if(firstUser != null) {
                        DbUser dbUser = new DbUser(firstUser.getId(), firstUser.getFirstName(), firstUser.getLastName());
                        users.add(dbUser);
                        System.out.println("User found on VK: " + firstUser.getFirstName() + " " + firstUser.getLastName() + ", id: " + firstUser.getId());
                    }

                } else {
                    System.err.println("No users found on VK with name: " + student.getName());
                }
            }
            dbHelper.saveUsers(users);
        } catch (SQLException e) {
            System.err.println("Error during saving users to db: " + e.getMessage());
            e.printStackTrace();
        }

    }
    private static void logError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}

