import java.util.HashMap;
import java.util.List;

public class CourseAnalyzer {
    private List<Section> sections;
    private Map<Integer, List<Progress>> studentProgressMap;

    public CourseAnalyzer(Course course) {
        this.sections = new ArrayList<>();
        this.studentProgressMap = new HashMap<>();
        // Загружаем данные из CSV-файла
        loadData(course);
    }

    private void loadData(Course course) {
        // Логика загрузки данных из CSV-файла
        // ...
    }

    // Метод для определения наиболее сложных разделов
    public List<Section> getMostDifficultSections() {
        Map<Integer, Double> sectionAverageScores = new HashMap<>();
        for (Section section : sections) {
            double averageScore = calculateAverageScoreForSection(section);
            sectionAverageScores.put(section.getSectionId(), averageScore);
        }

        // Сортируем разделы по возрастанию среднего балла (чем ниже средний балл, тем сложнее раздел)
        List<Map.Entry<Integer, Double>> sortedSections = sectionAverageScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());

        List<Section> mostDifficultSections = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : sortedSections) {
            mostDifficultSections.add(getSectionById(entry.getKey()));
        }

        return mostDifficultSections;
    }

    private double calculateAverageScoreForSection(Section section) {
        List<Progress> sectionProgress = new ArrayList<>();
        for (List<Progress> studentProgress : studentProgressMap.values()) {
            sectionProgress.addAll(studentProgress.stream()
                    .filter(p -> p.getAssignmentId() == section.getSectionId())
                    .collect(Collectors.toList()));
        }

        int totalScore = sectionProgress.stream().mapToInt(Progress::getScore).sum();
        return (double) totalScore / sectionProgress.size();
    }

    private Section getSectionById(int sectionId) {
        for (Section section : sections) {
            if (section.getSectionId() == sectionId) {
                return section;
            }
        }
        return null;
    }
}