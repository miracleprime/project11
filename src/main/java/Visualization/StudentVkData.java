package Visualization;

public class StudentVkData {
    private String studentName;
    private boolean vkUserFound;

    public StudentVkData(String studentName, boolean vkUserFound) {
        this.studentName = studentName;
        this.vkUserFound = vkUserFound;
    }

    public String getStudentName() {
        return studentName;
    }

    public boolean isVkUserFound() {
        return vkUserFound;
    }
}
