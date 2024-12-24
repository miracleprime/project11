package Visualization;
import java.util.List;
import java.util.Map;

public class GradeDistributionData {
    private String courseName;
    private Map<Integer, Integer> gradeCounts;
    public GradeDistributionData(String courseName, Map<Integer, Integer> gradeCounts){
        this.courseName = courseName;
        this.gradeCounts = gradeCounts;
    }

    public String getCourseName() {
        return courseName;
    }

    public Map<Integer, Integer> getGradeCounts() {
        return gradeCounts;
    }
}
