public class Assignment {
    private int assignmentId;
    private String assignmentName;
    private AssignmentType type;
    private Section section;

    public Assignment(int assignmentId, String assignmentName, AssignmentType type, Section section) {
        this.assignmentId = assignmentId;
        this.assignmentName = assignmentName;
        this.type = type;
        this.section = section;
    }
}