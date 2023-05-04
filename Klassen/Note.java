package Klassen;

public class Note {

    private double noteWert;
    private String noteText;

    public Note(String name, String kurs, double noteWert) {
    }

    public String getNoteText() {
        return noteText;
    }
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
    public double getNote() {
        return noteWert;
    }

    public void setNoteWert(int noteWert) {
        this.noteWert = noteWert;
    }


}
