
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

enum Importance {
    EPO,
    TEST,
    KLAUSUR,
}

/**
 *
 *
 */
public class GradeManagement {

    private static final List<Course> courses = new ArrayList<>();
    private static final List<Student> students = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Bitte geben Sie eine Aktion ein:");
            System.out.println(" - 'ende': Programm beenden");
            System.out.println(" - 'newCourse': Neuen Kurs erstellen");
            System.out.println(" - 'deleteCourse': Kurs löschen");
            System.out.println(" - 'newStudent': Neuen Studenten erstellen");
            System.out.println(" - 'deleteStudent': Studenten löschen");
            System.out.println(" - 'subscribe': Studenten für Kurs anmelden");
            System.out.println(" - 'unsubscribe': Studenten von Kurs abmelden");
            System.out.println(" - 'addGrade': Note hinzufügen");
            System.out.println(" - 'removeGrade': Note entfernen");
            System.out.println(" - 'printStats': Statistiken anzeigen");

            String input = scan.next();
            switch (input.toLowerCase()) {
                case "ende" -> running = false;
                case "newcourse" -> createNewCourse(scan);
                case "deletecourse" -> deleteCourse(scan);
                case "newstudent" -> createNewStudent(scan);
                case "deletestudent" -> deleteStudent(scan);
                case "subscribe" -> subscribeStudentToCourse(scan);
                case "unsubscribe" -> unsubscribeStudentFromCourse(scan);
                case "addgrade" -> addGrade(scan);
                case "removegrade" -> removeGrade(scan);
                case "printstats" -> printStatistics(scan);
                case "save" -> {
                    System.out.println("Bitte geben Sie den Dateinamen zum Speichern der Daten ein:");
                    String saveFilename = scan.next();
                    saveDataToFile(saveFilename);
                }
                case "load" -> {
                    System.out.println("Bitte geben Sie den Dateinamen zum Laden der Daten ein:");
                    String loadFilename = scan.next();
                    loadDataFromFile(loadFilename);
                }
                default -> System.out.println("Ungültige Eingabe. Bitte versuchen Sie es erneut.");
            }
            System.out.println();
        }
        scan.close();
    }

    // Methode zum Speichern der Daten in eine Textdatei
    private static void saveDataToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            // Speichern der Kursdaten
            writer.println("COURSES");
            for (Course course : courses) {
                writer.println(course.getName());
            }

            // Speichern der Studentendaten
            writer.println("STUDENTS");
            for (Student student : students) {
                writer.println(student.getName() + "," + student.getAge());
            }

            // Speichern der Notendaten
            writer.println("GRADES");
            for (Course course : courses) {
                for (Map.Entry<Student, List<Grade>> entry : course.getGradeMap().entrySet()) {
                    Student student = entry.getKey();
                    List<Grade> grades = entry.getValue();
                    for (Grade grade : grades) {
                        writer.println(course.getName() + "," + student.getName() + "," + student.getAge() + "," + grade.value() + "," + grade.importance());
                    }
                }
            }
            System.out.println("Die Daten wurden erfolgreich in die Datei '" + filename + "' gespeichert.");
        } catch (FileNotFoundException e) {
            System.out.println("Fehler beim Speichern der Daten: " + e.getMessage());
        }
    }

    // Methode zum Laden der Daten aus einer Textdatei
    private static void loadDataFromFile(String filename) {
        clearData(); // Vor dem Laden der Daten die bestehenden Daten löschen

        try (Scanner scanner = new Scanner(new File(filename))) {
            String section = "";
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.equalsIgnoreCase("COURSES")) {
                    section = "COURSES";
                } else if (line.equalsIgnoreCase("STUDENTS")) {
                    section = "STUDENTS";
                } else if (line.equalsIgnoreCase("GRADES")) {
                    section = "GRADES";
                } else {
                    switch (section) {
                        case "COURSES" -> courses.add(new Course(line));
                        case "STUDENTS" -> {
                            String[] studentData = line.split(",");
                            String name = studentData[0];
                            int age = Integer.parseInt(studentData[1]);
                            students.add(new Student(name, age));
                        }
                        case "GRADES" -> {
                            String[] gradeData = line.split(",");
                            String courseName = gradeData[0];
                            String studentName = gradeData[1];
                            int studentAge = Integer.parseInt(gradeData[2]);
                            int value = Integer.parseInt(gradeData[3]);
                            Importance importance = Importance.valueOf(gradeData[4]);
                            Course course = findCourseByName(courseName);
                            Student student = findStudentByName(studentName, studentAge);
                            if (course != null && student != null) {
                                List<Grade> grades = course.getGradeMap().getOrDefault(student, new ArrayList<>());
                                grades.add(new Grade(value, importance));
                                course.getGradeMap().put(student, grades);
                            }
                        }
                    }
                }
            }
            System.out.println("Die Daten wurden erfolgreich aus der Datei '" + filename + "' geladen.");
        } catch (FileNotFoundException e) {
            System.out.println("Fehler beim Laden der Daten: Die Datei '" + filename + "' wurde nicht gefunden.");
        }
    }

    // Methode zum Löschen aller Daten
    private static void clearData() {
        courses.clear();
        students.clear();
    }


    private static void createNewCourse(Scanner scan) {
        System.out.println("Bitte geben Sie den Namen des neuen Kurses ein:");
        String name = scan.next();
        courses.add(new Course(name));
        System.out.println("Der Kurs '" + name + "' wurde erstellt.");
    }

    private static void deleteCourse(Scanner scan) {
        System.out.println("Bitte geben Sie den Namen des zu löschenden Kurses ein:");
        String name = scan.next();
        Course courseToDelete = findCourseByName(name);
        if (courseToDelete != null) {
            courses.remove(courseToDelete);
            System.out.println("Der Kurs '" + name + "' wurde gelöscht.");
        } else {
            System.out.println("Der Kurs '" + name + "' existiert nicht.");
        }
    }

    private static void createNewStudent(Scanner scan) {
        System.out.println("Bitte geben Sie den Namen des neuen Studenten ein:");
        String name = scan.next();
        System.out.println("Bitte geben Sie das Alter des neuen Studenten ein:");
        int age = scan.nextInt();
        students.add(new Student(name, age));
        System.out.println("Der Student '" + name + "' wurde erstellt.");
    }

    private static void deleteStudent(Scanner scan) {
        System.out.println("Bitte geben Sie den Namen des zu löschenden Studenten ein:");
        String name = scan.next();
        System.out.println("Bitte geben Sie das Alter des zu löschenden Studenten ein:");
        int age = scan.nextInt();
        Student studentToDelete = findStudentByName(name, age);
        if (studentToDelete != null) {
            students.remove(studentToDelete);
            System.out.println("Der Student '" + name + "' wurde gelöscht.");
        } else {
            System.out.println("Der Student '" + name + "' existiert nicht.");
        }
    }

    private static void subscribeStudentToCourse(Scanner scan) {
        System.out.println("Bitte geben Sie den Namen des Kurses ein:");
        String courseName = scan.next();
        Course course = findCourseByName(courseName);
        System.out.println("Bitte geben Sie den Namen des Studenten ein:");
        String studentName = scan.next();
        Student student = findStudentByName(studentName);
        if (course != null && student != null) {
            student.getCourseList().add(course);
            System.out.println("Der Student '" + studentName + "' wurde für den Kurs '" + courseName + "' eingeschrieben.");
        } else {
            System.out.println("Fehler: Kurs oder Student nicht gefunden.");
        }
    }

    private static void unsubscribeStudentFromCourse(Scanner scan) {
        System.out.println("Bitte geben Sie den Namen des Kurses ein:");
        String courseName = scan.next();
        Course course = findCourseByName(courseName);
        System.out.println("Bitte geben Sie den Namen des Studenten ein:");
        String studentName = scan.next();
        Student student = findStudentByName(studentName);
        if (course != null && student != null) {
            student.getCourseList().remove(course);
            System.out.println("Der Student '" + studentName + "' wurde vom Kurs '" + courseName + "' abgemeldet.");
        } else {
            System.out.println("Fehler: Kurs oder Student nicht gefunden.");
        }
    }

    private static void addGrade(Scanner scan) {
        System.out.println("Bitte geben Sie den Namen des Kurses ein:");
        String courseName = scan.next();
        Course course = findCourseByName(courseName);
        System.out.println("Bitte geben Sie den Namen des Studenten ein:");
        String studentName = scan.next();
        Student student = findStudentByName(studentName);
        if (course != null && student != null) {
            System.out.println("Bitte geben Sie den Notenwert ein:");
            int gradeValue = scan.nextInt();
            System.out.println("Bitte geben Sie die Wichtigkeit der Note ein ('low', 'mid' oder 'high'):");
            String importanceStr = scan.next().toUpperCase();
            Importance gradeImportance;
            try {
                gradeImportance = Importance.valueOf(importanceStr);
                List<Grade> grades = course.getGradeMap().getOrDefault(student, new ArrayList<>());
                grades.add(new Grade(gradeValue, gradeImportance));
                course.getGradeMap().put(student, grades);
                System.out.println("Die Note wurde hinzugefügt.");
            } catch (IllegalArgumentException e) {
                System.out.println("Fehler: Ungültige Wichtigkeit.");
            }
        } else {
            System.out.println("Fehler: Kurs oder Student nicht gefunden.");
        }
    }

    private static void removeGrade(Scanner scan) {
        System.out.println("Bitte geben Sie den Namen des Kurses ein:");
        String courseName = scan.next();
        Course course = findCourseByName(courseName);
        System.out.println("Bitte geben Sie den Namen des Studenten ein:");
        String studentName = scan.next();
        Student student = findStudentByName(studentName);
        if (course != null && student != null) {
            System.out.println("Bitte geben Sie den Notenwert ein:");
            int gradeValue = scan.nextInt();
            System.out.println("Bitte geben Sie die Wichtigkeit der Note ein ('low', 'mid' oder 'high'):");
            String importanceStr = scan.next().toUpperCase();
            Importance gradeImportance;
            try {
                gradeImportance = Importance.valueOf(importanceStr);
                List<Grade> grades = course.getGradeMap().getOrDefault(student, new ArrayList<>());
                Grade gradeToRemove = new Grade(gradeValue, gradeImportance);
                grades.remove(gradeToRemove);
                course.getGradeMap().put(student, grades);
                System.out.println("Die Note wurde entfernt.");
            } catch (IllegalArgumentException e) {
                System.out.println("Fehler: Ungültige Wichtigkeit.");
            }
        } else {
            System.out.println("Fehler: Kurs oder Student nicht gefunden.");
        }
    }

    private static void printStatistics(Scanner scan) {
        System.out.println("Bitte geben Sie den Namen des Studenten ein:");
        String studentName = scan.next();
        Student student = findStudentByName(studentName);
        if (student != null) {
            System.out.println("Name: " + student.getName());
            System.out.println("Alter: " + student.getAge());
            System.out.println("Kurse: " + student.getCourseList());

            double overallAverage = calculateOverallAverageGrade(student);
            System.out.println("Durchschnittsnote aller Kurse: " + overallAverage);

            System.out.println("Bitte geben Sie den Namen des Kurses ein:");
            String courseName = scan.next();
            Course course = findCourseByName(courseName);
            if (course != null) {
                List<Grade> grades = course.getGradeMap().getOrDefault(student, new ArrayList<>());
                double average = calculateAverageGrade(grades);
                System.out.println("Durchschnittsnote für Kurs '" + courseName + "': " + average);
                System.out.println("Kursnoten: " + grades);
            } else {
                System.out.println("Fehler: Kurs nicht gefunden.");
            }
        } else {
            System.out.println("Fehler: Student nicht gefunden.");
        }
    }

    private static Student findStudentByName(String name, int age) {
        for (Student student : students) {
            if (student.getName().equalsIgnoreCase(name) && student.getAge() == age) {
                return student;
            }
        }
        return null;
    }

    private static double calculateOverallAverageGrade(Student student) {
        List<Grade> allGrades = new ArrayList<>();
        for (Course course : student.getCourseList()) {
            List<Grade> grades = course.getGradeMap().getOrDefault(student, new ArrayList<>());
            allGrades.addAll(grades);
        }
        return calculateAverageGrade(allGrades);
    }

    private static double calculateAverageGrade(List<Grade> grades) {
        if (grades.isEmpty()) {
            return 0.0;
        }
        int sum = 0;
        for (Grade grade : grades) {
            sum += grade.value();
        }
        return (double) sum / grades.size();
    }

    private static Course findCourseByName(String name) {
        for (Course course : courses) {
            if (course.getName().equalsIgnoreCase(name)) {
                return course;
            }
        }
        return null;
    }

    private static Student findStudentByName(String name) {
        for (Student student : students) {
            if (student.getName().equalsIgnoreCase(name)) {
                return student;
            }
        }
        return null;
    }
}

class Course {

    private final String name;
    private final Map<Student, List<Grade>> gradeMap;

    public String getName() {
        return name;
    }

    public Map<Student, List<Grade>> getGradeMap() {
        return gradeMap;
    }

    public Course(String name) {
        this.name = name;
        this.gradeMap = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}

class Student {

    private final String name;
    private final int age;
    private final List<Course> courseList;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
        courseList = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object) name, Optional.of(age));
    }

    @Override
    public String toString() {
        return name  + ", " + age;
    }
}

record Grade(double value, Importance importance) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o != null && getClass() == o.getClass()) {
            Grade grade = (Grade) o;
            return value == grade.value && importance == grade.importance;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return value + ", " + importance;
    }
}
