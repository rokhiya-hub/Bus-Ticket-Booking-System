import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminBusManagementFrame extends JFrame {

    private JTable busTable;
    private DefaultTableModel model;

    private JTextField busNoField, routeField, typeField, fareField, seatsField;

    public AdminBusManagementFrame() {

        setTitle("Admin - Bus Management");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 47));
        header.setPreferredSize(new Dimension(getWidth(), 70));

        JLabel title = new JLabel("Bus Management Panel");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(244, 246, 249));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form Panel
        JPanel formPanel = createCardPanel("Add / Update Bus");

        busNoField = new JTextField();
        routeField = new JTextField();
        typeField = new JTextField();
        fareField = new JTextField();
        seatsField = new JTextField();

        formPanel.add(createFormRow("Bus Number", busNoField));
        formPanel.add(createFormRow("Route", routeField));
        formPanel.add(createFormRow("Bus Type", typeField));
        formPanel.add(createFormRow("Fare", fareField));
        formPanel.add(createFormRow("Total Seats", seatsField));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton addBtn = createButton("Add Bus", new Color(45, 137, 239));
        JButton updateBtn = createButton("Update Bus", new Color(255, 193, 7));
        JButton deleteBtn = createButton("Delete Bus", new Color(220, 53, 69));

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);

        formPanel.add(btnPanel);

        // Table Panel
        JPanel tablePanel = createCardPanel("All Buses");

        String[] columns = {"Bus No", "Route", "Type", "Fare", "Seats"};
        model = new DefaultTableModel(columns, 0);
        busTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(busTable);
        tablePanel.add(scrollPane);

        mainPanel.add(formPanel, BorderLayout.WEST);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Load buses when frame opens
        loadBusesFromDB();

        // Actions
        addBtn.addActionListener(e -> addBus());
        updateBtn.addActionListener(e -> updateBus());
        deleteBtn.addActionListener(e -> deleteBus());

        busTable.getSelectionModel().addListSelectionListener(e -> loadSelectedBus());
    }

    // ================= DATABASE METHODS =================

    private void addBus() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO buses(bus_no, route, type, fare, seats) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, busNoField.getText().trim());
            ps.setString(2, routeField.getText().trim());
            ps.setString(3, typeField.getText().trim());
            ps.setDouble(4, Double.parseDouble(fareField.getText().trim()));
            ps.setInt(5, Integer.parseInt(seatsField.getText().trim()));

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Bus Added Successfully");

            loadBusesFromDB();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateBus() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "UPDATE buses SET route=?, type=?, fare=?, seats=? WHERE bus_no=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, routeField.getText().trim());
            ps.setString(2, typeField.getText().trim());
            ps.setDouble(3, Double.parseDouble(fareField.getText().trim()));
            ps.setInt(4, Integer.parseInt(seatsField.getText().trim()));
            ps.setString(5, busNoField.getText().trim());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Bus Updated Successfully");

            loadBusesFromDB();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteBus() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "DELETE FROM buses WHERE bus_no=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, busNoField.getText().trim());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Bus Deleted Successfully");

            loadBusesFromDB();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadBusesFromDB() {
        try (Connection con = DBConnection.getConnection()) {

            model.setRowCount(0);

            String sql = "SELECT * FROM buses";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("bus_no"),
                        rs.getString("route"),
                        rs.getString("type"),
                        rs.getDouble("fare"),
                        rs.getInt("seats")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading buses: " + e.getMessage());
        }
    }

    // ================= UI HELPERS =================

    private JPanel createCardPanel(String titleText) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(titleText));
        return panel;
    }

    private JPanel createFormRow(String label, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(10, 5));
        row.setBackground(Color.WHITE);
        row.add(new JLabel(label), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private void loadSelectedBus() {
        int row = busTable.getSelectedRow();
        if (row == -1) return;

        busNoField.setText(model.getValueAt(row, 0).toString());
        routeField.setText(model.getValueAt(row, 1).toString());
        typeField.setText(model.getValueAt(row, 2).toString());
        fareField.setText(model.getValueAt(row, 3).toString());
        seatsField.setText(model.getValueAt(row, 4).toString());
    }

    private void clearFields() {
        busNoField.setText("");
        routeField.setText("");
        typeField.setText("");
        fareField.setText("");
        seatsField.setText("");
    }
}
