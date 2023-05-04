package Klassen;

import java.io.*;
import java.util.ArrayList;

public class Notenverwaltung {
    private ArrayList<Note> notenListe;
    private File file;

    public Notenverwaltung() {
        notenListe = new ArrayList<>();
        file = new File("noten.txt");
        if (file.exists()) {
            loadNotenFromFile();
        }
    }

    public void addNote(String name, String kurs, double note) {
        Note neueNote;
        neueNote = new Note(name, kurs, note);
        notenListe.add(neueNote);
        saveNotenToFile();
    }

    public void deleteNote(int index) {
        notenListe.remove(index);
        saveNotenToFile();
    }

    public ArrayList<Note> getAllNoten() {
        return notenListe;
    }

    public double getDurchschnittsnote() {
        if (notenListe.size() == 0) {
            return 0;
        }

        double sum = 0;
        for (Note note : notenListe) {
            sum += note.getNote();
        }

        return sum / notenListe.size();
    }

    private void loadNotenFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String name = parts[0];
                    String kurs = parts[1];
                    double note = Double.parseDouble(parts[2]);
                    notenListe.add(new Note(name, kurs, note));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNotenToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (Note note : notenListe) {
                writer.write(note.getName() + ";" + note.getKurs() + ";" + note.getNote() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}