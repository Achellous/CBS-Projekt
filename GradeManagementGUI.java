package Klassen;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Starter-App
 */
public class GradeManagementGUI extends JFrame {

    private final List<Course> courses = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();
    private final Course placeholderCourse = new Course("No selected course");
    private final Student placeholderStudent = new Student("No selected student", 0);
    private JComboBox<Course> courseComboBox;
    private JComboBox<Student> studentComboBox;

    private String dataFileName = "data";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GradeManagementGUI gui = new GradeManagementGUI();
            gui.setTitle("Grade Management");
            gui.setSize(600, 420);
            gui.setResizable(false);
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gui.createGUI();
            gui.setVisible(true);
        });
    }

    /**
     *GUI-Erstellen
     */
    private void createGUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Create GUI components
        JTextField courseNameTextField = new JTextField();
        JButton newCourseButton = new JButton("New Course");
        newCourseButton.addActionListener(e -> {
            String name = courseNameTextField.getText();
            courses.add(new Course(name));
            courseNameTextField.setText("");
            updateComboBoxes();
        });

        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 30));

        // Füge Platzhalterwert für "Kein Kurs ausgewählt" hinzu
        courseComboBox.addItem(placeholderCourse);

        JButton deleteCourseButton = new JButton("Delete Course");
        deleteCourseButton.addActionListener(e -> {
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            if (selectedCourse != placeholderCourse) {
                courses.remove(selectedCourse);
                updateComboBoxes();
            }
        });



        JTextField studentNameTextField = new JTextField();
        JTextField studentAgeTextField = new JTextField();
        JButton newStudentButton = new JButton("New Student");
        newStudentButton.addActionListener(e -> {
            String name = studentNameTextField.getText();
            int age = Integer.parseInt(studentAgeTextField.getText());
            students.add(new Student(name, age));
            studentNameTextField.setText("");
            studentAgeTextField.setText("");
            updateComboBoxes();
        });

        studentComboBox = new JComboBox<>();
        studentComboBox.setPreferredSize(new Dimension(200, 30));

        // Füge Platzhalterwert für "Kein Schüler ausgewählt" hinzu
        studentComboBox.addItem(placeholderStudent);

        JButton deleteStudentButton = new JButton("Delete Student");
        deleteStudentButton.addActionListener(e -> {
            Student selectedStudent = (Student) studentComboBox.getSelectedItem();
            if (selectedStudent != placeholderStudent) {
                students.remove(selectedStudent);
                updateComboBoxes();
            }
        });

        JButton subscribeButton = new JButton("Subscribe");
        subscribeButton.addActionListener(e -> {
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            Student selectedStudent = (Student) studentComboBox.getSelectedItem();
            if (selectedCourse != null && selectedStudent != null) {
                if (selectedCourse != placeholderCourse && selectedStudent != placeholderStudent) {
                    selectedStudent.getCourseList().add(selectedCourse);
                    selectedCourse.getGradeMap().put(selectedStudent, new ArrayList<>());
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a valid course and student.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        JButton unsubscribeButton = new JButton("Unsubscribe");
        unsubscribeButton.addActionListener(e -> {
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            Student selectedStudent = (Student) studentComboBox.getSelectedItem();
            if (selectedCourse != null && selectedStudent != null) {
                selectedStudent.getCourseList().remove(selectedCourse);
                selectedCourse.getGradeMap().remove(selectedStudent);
            }
        });

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(dataFileName));
            int option = fileChooser.showSaveDialog(panel);
            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                dataFileName = selectedFile.getAbsolutePath();
                saveGradeData();
            }
        });

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(dataFileName));
            int option = fileChooser.showOpenDialog(panel);
            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                dataFileName = selectedFile.getAbsolutePath();
                loadGradeData();
                updateComboBoxes();
            }
        });

        JTextField gradeValueTextField = new JTextField();
        JComboBox<Importance> importanceComboBox = new JComboBox<>(Importance.values());
        importanceComboBox.setPreferredSize(new Dimension(200, 30));

        JButton addGradeButton = new JButton("Add Grade");
        addGradeButton.addActionListener(e -> {
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            Student selectedStudent = (Student) studentComboBox.getSelectedItem();
            double value = Double.parseDouble(gradeValueTextField.getText());
            Importance importance = (Importance) importanceComboBox.getSelectedItem();
            if (selectedCourse != null && selectedStudent != null && importance != null) {
                if (selectedStudent.getCourseList().contains(selectedCourse)) {
                    List<Grade> grades = selectedCourse.getGradeMap().getOrDefault(selectedStudent, new ArrayList<>());
                    grades.add(new Grade(value, importance));
                    selectedCourse.getGradeMap().put(selectedStudent, grades);
                    gradeValueTextField.setText("");
                } else {
                    JOptionPane.showMessageDialog(panel, "The student is not subscribed to the selected course.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton removeGradeButton = new JButton("Remove Grade");
        removeGradeButton.addActionListener(e -> {
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            Student selectedStudent = (Student) studentComboBox.getSelectedItem();
            double value = Double.parseDouble(gradeValueTextField.getText());
            Importance importance = (Importance) importanceComboBox.getSelectedItem();
            if (selectedCourse != null && selectedStudent != null && importance != null) {
                List<Grade> grades = selectedCourse.getGradeMap().getOrDefault(selectedStudent, new ArrayList<>());
                grades.remove(new Grade(value, importance));
                selectedCourse.getGradeMap().put(selectedStudent, grades);
                gradeValueTextField.setText("");
            }
        });

        JButton printStatsButton = new JButton("Print Stats");
        printStatsButton.addActionListener(e -> {
            Student selectedStudent = (Student) studentComboBox.getSelectedItem();
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            if (selectedStudent != null && selectedCourse != null && selectedStudent != placeholderStudent) {
                StringBuilder statsBuilder = new StringBuilder();
                statsBuilder.append("Name: ").append(selectedStudent.getName()).append("\n");
                statsBuilder.append("Age: ").append(selectedStudent.getAge()).append("\n");
                statsBuilder.append("Courses: ").append(selectedStudent.getCourseList()).append("\n");
                double overallAverage = calculateOverallAverageGrade(selectedStudent);
                statsBuilder.append("Overall Average Grade: ").append(overallAverage).append("\n");
                if (selectedCourse != placeholderCourse) {
                    List<Grade> grades = selectedCourse.getGradeMap().getOrDefault(selectedStudent, new ArrayList<>());
                    double courseAverage = calculateAverageGrade(grades);
                    statsBuilder.append("Average Grade for Course '").append(selectedCourse.getName()).append("': ").append(courseAverage).append("\n");
                    statsBuilder.append("Grades for Course '").append(selectedCourse.getName()).append("': ").append(grades).append("\n");
                }
                String stats = statsBuilder.toString();
                JOptionPane.showMessageDialog(panel, stats, "Statistics", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        // Add GUI components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Course Name"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(courseNameTextField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(newCourseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(courseComboBox, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(deleteCourseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Student Name"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(new JLabel("Age"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(studentNameTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(studentAgeTextField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(newStudentButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(studentComboBox, gbc);

        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(deleteStudentButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(subscribeButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(unsubscribeButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(saveButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(loadButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Grade"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(gradeValueTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(importanceComboBox, gbc);

        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(addGradeButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(removeGradeButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(printStatsButton, gbc);
        setContentPane(panel);
    }

    /**
     *
     */
    private void updateComboBoxes() {
        courseComboBox.setModel(new DefaultComboBoxModel<>(courses.toArray(new Course[0])));
        studentComboBox.setModel(new DefaultComboBoxModel<>(students.toArray(new Student[0])));
        courseComboBox.addItem(placeholderCourse);
        studentComboBox.addItem(placeholderStudent);
    }

    /**
     *Gesamte-Durchschnitt-Berechnung
     * @param student
     * @return
     */
    private double calculateOverallAverageGrade(Student student) {
        List<Grade> allGrades = new ArrayList<>();
        for (Course course : courses) {
            List<Grade> grades = course.getGradeMap().getOrDefault(student, new ArrayList<>());
            allGrades.addAll(grades);
        }
        return calculateAverageGrade(allGrades);
    }

    /**
     *Note-Durchschnitt-Berechnen
     * @param grades
     * @return
     */
    private double calculateAverageGrade(List<Grade> grades) {
        if (grades.isEmpty()) {
            return 0.0;
        }

        double sumLow = 0.0;
        int countLow = 0;
        double sumMid = 0.0;
        int countMid = 0;
        double sumHigh = 0.0;
        int countHigh = 0;

        for (Grade grade : grades) {
            if (grade.importance().equals(Importance.EPO)) {
                countLow++;
                sumLow += grade.value();
            } else if (grade.importance().equals(Importance.TEST)) {
                countMid++;
                sumMid += grade.value();
            } else if (grade.importance().equals(Importance.KLAUSUR)) {
                countHigh++;
                sumHigh += grade.value();
            }
        }

        double weightedSum = 0.0;
        double totalWeight = 0.0;

        if (countLow > 0) {
            weightedSum += (sumLow / countLow) * 0.2;
            totalWeight += 0.2;
        }
        if (countMid > 0) {
            weightedSum += (sumMid / countMid) * 0.3;
            totalWeight += 0.3;
        }
        if (countHigh > 0) {
            weightedSum += (sumHigh / countHigh) * 0.5;
            totalWeight += 0.5;
        }

        double averageGrade = 0.0;
        if (totalWeight > 0) {
            averageGrade = weightedSum / totalWeight;
        }

        return Math.round(averageGrade * 100.0) / 100.0;
    }


    /**
     *
     * Note-Informationen-Speichen
     */
    private void saveGradeData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFileName))) {
            for (Student student : students) {
                writer.println("Student:" + student.getName() + "," + student.getAge());
            }
            for (Course course : courses) {
                writer.println("Course:" + course.getName());
                for (Student student : course.getGradeMap().keySet()) {
                    writer.print("Student:" + student.getName() + "," + student.getAge());
                    List<Grade> grades = course.getGradeMap().get(student);
                    for (Grade grade : grades) {
                        writer.print("," + grade.value() + "," + grade.importance());
                    }
                    writer.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Note(txt.)-Aufrufen
     */
    private void loadGradeData() {
        courses.clear();
        students.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFileName))) {
            String line;
            Course currentCourse = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Course:")) {
                    String courseName = line.substring("Course:".length());
                    currentCourse = new Course(courseName);
                    courses.add(currentCourse);
                } else if (line.startsWith("Student:")) {
                    String[] parts = line.substring("Student:".length()).split(",");
                    if (parts.length >= 2) {
                        String studentName = parts[0];
                        int studentAge = Integer.parseInt(parts[1]);
                        Student student = new Student(studentName, studentAge);

                        // Check if student already exists in the list
                        int existingStudentIndex = students.indexOf(student);
                        if (existingStudentIndex != -1) {
                            student = students.get(existingStudentIndex);
                        } else {
                            students.add(student);
                        }

                        if (currentCourse != null) {
                            student.getCourseList().add(currentCourse);
                            List<Grade> grades = new ArrayList<>();
                            for (int index = 2; index < parts.length; index += 2) {
                                int value = Integer.parseInt(parts[index]);
                                Importance importance = Importance.valueOf(parts[index + 1]);
                                grades.add(new Grade(value, importance));
                            }
                            currentCourse.getGradeMap().put(student, grades);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}