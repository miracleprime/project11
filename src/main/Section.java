public class Section {
    private int sectionId;
    private String sectionName;
    private Course course;

    public Section(int sectionId, String sectionName, Course course) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.course = course;
    }
}