package Klassen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Fach implements Serializable {

    // name
    // listOfGrades
    private String name;
    private HashMap<Student, ArrayList<Grade>> listOfGrades;

    public Fach(String name) {
        this.name = name;
        this.notenListe = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Methode für den Noten-Durchschnitt des Fachs
    public double getAverage(Student student) {
        double count = 0;
        for (Grade grade : listOfGrades.get(student)) {
            count += (double) grade.getGradeValue();
        }
        count /= getListOfGrades(student).size();
        count = Math.round(count * 100.0) / 100.0;
        return count;
    }

    public ArrayList<Grade> getListOfGrades(Student student) {
        return listOfGrades.get(student);
    }

    public void addGrade(Student student, Grade grade) {
        ArrayList<Grade> grades = new ArrayList<>();
        if (getListOfGrades(student) != null) {
            grades = getListOfGrades(student);
        }
        grades.add(grade);
        listOfGrades.put(student, grades);
    }
}