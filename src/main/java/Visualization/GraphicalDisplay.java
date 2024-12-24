package Visualization;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphicalDisplay extends JFrame {
    private JFreeChart studentPerformanceChart;
    private ChartPanel studentPerformanceChartPanel;
    private JList<String> studentList;

    public GraphicalDisplay(List<GradeDistributionData> gradeData, Map<String, Map<String, Integer>> studentGradeData, Map<String, Integer> taskCounts, Map<String, Double> avgGradePerSubject) {
        super("Панель анализа учащихся");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        JTabbedPane tabbedPane = new JTabbedPane();



        // Создаем панель для успеваемости студентов
        JPanel studentPanel = new JPanel(new BorderLayout());

        // Создаем список имен студентов
        String[] studentNames = studentGradeData.keySet().toArray(new String[0]);
        studentList = new JList<>(studentNames);
        JScrollPane studentListScroller = new JScrollPane(studentList);
        studentListScroller.setPreferredSize(new Dimension(200, 400));
        studentPanel.add(studentListScroller, BorderLayout.WEST);

        // Создаем панель для индивидуальной успеваемости (нижний график)
        studentPerformanceChart = createEmptyStudentPerformanceChart();
        studentPerformanceChartPanel = new ChartPanel(studentPerformanceChart);
        studentPerformanceChartPanel.setPreferredSize(new Dimension(600, 400));
        studentPanel.add(studentPerformanceChartPanel, BorderLayout.CENTER);
        studentList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedStudent = studentList.getSelectedValue();
                    if (selectedStudent != null) {
                        updateStudentPerformanceChart(selectedStudent, studentGradeData.get(selectedStudent));
                    }
                }
            }
        });
        tabbedPane.addTab("Успеваемость студентов", studentPanel);

        // Средняя оценка по заданиям
        JPanel avgGradePanel = new JPanel(new BorderLayout());
        JFreeChart avgGradeChart = createAvgGradePerSubjectChart(avgGradePerSubject);
        ChartPanel avgGradeChartPanel = new ChartPanel(avgGradeChart);
        avgGradeChartPanel.setPreferredSize(new Dimension(1000, 600));
        avgGradePanel.add(avgGradeChartPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Средняя оценка по заданиям", avgGradePanel);

        // Процент выполнения заданий
        JPanel taskCompletionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JFreeChart taskChart = createTaskCompletionChart(taskCounts);
        ChartPanel taskChartPanel = new ChartPanel(taskChart);
        taskChartPanel.setPreferredSize(new Dimension(500, 500));
        taskCompletionPanel.add(taskChartPanel);
        tabbedPane.addTab("Выполнение заданий", taskCompletionPanel);


        // Самые сложные ДЗ
        JPanel mostDifficultTasksPanel = new JPanel(new BorderLayout());
        JFreeChart mostDifficultTasksChart = createMostDifficultTasksChart(studentGradeData);
        ChartPanel mostDifficultTasksChartPanel = new ChartPanel(mostDifficultTasksChart);
        mostDifficultTasksChartPanel.setPreferredSize(new Dimension(1000,600));
        mostDifficultTasksPanel.add(mostDifficultTasksChartPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Самые сложные ДЗ", mostDifficultTasksPanel);

        add(tabbedPane);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JFreeChart createEmptyStudentPerformanceChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(
                "Успеваемость студента",
                "Задание",
                "Оценка",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        return chart;
    }

    private void updateStudentPerformanceChart(String studentName, Map<String, Integer> studentGrades) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : studentGrades.entrySet()) {
            dataset.addValue(entry.getValue(), "Оценка", entry.getKey());
        }
        studentPerformanceChart = ChartFactory.createBarChart(
                "Успеваемость для " + studentName,
                "Задание",
                "Оценка",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        CategoryPlot plot = studentPerformanceChart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setMaximumCategoryLabelWidthRatio(1f);
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        xAxis.setCategoryMargin(0.15);
        studentPerformanceChartPanel.setChart(studentPerformanceChart);
    }


    private JFreeChart createAvgGradePerSubjectChart(Map<String, Double> avgGradePerSubject) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : avgGradePerSubject.entrySet()) {
            dataset.addValue(entry.getValue(), "Средняя оценка", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Средняя оценка по заданиям",
                "Задание",
                "Средняя оценка",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setMaximumCategoryLabelWidthRatio(1f);
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        return chart;
    }


    private JFreeChart createTaskCompletionChart(Map<String, Integer> taskCounts) {
        PieDataset dataset = createTaskDataset(taskCounts);
        return ChartFactory.createPieChart(
                "Процент выполнения заданий",
                dataset,
                true,
                true,
                false
        );
    }

    private PieDataset createTaskDataset(Map<String, Integer> taskCounts) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        int totalTasks = 0;
        for (Map.Entry<String, Integer> entry : taskCounts.entrySet()) {
            totalTasks += entry.getValue();
        }
        for (Map.Entry<String, Integer> entry : taskCounts.entrySet()) {
            double percentage = ((double) entry.getValue() / totalTasks) * 100;
            dataset.setValue(entry.getKey(), percentage);
        }
        return dataset;
    }

    private JFreeChart createMostDifficultTasksChart(Map<String, Map<String, Integer>> studentGradeData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> taskSolutionCount = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> studentEntry : studentGradeData.entrySet()) {
            Map<String, Integer> grades = studentEntry.getValue();
            for (Map.Entry<String, Integer> gradeEntry : grades.entrySet()) {
                String taskName = gradeEntry.getKey();
                if (taskName.startsWith("ДЗ: Практика") && gradeEntry.getValue() > 0) {
                    taskSolutionCount.put(taskName, taskSolutionCount.getOrDefault(taskName, 0) + 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : taskSolutionCount.entrySet()) {
            dataset.addValue(entry.getValue(), "Кол-во решений", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Самые сложные ДЗ",
                "Задание",
                "Кол-во решений",
                dataset,
                PlotOrientation.HORIZONTAL, // Changed here
                true,
                true,
                false
        );
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setMaximumCategoryLabelWidthRatio(1f);
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        return chart;
    }
    public static void main(String[] args) {

        Map<Integer, Integer> gradeMap1 = new HashMap<>();
        gradeMap1.put(1, 40);
        gradeMap1.put(2, 30);
        gradeMap1.put(3, 10);
        gradeMap1.put(4, 10);

        Map<Integer, Integer> gradeMap2 = new HashMap<>();
        gradeMap2.put(1, 10);
        gradeMap2.put(2, 20);
        gradeMap2.put(3, 40);
        gradeMap2.put(4, 20);

        List<GradeDistributionData> gradeDataList = new ArrayList<>();
        gradeDataList.add(new GradeDistributionData("Course 1", gradeMap1));
        gradeDataList.add(new GradeDistributionData("Course 2", gradeMap2));

        Map<String, Map<String, Integer>> studentGrades = new HashMap<>();
        Map<String, Integer> student1Grades = new HashMap<>();
        student1Grades.put("Course 1", 4);
        student1Grades.put("Course 2", 2);
        studentGrades.put("Student 1", student1Grades);

        Map<String, Integer> student2Grades = new HashMap<>();
        student2Grades.put("Course 1", 1);
        student2Grades.put("Course 2", 3);
        studentGrades.put("Student 2", student2Grades);
        Map<String, Integer> taskMap = new HashMap<>();
        taskMap.put("УПР", 20);
        taskMap.put("ДЗ", 20);
        taskMap.put("КВ", 20);
        Map<String, Double> avgGradePerSubject = new HashMap<>();
        avgGradePerSubject.put("Course 1", 3.5);
        avgGradePerSubject.put("Course 2", 2.2);
        SwingUtilities.invokeLater(() -> new GraphicalDisplay(gradeDataList, studentGrades, taskMap, avgGradePerSubject));
    }
}
