package scv;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pars {

    public static List<String[]> parseCsv(String filePath, String delimiter) throws IOException {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                records.add(values);
            }
        }
        return records;
    }
    public static void printTable(List<String[]> data) {
        if (data == null || data.isEmpty()) {
            System.out.println("No data to display.");
            return;
        }
        int maxColumns = data.get(0).length;
        int[] maxLengths = new int[maxColumns];
        for (String[] row : data) {
            for (int i = 0; i < row.length && i < maxColumns; i++) {
                maxLengths[i] = Math.max(maxLengths[i], row[i].length());
            }
        }
        for (String[] row : data) {
            for (int i = 0; i < row.length && i < maxColumns; i++) {
                System.out.printf("%-" + (maxLengths[i] + 3) + "s", row[i] + " |");
            }
            System.out.println();
        }
    }
    public static Map<String, Map<String, Integer>> parseStudentGrades(List<String[]> csvData) {
        Map<String, Map<String, Integer>> studentGrades = new HashMap<>();
        if (csvData.size() < 3) {
            System.err.println("CSV-данные не содержат достаточно строк");
            return studentGrades;
        }
        String[] headers = csvData.get(1);
        for (int i = 3; i < csvData.size(); i++) {
            String[] row = csvData.get(i);
            if (row.length < 3) continue;
            String studentName = row[0].trim();
            Map<String, Integer> grades = new HashMap<>();
            for (int j = 3; j < row.length && j < headers.length; j++) {
                String header = headers[j].trim();
                String value = row[j].trim();
                if (!value.isEmpty()) {
                    try {
                        int grade = Integer.parseInt(value);
                        grades.put(header, grade);
                    } catch (NumberFormatException e) {
                        System.err.println("Пропущено некорректное число: " + value + " для заголовка: "+ header);
                    }
                }
            }
            studentGrades.put(studentName, grades);
        }
        return studentGrades;
    }


    public static Map<String, List<String>> parseCourseData(List<String[]> csvData) {
        Map<String, List<String>> courseTopics = new HashMap<>();
        if (csvData == null || csvData.isEmpty()) {
            System.err.println("Ошибка: CSV-данные пусты или равны null.");
            return courseTopics;
        }
        String[] firstRow = csvData.get(0);

        if (firstRow.length < 2 || firstRow[1] == null || firstRow[1].isEmpty()) {
            System.err.println("Ошибка: Первая строка пуста или не содержит информации о теме курса.");
            return courseTopics;
        }
        String course = firstRow[1].trim();
        List<String> topics = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d+\\.\\s[\\p{L}\\s\\d.,]+)");
        for(int i = 0; i < firstRow.length; i++) {
            if (firstRow[i] != null && !firstRow[i].trim().isEmpty()) {
                Matcher matcher = pattern.matcher(firstRow[i]);
                while (matcher.find()) {
                    topics.add(matcher.group(1).trim());
                }
            }
        }
        courseTopics.put(course, topics);

        return courseTopics;
    }



    public static void main(String[] args) {
        String filePath = "C:\\Users\\krubl\\OneDrive\\Рабочий стол\\ss.csv";
        String delimiter = ";";

        try {
            List<String[]> csvData = parseCsv(filePath, delimiter);
            System.out.println("-----------------------------------\nИсходные данные:\n-----------------------------------");
            printTable(csvData);

            Map<String, Map<String, Integer>> studentGrades = parseStudentGrades(csvData);
            System.out.println("\n-----------------------------------\nОценки студентов:\n-----------------------------------");
            if (studentGrades.isEmpty()) {
                System.out.println("Оценки студентов не найдены.");
            } else {
                for (Map.Entry<String, Map<String, Integer>> entry : studentGrades.entrySet()) {
                    System.out.println("Студент: " + entry.getKey());
                    for (Map.Entry<String, Integer> gradeEntry : entry.getValue().entrySet()) {
                        System.out.println("  " + gradeEntry.getKey() + ": " + gradeEntry.getValue());
                    }
                }
            }
            Map<String, List<String>> courseData = parseCourseData(csvData);
            System.out.println("\n-----------------------------------\nДанные курса:\n-----------------------------------");
            if (courseData.isEmpty()) {
                System.out.println("Данные курса не найдены.");
            } else {
                for (Map.Entry<String, List<String>> entry : courseData.entrySet()) {
                    System.out.println("Курс: " + entry.getKey());
                    System.out.println("Темы: " + entry.getValue());
                }
            }


        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e){
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
