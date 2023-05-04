package Klassen;

import javax.swing.*;

public class NotenverwaltungGUI extends JFrame {
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel kursLabel;
    private JComboBox<String> kursComboBox;
    private JLabel noteLabel;
    private JTextField noteTextField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton showAllButton;

    public NotenverwaltungGUI() {
        // GUI-Initialisierung
        this.setTitle("Notenverwaltung");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Name-Komponenten
        nameLabel = new JLabel("Name:");
        nameTextField = new JTextField(20);

        // Kurs-Komponenten
        kursLabel = new JLabel("Kurs:");
        String[] kurse = {"Mathe", "Deutsch", "Englisch", "Geschichte"};
        kursComboBox = new JComboBox<>(kurse);

        // Note-Komponenten
        noteLabel = new JLabel("Note:");
        noteTextField = new JTextField(20);

        // Schaltflächen-Komponenten
        addButton = new JButton("Hinzufügen");
        deleteButton = new JButton("Löschen");
        showAllButton = new JButton("Alle anzeigen");

        // GUI-Layout
        JPanel panel = new JPanel();
        panel.add(nameLabel);
        panel.add(nameTextField);
        panel.add(kursLabel);
        panel.add(kursComboBox);
        panel.add(noteLabel);
        panel.add(noteTextField);
        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(showAllButton);
        this.add(panel);

        this.setVisible(true);
    }

    public static void main(String[] args) {
        NotenverwaltungGUI gui = new NotenverwaltungGUI();
    }
}
