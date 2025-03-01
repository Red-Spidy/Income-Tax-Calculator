import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ShowRecords extends JFrame {
    static final String DB_URL = "jdbc:mysql://localhost:3306/taxdb";
    static final String USER = "root";
    static final String PASS = "Red_Spidy";

    private JTable table;
    private DefaultTableModel tableModel;
    private String regime;

    public ShowRecords(String regime) {
        this.regime = regime;
        setTitle("Records - " + regime + " Regime");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Income", "Tax"}, 0);
        table = new JTable(tableModel);
        loadRecords();

        // Scroll pane for table
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton deleteRecordButton = new JButton("Delete Record");
        JButton deleteAllButton = new JButton("Delete All Records");
        JButton updateRecordButton = new JButton("Update Record");

        buttonPanel.add(deleteRecordButton);
        buttonPanel.add(updateRecordButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        deleteRecordButton.addActionListener(e -> deleteSelectedRecord());
        deleteAllButton.addActionListener(e -> deleteAllRecords());
        updateRecordButton.addActionListener(e -> updateSelectedRecord());
    }

    private void loadRecords() {
        String tableName = regime.equals("Old") ? "old_regime" : "new_regime";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            tableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double income = rs.getDouble("income");
                double tax = rs.getDouble("tax");
                tableModel.addRow(new Object[]{id, name, income, tax});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading records: " + e.getMessage());
        }
    }

    private void deleteSelectedRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String tableName = regime.equals("Old") ? "old_regime" : "new_regime";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?")) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Record deleted successfully.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting record: " + e.getMessage());
        }
    }

    private void deleteAllRecords() {
        String tableName = regime.equals("Old") ? "old_regime" : "new_regime";
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all records?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM " + tableName);
                tableModel.setRowCount(0);
                JOptionPane.showMessageDialog(this, "All records deleted successfully.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting all records: " + e.getMessage());
            }
        }
    }

    private void updateSelectedRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to update.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String newName = JOptionPane.showInputDialog(this, "Enter new name:");
        String newIncomeStr = JOptionPane.showInputDialog(this, "Enter new income:");

        if (newName == null || newIncomeStr == null || newName.isEmpty() || newIncomeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
            return;
        }

        try {
            double newIncome = Double.parseDouble(newIncomeStr);
            double newTax = calculateTax(newIncome);

            String tableName = regime.equals("Old") ? "old_regime" : "new_regime";
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement("UPDATE " + tableName + " SET name = ?, income = ?, tax = ? WHERE id = ?")) {
                stmt.setString(1, newName);
                stmt.setDouble(2, newIncome);
                stmt.setDouble(3, newTax);
                stmt.setInt(4, id);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    tableModel.setValueAt(newName, selectedRow, 1);
                    tableModel.setValueAt(newIncome, selectedRow, 2);
                    tableModel.setValueAt(newTax, selectedRow, 3);
                    JOptionPane.showMessageDialog(this, "Record updated successfully.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating record: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid income.");
        }
    }

    private double calculateTax(double income) {
        if (regime.equals("Old")) {
            if (income <= 250000) return 0;
            else if (income <= 500000) return 0.05 * (income - 250000);
            else if (income <= 1000000) return 12500 + 0.2 * (income - 500000);
            else return 112500 + 0.3 * (income - 1000000);
        } else {
            if (income <= 250000) return 0;
            else if (income <= 500000) return 0.05 * income;
            else if (income <= 1000000) return 25000 + 0.1 * (income - 500000);
            else return 75000 + 0.2 * (income - 1000000);
        }
    }
}
