package com.mycompany.mphcr04;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MPHCR04 {
    private static final String filePath = "src/main/java/csvfiles/Employeedetails_1.csv";
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField[] textFields;
    private JButton deleteButton;
    private JComboBox<String> employeeComboBox;
    private List<String[]> records;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MPHCR04::new);
    }

    public MPHCR04() {
        JFrame frame = new JFrame("MotorPH Employee App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tableModel = new DefaultTableModel(new String[]{
            "Employee #", "Last Name", "First Name", "Birthday", "Address", 
            "Phone Number", "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", 
            "Status", "Position", "Immediate Supervisor"}, 0);
        table = new JTable(tableModel);
        loadCsvData();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        textFields = new JTextField[table.getColumnCount()];
        JPanel inputPanel = new JPanel(new GridLayout(table.getColumnCount(), 2));
        for (int i = 0; i < table.getColumnCount(); i++) {
            inputPanel.add(new JLabel(table.getColumnName(i)));
            textFields[i] = new JTextField();
            inputPanel.add(textFields[i]);
        }

        employeeComboBox = new JComboBox<>();
        loadEmployeeComboBox();
        employeeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedEmployee = (String) employeeComboBox.getSelectedItem();
                if (selectedEmployee != null) {
                    displayEmployeeDetails(selectedEmployee);
                }
            }
        });

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false); // Initially disabled

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRecord();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String employeeNumber = promptForEmployeeNumber();
                if (employeeNumber != null) {
                    updateRecord(employeeNumber);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String employeeNumber = promptForEmployeeNumber();
                if (employeeNumber != null) {
                    deleteRecord(employeeNumber);
                }
            }
        });

        panel.add(employeeComboBox);
        panel.add(new JScrollPane(table));
        panel.add(inputPanel);
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private String promptForEmployeeNumber() {
        return JOptionPane.showInputDialog("Enter Employee Number:");
    }

    private void loadEmployeeComboBox() {
        for (String[] record : records) {
            employeeComboBox.addItem(record[0] + " - " + record[2] + " " + record[1]);
        }
    }

    private void displayEmployeeDetails(String selectedEmployee) {
        String employeeNumber = selectedEmployee.split(" - ")[0];
        int rowIndex = findRowByEmployeeNumber(employeeNumber);
        if (rowIndex != -1) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                textFields[i].setText((String) table.getValueAt(rowIndex, i));
            }
            deleteButton.setEnabled(true);
        }
    }

    private void addRecord() {
        String[] newRecord = new String[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            newRecord[i] = textFields[i].getText();
        }
        tableModel.addRow(newRecord);
        saveCsvData();
        employeeComboBox.addItem(newRecord[0] + " - " + newRecord[2] + " " + newRecord[1]);
        clearTextFields();
    }

    private void updateRecord(String employeeNumber) {
        int selectedRow = findRowByEmployeeNumber(employeeNumber);
        if (selectedRow != -1) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                String value = JOptionPane.showInputDialog("Enter new " + table.getColumnName(i) + ":", table.getValueAt(selectedRow, i));
                tableModel.setValueAt(value, selectedRow, i);
            }
            saveCsvData();
        } else {
            JOptionPane.showMessageDialog(null, "Employee with Employee Number " + employeeNumber + " not found.");
        }
    }

    private void deleteRecord(String employeeNumber) {
        int selectedRow = findRowByEmployeeNumber(employeeNumber);
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            saveCsvData();
            updateEmployeeComboBox();
            clearTextFields();
            deleteButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(null, "Employee with Employee Number " + employeeNumber + " not found.");
        }
    }

    private void updateEmployeeComboBox() {
        employeeComboBox.removeAllItems();
        loadEmployeeComboBox();
    }

    private int findRowByEmployeeNumber(String employeeNumber) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (employeeNumber.equals(tableModel.getValueAt(i, 0))) { // Assuming Employee # is in the first column
                return i;
            }
        }
        return -1;
    }

    private void loadCsvData() {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            records = reader.readAll();
            for (String[] record : records) {
                tableModel.addRow(record);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    private void saveCsvData() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearTextFields() {
        for (JTextField textField : textFields) {
            textField.setText("");
        }
    }
}
