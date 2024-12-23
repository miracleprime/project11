import SQL.DbCourseSection;
import org.apache.commons.csv.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVParser {
    public static List<String[]> parseCsv(String filePath, String delimiter) throws IOException {
        List<String[]> records = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.withDelimiter(delimiter.charAt(0)); // Removed .withHeader();
        String[] headers = null;
        try (Reader reader = new FileReader(filePath);
             org.apache.commons.csv.CSVParser parser = new org.apache.commons.csv.CSVParser(reader, format)
        ){
            List<CSVRecord> recordsList = parser.getRecords();

            if (recordsList.isEmpty()) {
                System.err.println("Error: No records found in CSV data, please check the header or the delimiter.");
                return records;
            }
            int rowNum = 0;
            for(CSVRecord record : recordsList){
                if(rowNum == 0){
                    // Save the headers for parsing the student grades later
                    headers = new String[record.size()];
                    for (int i = 0; i < record.size(); i++) {
                        headers[i] = record.get(i);
                    }
                } else {
                    String[] values = new String[record.size()];
                    for (int i = 0; i < record.size(); i++) {
                        values[i] = record.get(i);
                    }
                    records.add(values);
                }
                rowNum++;
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
        // Since the header is not defined by CSVParser, we get it from the csvData list instead
        String[] headers = csvData.get(0);
        for (int i = 1; i < csvData.size(); i++) { // start at row 2, since row 1 is the header
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
                        System.err.println("Пропущено некорректное число: " + value + " для заголовка: " + header);
                    }
                }
            }
            studentGrades.put(studentName, grades);
        }
        System.out.println("Parsed student grades: " + studentGrades); // Log parsed grades
        return studentGrades;
    }
    public static Map<String, List<DbCourseSection>> parseCourseData(List<String[]> csvData) {
        Map<String, List<DbCourseSection>> courseSections = new HashMap<>();
        if (csvData == null || csvData.isEmpty() || csvData.size() < 2) {
            System.err.println("Ошибка: CSV-данные пусты или не содержат необходимой информации.");
            return courseSections;
        }

        String[] firstRow = csvData.get(0);
        if (firstRow.length < 2 || firstRow[1] == null || firstRow[1].isEmpty()) {
            System.err.println("Ошибка: Первая строка пуста или не содержит информации о теме курса.");
            return courseSections;
        }
        String course = firstRow[1].trim();

        List<DbCourseSection> sections = new ArrayList<>();
        System.out.println("Parsed course data: " + course);
        System.out.println("First row content: " + Arrays.toString(firstRow));
        // Паттерн для поиска названий разделов и их содержимого
        Pattern sectionPattern = Pattern.compile("(\\d+\\.\\s[\\p{L}\\s\\d.,]+)(.*)");
        // Pattern for extracting section tasks (КВ, УПР, ДЗ)
        Pattern taskPattern = Pattern.compile("((КВ|УПР|ДЗ):\\s[\\p{L}\\s\\d.,]+)");
        // Parse all data from the first row
        for(int i = 0; i < firstRow.length; i++) {
            if(firstRow[i] != null && !firstRow[i].trim().isEmpty()) {
                Matcher sectionMatcher = sectionPattern.matcher(firstRow[i]);
                while(sectionMatcher.find()){
                    String sectionTitle = sectionMatcher.group(1).trim();
                    String sectionContent = sectionMatcher.group(2).trim();
                    List<String> tasks = new ArrayList<>();
                    Matcher taskMatcher = taskPattern.matcher(sectionContent);
                    while (taskMatcher.find()) {
                        tasks.add(taskMatcher.group(1).trim());
                    }
                    DbCourseSection section = new DbCourseSection(sectionTitle, tasks);
                    sections.add(section);
                    System.out.println("Parsed sections: " + sections);
                    System.out.println("  Parsed section: " + sectionTitle + ", Tasks: " + tasks);
                }
            }
        }
        courseSections.put(course, sections);
        System.out.println("Parsed course data: " + courseSections); // Log parsed data
        return courseSections;
    }

    public static List<String> parseCourses(String coursesString) {
        List<String> courses = new ArrayList<>();
        String[] lines = coursesString.split("\n"); // Split string by newline

        for(String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty()) {
                courses.add(trimmedLine);
            }
        }
        return courses;
    }
}
